package com.company;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.*;
import java.net.*;


/**
 * This is the chat client program.
 */
public class Client {

    private final String hostname;
    private final int port;
    private String userName;
    static final Logger clientLogger = Logger.getLogger(Client.class.getName());

    /**
     * Base constructor
     *
     * @param hostname {@link String} - the host name
     * @param port     {@link Integer} - port of the host
     */
    public Client(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        this.userName = "";
    }

    /**
     * Return username
     *
     * @return {@link #userName} String - name of the user
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Return host name
     *
     * @return {@link #hostname} String - name of the host
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Return host port
     *
     * @return {@link #port} String - port of the host
     */
    public int getPort() {
        return port;
    }

    /**
     * Set {@link #userName} to specified name
     *
     * @param userName String - username
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Method which tries to connect with server<br>
     * if succeed it start two threads:<br>
     * {@link ReadThread#ReadThread(Socket, Client, Logger)} - read server input<br>
     * {@link WriteThread#WriteThread(Socket, Client, Logger)} - read user input
     */
    public void execute() {
        try {
            Socket socket = new Socket(hostname, port);

            clientLogger.info("Connected to the chat server");

            new ReadThread(socket, this, clientLogger).start();
            new WriteThread(socket, this, clientLogger).start();

        } catch (UnknownHostException ex) {
            clientLogger.info("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            clientLogger.info("I/O Error: " + ex.getMessage());
        }

    }

    /**
     * Main class making one Client object and starting {@link #execute()} method
     *
     * @param args String - is specified while starting program
     */
    public static void main(String[] args) {

        PropertyConfigurator.configure("./CommonUtil/src/main/resources/log4j.properties");

        String hostname = "localhost";
        int port = 8987;

        Client client = new Client(hostname, port);
        client.execute();
    }
}
