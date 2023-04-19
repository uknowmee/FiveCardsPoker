package com.company;

import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.Assert.*;

/**
 * Class testing ReadThread
 */
public class ReadThreadTest {

    @Test
    public void constructor() throws IOException {
        Client client = new Client("localhost", 2557);

        ServerSocket serverSocket = new ServerSocket(client.getPort());
        Socket socket = new Socket(client.getHostname(), client.getPort());

        ReadThread readThread = new ReadThread(socket, client, Client.clientLogger);
        readThread.start();

        assertEquals(client, readThread.getClient());
        assertEquals(Client.clientLogger, readThread.getRtLogger());
    }
}
