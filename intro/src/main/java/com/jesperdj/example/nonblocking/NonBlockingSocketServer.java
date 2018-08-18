package com.jesperdj.example.nonblocking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

public class NonBlockingSocketServer {
    private static final Logger LOG = LoggerFactory.getLogger(NonBlockingSocketServer.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open()
                .bind(new InetSocketAddress(7000));

        // Asynchronously accept a connection
        serverSocketChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
            @Override
            public void completed(AsynchronousSocketChannel socketChannel, Void attachment) {
                // Asynchronously accept the next connection
                serverSocketChannel.accept(null, this);

                // Handle the accepted connection
                handleConnection(socketChannel);
            }

            @Override
            public void failed(Throwable exception, Void attachment) {
                LOG.error("Error while accepting a connection", exception);
            }
        });

        LOG.info("Non-blocking socket server is running");

        // Let the main thread wait forever
        Thread.currentThread().join();
    }

    private static void handleConnection(AsynchronousSocketChannel socketChannel) {
        try {
            LOG.info("Handling connection from: {}", socketChannel.getRemoteAddress());
        } catch (IOException e) {
            LOG.error("I/O error", e);
        }

        // Allocate a buffer to store received data
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        // Asynchronously receive data
        LOG.info("Start asynchronous read; {} bytes free in buffer", buffer.remaining());
        socketChannel.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer count, ByteBuffer currentBuffer) {
                if (count == -1) {
                    LOG.info("Connection closed");
                } else if (count == 0 && !currentBuffer.hasRemaining()) {
                    // Buffer is full; allocate a larger buffer, copy data from the old buffer
                    int newCapacity = currentBuffer.capacity() * 2;
                    LOG.info("Buffer is full; allocating a new buffer ({} bytes)", newCapacity);
                    ByteBuffer newBuffer = ByteBuffer.allocate(newCapacity);
                    currentBuffer.flip();
                    newBuffer.put(currentBuffer);

                    // Issue another asynchronous read with the new buffer
                    LOG.info("Start asynchronous read; {} bytes free in buffer", newBuffer.remaining());
                    socketChannel.read(newBuffer, newBuffer, this);
                } else {
                    LOG.info("Received {} bytes", count);

                    currentBuffer.flip();
                    decodeMessages(currentBuffer);

                    currentBuffer.compact();
                    LOG.info("Start asynchronous read; {} bytes free in buffer", currentBuffer.remaining());
                    socketChannel.read(currentBuffer, currentBuffer, this);
                }
            }

            private void decodeMessages(ByteBuffer buffer) {
                while (buffer.remaining() >= 2) {
                    int length = buffer.getShort() & 0xFFFF;
                    if (buffer.remaining() >= length) {
                        int lim = buffer.limit();
                        buffer.limit(buffer.position() + length);
                        String text = StandardCharsets.UTF_8.decode(buffer).toString();
                        buffer.limit(lim);

                        LOG.info("Received message: [{}] {}", length, text);
                    } else {
                        // Not a complete message remaining; reset to position of length prefix and stop decoding
                        buffer.position(buffer.position() - 2);
                        break;
                    }
                }
            }

            @Override
            public void failed(Throwable exception, ByteBuffer currentBuffer) {
                LOG.error("Error while receiving data", exception);

                try {
                    socketChannel.close();
                } catch (IOException e) {
                    LOG.error("Error while closing socket channel", e);
                }
            }
        });

        LOG.info("handleConnection finished");
    }
}
