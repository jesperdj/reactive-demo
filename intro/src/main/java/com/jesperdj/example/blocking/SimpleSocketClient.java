package com.jesperdj.example.blocking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SimpleSocketClient {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleSocketClient.class);

    private static final String MESSAGE = "Hello Blocking World";

    public static void main(String[] args) throws IOException {
        LOG.info("Opening connection");

        try (Socket socket = new Socket("localhost", 7000);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8))) {

            LOG.info("Sending message");
            writer.println(MESSAGE);
            writer.flush();

            LOG.info("Receiving response");
            String response = reader.readLine();
            LOG.info("Received: {}", response);
        }

        LOG.info("Finished");
    }
}
