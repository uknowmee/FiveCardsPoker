package com.company;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.*;

/**
 * This thread is responsible for reading server's
 * input and printing it to the user console.
 * It runs in an infinite loop until the client disconnects from the server.
 */
public class ReadThread extends Thread {

    private BufferedReader reader;
    private final Client client;
    private final Logger rtLogger;

    /**
     * Base constructor
     *
     * @param socket {@link Socket} - connected socket
     * @param client {@link Client} - owner of the thread
     * @param logger {@link Logger} - prints server input and log stuff if necessary
     */
    public ReadThread(Socket socket, Client client, Logger logger) {
        this.client = client;
        this.rtLogger = logger;

        try {
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
        } catch (IOException ex) {
            rtLogger.info("Error getting input stream: " + ex.getMessage());
            ex.printStackTrace();
        }
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
     * @return {@link #rtLogger} Logger - owners logger
     */
    public Logger getRtLogger() {
        return rtLogger;
    }

    /**
     * Infinite loop printing server input to user console
     */
    @Override
    public void run() {

        while (true) {
            try {
                String response = reader.readLine();

                if (!client.getUserName().equals("")) {
                    rtLogger.info(response);
                }

            } catch (IOException ex) {
                rtLogger.info("connection ended.");
                break;
            }
        }
        System.exit(0);
    }
}
