package com.company;

import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.Assert.*;

/**
 * Class testing WriteThread
 */
public class WriteThreadTest {

    @Test
    public void constructor() throws IOException {
        Client client = new Client("localhost", 2587);

        ServerSocket serverSocket = new ServerSocket(client.getPort());
        Socket socket = new Socket(client.getHostname(), client.getPort());

        WriteThread writeThread = new WriteThread(socket, client, Client.clientLogger);
        writeThread.start();

        assertEquals(client, writeThread.getClient());
        assertEquals(Client.clientLogger, writeThread.getWtLogger());
        assertEquals(socket, writeThread.getSocket());
    }
}
