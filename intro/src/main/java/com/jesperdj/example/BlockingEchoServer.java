package com.jesperdj.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class BlockingEchoServer {
    private static final Logger LOG = LoggerFactory.getLogger(BlockingEchoServer.class);

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(7000);

        while (true) {
            LOG.info("Accepting a connection...");
            Socket socket = serverSocket.accept();

            ConnectionHandler connectionHandler = new ConnectionHandler(socket);
            Thread thread = new Thread(connectionHandler);
            thread.start();
        }
    }

    private static class ConnectionHandler implements Runnable {
        private final Socket socket;

        private ConnectionHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            LOG.info("Handling connection from: {}", socket.getRemoteSocketAddress());

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {

                String line;
                while ((line = reader.readLine()) != null) {
                    LOG.info("Received: {}", line);
                    writer.println(line);
                    writer.flush();
                }
            } catch (IOException e) {
                LOG.error("I/O error", e);
            }

            LOG.info("ConnectionHandler finished");
        }
    }
}
