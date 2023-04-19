package com.company;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.Assert.*;

/**
 * Class testing ServerThread
 */
public class ServerThreadTest {
    int port = 1000;
    private Server server;

    private ServerThread serverThread;
    private ServerSocket serverSocket;

    private String name;
    private UserThread newUser;
    private Socket socket;
    private String user;

    private String name1;
    private UserThread newUser1;
    private Socket socket1;
    private String user1;

    private String name2;
    private UserThread newUser2;
    private Socket socket2;
    private String user2;

    private String name3;
    private UserThread newUser3;
    private Socket socket3;
    private String user3;

    @Before
    public void setUp() throws IOException {
        String inDeck = ", in deck: ";

        this.port = 2001;
        this.server = new Server(port);
        this.serverThread = new ServerThread(Server.serverLogger, server);

        RawConnectionTest rawConnectionTest = new RawConnectionTest(port);
        this.serverSocket = new ServerSocket(port);
        rawConnectionTest.start();

        this.socket = serverSocket.accept();
        this.name = "michal";
        this.newUser = new UserThread(socket, server, Server.serverLogger);
        this.serverThread = new ServerThread(Server.serverLogger, server);
        OutputStream output = socket.getOutputStream();
        this.newUser.setWriter(new PrintWriter(output, true));
        server.addUserThread(newUser);
        server.addUser(newUser);
        server.addUserName(name, newUser);

        this.socket1 = serverSocket.accept();
        this.name1 = "wojtek";
        this.newUser1 = new UserThread(socket1, server, Server.serverLogger);
        OutputStream output1 = socket.getOutputStream();
        newUser1.setWriter(new PrintWriter(output1, true));
        server.addUserThread(newUser1);
        server.addUser(newUser1);
        server.addUserName(name1, newUser1);

        this.socket2 = serverSocket.accept();

        this.name2 = "ola";
        this.newUser2 = new UserThread(socket2, server, Server.serverLogger);
        OutputStream output2 = socket.getOutputStream();
        newUser2.setWriter(new PrintWriter(output2, true));
        server.addUserThread(newUser2);
        server.addUser(newUser2);
        server.addUserName(name2, newUser2);

        this.socket3 = serverSocket.accept();
        this.name3 = "kacper";
        this.newUser3 = new UserThread(socket3, server, Server.serverLogger);
        OutputStream output3 = socket.getOutputStream();
        newUser3.setWriter(new PrintWriter(output3, true));
        server.addUserThread(newUser3);
        server.addUser(newUser3);
        server.addUserName(name3, newUser3);

        this.user = name + inDeck + "";
        this.user1 = name1 + inDeck + "";
        this.user2 = name2 + inDeck + "";
        this.user3 = name3 + inDeck + "";
    }

    @After
    public void tearDown() throws IOException {
        server.removeUser(name, newUser);
        server.removeUser(name1, newUser1);
        server.removeUser(name2, newUser2);
        server.removeUser(name3, newUser3);

        socket.close();
        socket1.close();
        socket2.close();
        socket3.close();

        serverSocket.close();
    }

    @Test
    public void constructor() {
        assertEquals(Server.serverLogger, serverThread.getStLogger());
        assertEquals(server, serverThread.getServer());
        assertFalse(serverThread.isIfClose());
    }

    @Test
    public void actionHelp() {
        assertEquals("""
                ###########################################################
                commands:\s
                \\help - print all commands
                \\showusers - print all users
                \\showdecks - print all running decks
                \\msgall - msg all connected users
                \\<username> - msg specified user
                \\info - show in game info about current players (if in game)
                \\CLOSE - exit
                ###########################################################""", serverThread.action(new Server.Split("\\help")));
    }

    @Test
    public void actionShowUsersEmpty() {
        server.removeUser(name, newUser);
        server.removeUser(name1, newUser1);
        server.removeUser(name2, newUser2);
        server.removeUser(name3, newUser3);
        assertEquals("[]", serverThread.action(new Server.Split("\\showusers")));
    }

    @Test
    public void actionShowUsersOne() {
        String inDeck = ", in deck: ";

        server.removeUser(name1, newUser1);
        server.removeUser(name2, newUser2);
        server.removeUser(name3, newUser3);

        assertEquals("[" + name + inDeck + "" + ", " + newUser + "]",
                serverThread.action(new Server.Split("\\showusers")));
    }

    @Test
    public void actionShowUsersFew() {
        assertTrue(serverThread.action(new Server.Split("\\showusers")).contains(user));
        assertTrue(serverThread.action(new Server.Split("\\showusers")).contains(user1));
        assertTrue(serverThread.action(new Server.Split("\\showusers")).contains(user2));
    }

    @Test
    public void actionInfo() throws InterruptedException {
        newUser.userAction(name, "\\adddeck first 2");
        newUser1.userAction(name1, "\\joindeck first");

        GameStartThread gameStartThread = new GameStartThread();
        GameStartedThread gameStartedThread = new GameStartedThread();

        GameStartThread.gameStart();

        assertEquals("messaged all", serverThread.action(new Server.Split("\\info")));
    }

    @Test
    public void actionInfoNotRunning() throws InterruptedException {
        newUser.userAction(name, "\\adddeck first 2");

        GameStartThread gameStartThread = new GameStartThread();
        GameStartedThread gameStartedThread = new GameStartedThread();

        GameStartThread.gameStart();

        assertEquals("messaged all", serverThread.action(new Server.Split("\\info")));
    }

    @Test
    public void showDecks() {
        newUser.userAction(name, "\\adddeck first 2");
        newUser1.userAction(name1, "\\joindeck first");

        newUser2.userAction(name2, "\\adddeck second 3");

        newUser3.userAction(name3, "\\adddeck third 3");

        String answer = """
                Deck named: first, with maximum of: 2 players
                \tdecks players:
                \t\tmichal
                \t\twojtek
                """;

        String answer1 = """
                Deck named: second, with maximum of: 3 players
                \tdecks players:
                \t\tola
                """;

        String answer2 = """
                Deck named: third, with maximum of: 3 players
                \tdecks players:
                \t\tkacper
                """;

        String decks = serverThread.action(new Server.Split("\\showdecks"));

        assertTrue(decks.contains(answer));
        assertTrue(decks.contains(answer1));
        assertTrue(decks.contains(answer2));
    }

    @Test
    public void msgAll() {
        assertEquals("messaged all", serverThread.action(new Server.Split("\\msgall czesc")));
        assertEquals("messaged all", serverThread.action(new Server.Split("\\msgall")));
    }

    @Test
    public void defaultAction() {
        assertEquals("unknown command!", serverThread.action(new Server.Split("\\dasda")));
        assertEquals("[SERVER]: siema", serverThread.action(new Server.Split("\\michal siema")));
    }
}
