package com.jesperdj.example.nonblocking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

public class NonBlockingSocketClient {
    private static final Logger LOG = LoggerFactory.getLogger(NonBlockingSocketClient.class);

    private static final String MESSAGE = "Hello Non-blocking World";

    public static void main(String[] args) throws InterruptedException, IOException {
        AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();

        LOG.info("Start asynchronous connect");
        socketChannel.connect(new InetSocketAddress("localhost", 7000), null,
                new CompletionHandler<Void, Void>() {
                    @Override
                    public void completed(Void result, Void attachment) {
                        LOG.info("Connection opened");

                        byte[] message = MESSAGE.getBytes(StandardCharsets.UTF_8);

                        ByteBuffer buffer = ByteBuffer.allocate(1024);
                        buffer.putShort((short) message.length);
                        buffer.put(message);
                        buffer.flip();

                        LOG.info("Start asynchronous write: {} bytes remaining", buffer.remaining());
                        socketChannel.write(buffer, null, new CompletionHandler<Integer, Void>() {
                            @Override
                            public void completed(Integer count, Void attachment) {
                                LOG.info("Wrote {} bytes", count);

                                if (buffer.hasRemaining()) {
                                    LOG.info("Start asynchronous write: {} bytes remaining", buffer.remaining());
                                    socketChannel.write(buffer, null, this);
                                } else {
                                    LOG.info("Finished writing");
                                }
                            }

                            @Override
                            public void failed(Throwable exception, Void attachment) {
                                LOG.error("I/O error while writing bytes", exception);
                            }
                        });
                    }

                    @Override
                    public void failed(Throwable exception, Void attachment) {
                        LOG.error("Error while opening connection", exception);
                    }
                });

        LOG.info("Connect issued");

        Thread.sleep(10000L);
    }
}
