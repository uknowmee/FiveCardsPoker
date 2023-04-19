package com.company;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

import static org.junit.Assert.*;

/**
 * Class testing UserThread
 */
public class UserThreadTest {
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

        Server.decks.clear();
    }

    @Test
    public void constructor() {
        assertEquals(Server.serverLogger, newUser.getUtLogger());
        assertEquals(server, newUser.getServer());
        assertEquals(socket, newUser.getSocket());
    }

    @Test
    public void actionHelp() {
        assertEquals("""
                ###########################################################
                commands:\s
                \\help - print all commands
                \\showusers - print all users
                \\showdecks - print all running decks
                \\adddeck <nameOfDeck> <numberOfPlayers>
                \\joindeck <nameOfDeck>
                \\leavedeck
                \\msgall - msg all connected users
                \\<username> - msg specified user
                \\info - show in game info about current player (if in game)
                \\fold - ONLY IN GAME use it if u want to pass
                \\check - ONLY IN GAME use it if u want to check a player
                \\call - ONLY IN GAME use it if u want to call someones bet
                \\bet <money> - ONLY IN GAME use it if u want to bet
                \\raise <money> - ONLY IN GAME use it if u want to re-bet
                \\all - ONLY IN GAME use it if u cant call
                \\cya - ONLY IN GAME use it after u went down with money
                \\exchange <cardNum cardNum> - ONLY IN GAME use it to exchange cards
                \\bye - exit
                ###########################################################
                """, newUser.userAction(newUser.getName(), "\\help"));
    }

    @Test
    public void actionShowUsersEmpty() {
        server.removeUser(name, newUser);
        server.removeUser(name1, newUser1);
        server.removeUser(name2, newUser2);
        server.removeUser(name3, newUser3);
        assertEquals("[]", newUser.userAction(name, "\\showusers"));
    }

    @Test
    public void actionShowUsersOne() {
        String inDeck = ", in deck: ";

        server.removeUser(name1, newUser1);
        server.removeUser(name2, newUser2);
        server.removeUser(name3, newUser3);

        assertEquals("[" + name + inDeck + "]",
                newUser.userAction(newUser.getName(), "\\showusers"));
    }

    @Test
    public void actionShowUsersFew() {
        assertTrue(newUser.userAction(name, "\\showusers").contains(user));
        assertTrue(newUser1.userAction(name1, "\\showusers").contains(user1));
        assertTrue(newUser2.userAction(name2, "\\showusers").contains(user2));
    }

    @Test
    public void actionShowDecks() {
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

        String decks = newUser.userAction(name, "\\showdecks");

        assertTrue(decks.contains(answer));
        assertTrue(decks.contains(answer1));
        assertTrue(decks.contains(answer2));

        server.removeDeck(Objects.requireNonNull(Server.getUserFromName(name)).getDeck());
    }

    @Test
    public void defaultActionUnknownCommand() {
        assertEquals("unknown command!", newUser.userAction(name, "\\" + name));
        assertEquals("unknown command!", newUser.userAction(name, "asd"));
        assertEquals("unknown command!", newUser.userAction(name, "asddasdgswf"));
    }

    @Test
    public void defaultActionOnlyOneUser() {
        server.removeUser(name1, newUser1);
        server.removeUser(name2, newUser2);
        server.removeUser(name3, newUser3);

        assertEquals("unknown command!", newUser.userAction(name, "\\asda asd"));
        assertEquals("unknown command!", newUser.userAction(name, "\\asd"));
    }

    @Test
    public void defaultActionWhisper() {
        assertEquals(name + ": " + "asdasd", newUser.userAction(name, "\\" + name1 + " asdasd"));
    }

    @Test
    public void actionAddDeck() {
        assertEquals("Invalid deck name or number of players", newUser.userAction(name, "\\adddeck hihi "));

        assertEquals("You have created a deck named: hihi for: 3 players", newUser.userAction(name, "\\adddeck hihi 3"));
        assertEquals("unknown command!", newUser.userAction(name, "\\adddeck hihi 3"));
        assertEquals("unknown command!", newUser.userAction(name, "\\addDeck hihi 2"));
        assertEquals("unknown command!", newUser.userAction(name, "\\addDeck hihi"));

        assertEquals("unknown command!", newUser1.userAction(name1, "\\adddeck hihi 2"));
        assertEquals("unknown command!", newUser1.userAction(name1, "\\joindeck"));
        assertEquals("You have joined a deck named: hihi", newUser1.userAction(name1, "\\joindeck hihi"));

        assertEquals("You have left a deck named: hihi", newUser1.userAction(name1, "\\leavedeck"));

        assertEquals("You have joined a deck named: hihi", newUser1.userAction(name1, "\\joindeck hihi"));
        assertEquals("you already are in deck", newUser1.userAction(name1, "\\joindeck hihi"));
        assertEquals("You have joined a deck named: hihi", newUser2.userAction(name2, "\\joindeck hihi"));
        assertEquals("unknown command!", newUser3.userAction(name3, "\\joindeck hihi"));

        assertEquals("", newUser.ping());

        assertEquals("unknown command!", newUser3.userAction(name3, "\\adddeck hihi"));
        assertEquals("You have created a deck named: hihig for: 3 players", newUser3.userAction(name3, "\\adddeck hihig 3"));
        assertEquals(2, Server.getDecks().size());
        assertEquals("You have left a deck named: hihig", newUser3.userAction(name3, "\\leavedeck"));
        assertEquals("you are already not in deck", newUser3.userAction(name3, "\\leavedeck"));

        assertEquals("unknown command!", newUser.userAction(name, "\\leavedeck"));

        assertEquals("unknown command!", newUser3.userAction(name3, "\\adddeck hihi 2"));

        assertEquals(1, Server.getDecks().size());
    }

    @Test
    public void gameStart() throws IOException, InterruptedException {
        newUser.userAction(name, "\\adddeck moj 2");
        newUser1.userAction(name1, "\\joindeck moj");

        GameStartThread gameStartThread = new GameStartThread();
        GameStartedThread gameStartedThread = new GameStartedThread();

        assertEquals("game has started!", GameStartThread.gameStart());
        assertEquals(
                Objects.requireNonNull(Server.getUserFromName(name)).getDeck().getResponseString(),
                GameStartedThread.gameStarted(System.currentTimeMillis() + 15 * 1001));

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
    public void add2add4() {
        assertEquals("You have created a deck named: hihii for: 2 players", newUser3.userAction(name3, "\\adddeck hihii 2"));
        assertEquals("You have left a deck named: hihii", newUser3.userAction(name3, "\\leavedeck"));
        assertEquals("You have created a deck named: hihii for: 4 players", newUser3.userAction(name3, "\\adddeck hihii 4"));
        assertEquals("You have left a deck named: hihii", newUser3.userAction(name3, "\\leavedeck"));
    }

    @Test
    public void socketClose() throws IOException {

        assertEquals("You have created a deck named: mam for: 3 players", newUser.userAction(name, "\\adddeck mam 3"));
        assertEquals("You have joined a deck named: mam", newUser1.userAction(name1, "\\joindeck mam"));
        assertEquals("You have joined a deck named: mam", newUser2.userAction(name2, "\\joindeck mam"));

        server.removeUser(name, newUser);
        socket.close();

        String serverMessage = name + " has quit.";
        server.broadcast(serverMessage, newUser);

        assertEquals(name + " has left server, game mam is closing.", newUser.ping());
        assertEquals(0, Server.getDecks().size());
    }

    @Test
    public void actionMsgAll() {
        assertEquals("messaged all", newUser.userAction(name, "\\msgall hello everybody"));
    }

    @Test
    public void mainAction1() {
        newUser.userAction(name, "\\adddeck hihi 3");
        newUser1.userAction(name1, "\\joindeck hihi");
        newUser2.userAction(name2, "\\joindeck hihi");

        assertEquals("send from deck", newUser.action(name, "\\info"));

        newUser.userAction(name, "\\leavedeck");
        newUser1.userAction(name1, "\\leavedeck");
        newUser2.userAction(name2, "\\leavedeck");
    }

    @Test
    public void mainAction2() throws InterruptedException {
        newUser.userAction(name, "\\adddeck hihi 3");
        newUser1.userAction(name1, "\\joindeck hihi");

        assertEquals("", GameStartThread.gameStart());

        assertEquals("send from deck", newUser.action(name, "\\info"));

        newUser.userAction(name, "\\leavedeck");
        newUser1.userAction(name1, "\\leavedeck");
    }

    @Test
    public void mainAction3() throws InterruptedException {
        newUser1.userAction(name1, "\\joindeck hihi");

        assertEquals("", GameStartThread.gameStart());

        assertEquals("send from no existing deck", newUser.action(name, "\\info"));

        newUser1.userAction(name1, "\\leavedeck");
    }

    @Test
    public void for3percent() {
        Deck deck = new Deck("letsTest", "michal", 3);
        deck.playerJoin("wojtek");
        deck.playerJoin("ola");
        deck.startResponse();

        deck.updateResponse("michal", "\\check");
        deck.updateResponse("wojtek", "\\check");
        deck.updateResponse("ola", "\\check");

        deck.updateResponse("michal", "\\exchange");
        deck.updateResponse("wojtek", "\\exchange");
        deck.updateResponse("ola", "\\exchange");


        deck.setPlayersCredit("wojtek", -2);

        deck.updateResponse("michal", "\\check");
        deck.updateResponse("wojtek", "\\cya");
        deck.updateResponse("ola", "\\check");

        assertEquals("message sent", newUser.anyWinner(deck));

    }

    @Test
    public void for3percentV2() {

        server.addDeck("letsTest", newUser, 3);

        Deck deck = new Deck("letsTest", "michal", 3);
        deck.playerJoin("wojtek");
        deck.playerJoin("ola");
        deck.startResponse();

        deck.updateResponse("michal", "\\check");
        deck.updateResponse("wojtek", "\\check");
        deck.updateResponse("ola", "\\check");

        deck.updateResponse("michal", "\\exchange");
        deck.updateResponse("wojtek", "\\exchange");
        deck.updateResponse("ola", "\\exchange");


        deck.setPlayersCredit("wojtek", -2);

        deck.updateResponse("michal", "\\check");
        deck.updateResponse("wojtek", "\\cya");
        deck.updateResponse("ola", "\\check");

//        newUser.sendMessage("michal", deck.getResponse());

        assertEquals(deck.getResponse(), newUser.sendMessage("michal", deck.getResponse(), deck));

    }
}
