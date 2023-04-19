package com.company;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Class testing Client
 */
public class ClientTest {

    @Test
    public void constructor() {
        String hostname = "localhost";
        int port = 8987;

        Client client = new Client(hostname, port);

        assertEquals(port, client.getPort());
        assertEquals(hostname, client.getHostname());
        assertEquals("", client.getUserName());
    }

    @Test
    public void getUserName() {
        String hostname = "localhost";
        int port = 8987;

        Client client = new Client(hostname, port);
        client.setUserName("Michal");

        assertEquals("Michal", client.getUserName());
    }

    @Test
    public void getHostName() {
        String hostname = "localhost";
        int port = 8987;

        Client client = new Client(hostname, port);

        assertEquals(hostname, client.getHostname());
    }

    @Test
    public void getPort() {
        String hostname = "localhost";
        int port = 8987;

        Client client = new Client(hostname, port);

        assertEquals(port, client.getPort());
    }

    @Test
    public void setUserName() {
        String hostname = "localhost";
        int port = 8987;

        Client client = new Client(hostname, port);
        client.setUserName("Michal");

        assertEquals("Michal", client.getUserName());
    }
}
