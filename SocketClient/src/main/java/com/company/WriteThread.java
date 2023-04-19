package com.company;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.*;

/**
 * This thread is responsible for reading user's
 * input and send it to the server.
 * It runs in an infinite loop until the user types 'bye' to quit.
 */
public class WriteThread extends Thread {

    private PrintWriter writer;
    private final Socket socket;
    private final Client client;
    private final Logger wtLogger;

    /**
     * Base constructor
     *
     * @param socket {@link Socket} - connected socket
     * @param client {@link Client} - owner of the thread
     * @param logger {@link Logger} - prints server input and log stuff if necessary
     */
    public WriteThread(Socket socket, Client client, Logger logger) {
        this.socket = socket;
        this.client = client;
        this.wtLogger = logger;

        try {
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
        } catch (IOException ex) {
            wtLogger.info("Error getting output stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }

    /**
     * Return owner of the reader (client)
     *
     * @return {@link #client} Client - owner of the reader
     */
    public Client getClient() {
        return client;
    }

    /**
     * Return owners logger
     *
     * @return {@link #wtLogger} Logger - owners logger
     */
    public Logger getWtLogger() {
        return wtLogger;
    }

    /**
     * Infinite loop writing the user input to server console
     */
    @Override
    public void run() {

        Console console = System.console();

        String userName = console.readLine("\nEnter your name: ");
        client.setUserName(userName);
        writer.println(userName);

        String text;

        do {
            text = console.readLine();
            text = text.replace("\n", "").replace("\r", "");
            writer.println(text);

        } while (!text.equals("\\bye"));

        try {
            socket.close();
        } catch (IOException ex) {

            wtLogger.info("connection ended");

        }
    }
}
