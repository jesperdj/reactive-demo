package com.jesperdj.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

public class NonBlockingEchoServer {
    private static final Logger LOG = LoggerFactory.getLogger(NonBlockingEchoServer.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        AsynchronousServerSocketChannel serverSocketChannel = AsynchronousServerSocketChannel.open()
                .bind(new InetSocketAddress(7000));

        // Start asynchronous accept operation
        LOG.info("Start accept operation");
        serverSocketChannel.accept(null, new ConnectionHandler(serverSocketChannel));

        LOG.info("Server is running");

        // Let the main thread wait forever
        Thread.currentThread().join();
    }

    private static class ConnectionHandler implements CompletionHandler<AsynchronousSocketChannel, Void> {
        private final AsynchronousServerSocketChannel serverSocketChannel;

        private ConnectionHandler(AsynchronousServerSocketChannel serverSocketChannel) {
            this.serverSocketChannel = serverSocketChannel;
        }

        @Override
        public void completed(AsynchronousSocketChannel socketChannel, Void attachment) {
            // Start another asynchronous accept operation
            LOG.info("Start accept operation");
            serverSocketChannel.accept(null, this);

            // Allocate a buffer to store received bytes
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            // Start asynchronous read operation
            LOG.info("Start read operation; {} bytes free", buffer.remaining());
            socketChannel.read(buffer, buffer, new ReadHandler(socketChannel));
        }

        @Override
        public void failed(Throwable exception, Void attachment) {
            LOG.error("Error while accepting connection", exception);
        }
    }

    private static class ReadHandler implements CompletionHandler<Integer, ByteBuffer> {
        private final AsynchronousSocketChannel socketChannel;

        private ReadHandler(AsynchronousSocketChannel socketChannel) {
            this.socketChannel = socketChannel;
        }

        @Override
        public void completed(Integer count, ByteBuffer buffer) {
            if (count == -1) {
                LOG.info("Connection closed");
            } else if (count == 0 && !buffer.hasRemaining()) {
                int newCapacity = buffer.capacity() * 2;
                LOG.info("Buffer is full; allocating a new buffer of {} bytes", newCapacity);
                ByteBuffer newBuffer = ByteBuffer.allocate(newCapacity);
                buffer.flip();
                newBuffer.put(buffer);

                LOG.info("Start read operation; {} bytes free", newBuffer.remaining());
                socketChannel.read(newBuffer, newBuffer, this);
            } else {
                LOG.info("Read {} bytes", count);

                buffer.flip();

                LOG.info("Start write operation; {} bytes remaining", buffer.remaining());
                socketChannel.write(buffer, buffer, new WriteHandler(socketChannel));

                ByteBuffer newBuffer = ByteBuffer.allocate(1024);
                LOG.info("Start read operation; {} bytes free", newBuffer.remaining());
                socketChannel.read(newBuffer, newBuffer, this);
            }
        }

        @Override
        public void failed(Throwable exception, ByteBuffer buffer) {
            LOG.error("Error while reading", exception);
        }
    }

    private static class WriteHandler implements CompletionHandler<Integer, ByteBuffer> {
        private final AsynchronousSocketChannel socketChannel;

        private WriteHandler(AsynchronousSocketChannel socketChannel) {
            this.socketChannel = socketChannel;
        }

        @Override
        public void completed(Integer count, ByteBuffer buffer) {
            LOG.info("Wrote {} bytes", count);

            if (buffer.hasRemaining()) {
                LOG.info("Start write operation; {} bytes remaining", buffer.remaining());
                socketChannel.write(buffer, buffer, this);
            }
        }

        @Override
        public void failed(Throwable exception, ByteBuffer buffer) {
            LOG.error("Error while writing", exception);
        }
    }
}
