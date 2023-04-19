package com.company;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Class testing Server
 */
public class ServerTest {

    @Test
    public void constructor() {
        int port = 1000;
        Server server = new Server(port);

        assertEquals(port, server.getPort());
    }

    @Test
    public void userConstructor() {
        UserThread newUser = new UserThread(null, null, null);
        Server.User user = new Server.User(newUser, "michal");

        assertEquals("michal", user.getUserName());
        assertEquals("\\", user.getUserCommandName());
        assertEquals(newUser, user.getUserThread());
    }

    @Test
    public void splitConstructor() {
        Server.Split split = new Server.Split("\\michal hey guys how are you");

        assertEquals("hey guys how are you", split.getMessage());
        assertEquals("\\michal", split.getCommand());
    }

    @Test
    public void addUser() {
        int port = 1001;
        Server server = new Server(port);

        String name = "michal";
        UserThread newUser = new UserThread(null, server, Server.serverLogger);

        server.addUserThread(newUser);
        server.addUser(newUser);
        server.addUserName(name, newUser);

        Set<String> names = new HashSet<>();
        Set<UserThread> userThreads = new HashSet<>();

        names.add(name);
        userThreads.add(newUser);

        assertEquals(names, server.getUserNames());
        for (Server.User us : Server.getUsers()) {
            assertEquals("michal", us.getUserName());
            assertEquals(newUser, us.getUserThread());
            assertEquals("\\michal", us.getUserCommandName());
        }
        assertEquals(userThreads, server.getUserThreads());

        server.removeUser(name, newUser);
    }

    @Test
    public void getUserFromNameEmpty() {
        assertNull(Server.getUserFromName(""));
    }

    @Test
    public void writeToUser() {
        int port = 1001;
        Server server = new Server(port);

        assertFalse(Server.writeToUser("sad", null));
    }

    @Test
    public void removeUser() {
        int port = 1002;
        Server server = new Server(port);

        String name = "michal";
        UserThread newUser = new UserThread(null, server, Server.serverLogger);

        server.addUserThread(newUser);
        server.addUser(newUser);
        server.addUserName(name, newUser);

        server.removeUser(name, newUser);

        assertEquals(0, server.getUserThreads().size());
        assertEquals(0, Server.getUsers().size());
        assertEquals(0, server.getUserNames().size());
    }

    @Test
    public void removeUserNotExist() {
        int port = 1002;
        Server server = new Server(port);

        String name = "michal";
        UserThread newUser = new UserThread(null, server, Server.serverLogger);

        server.addUserThread(newUser);
        server.addUser(newUser);
        server.addUserName(name, newUser);

        server.removeUser("asd", newUser);

        assertEquals(1, server.getUserThreads().size());
        assertEquals(1, Server.getUsers().size());
        assertEquals(1, server.getUserNames().size());

        server.removeUser("michal", newUser);
    }

    @Test
    public void getServerPort() {
        int port = 1003;
        Server server = new Server(port);

        assertEquals(port, server.getPort());
    }
}
