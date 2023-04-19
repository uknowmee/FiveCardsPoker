package com.company;

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * This thread handles connection for each connected client, so the server
 * can handle multiple clients at the same time.
 */
public class UserThread extends Thread {
    private final Server server;
    private final Socket socket;
    private String nm = "";
    private PrintWriter writer;
    private final Logger utLogger;
    private static final String UNKNOWN_COMMAND = "unknown command!";
    private static final String PLAYERS = " players";

    /**
     * Base constructor
     *
     * @param socket {@link Socket} - socket of connection
     * @param logger {@link Logger} - prints server input and log stuff if necessary
     * @param server {@link Server} - server of which console will be handled
     */
    public UserThread(Socket socket, Server server, Logger logger) {
        this.socket = socket;
        this.server = server;
        this.utLogger = logger;
    }

    /**
     * Return owner
     *
     * @return {@link Server} Server - server which server thread is managing
     */
    public Server getServer() {
        return server;
    }

    /**
     * Return connection socket
     *
     * @return {@link #socket} Socket - socket of connection with client
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * Return owners logger
     *
     * @return {@link #utLogger} Logger - owners logger
     */
    public Logger getUtLogger() {
        return utLogger;
    }

    /**
     * Method used in tests, shouldn't be used often
     *
     * @param writer PrintWriter - userThread can write using it to ReadThread
     */
    public void setWriter(PrintWriter writer) {
        this.writer = writer;
    }

    /**
     * Sends a message to the client.
     *
     * @param message String - message to client
     * @return message String - message to client
     */
    public String sendMessage(String message) {
        writer.println(message);
        return message;
    }

    /**
     *
     * @param deck checks in certain deck if there are any winners
     * @return if successfully sent a message
     */
    public String anyWinner(Deck deck) {

        String message;
        for (String plName : deck.getResponse().getPlayingNames()) {
            for (String userName : deck.getResponse().getWinner()) {
                message = userName + " won round with:\n\tCards: \n" + deck.getPlayingCardsFromName(userName);
                Server.writeToUser(message + "[NEW ROUND IS STARTING]\n", Objects.requireNonNull(Server.getUserFromName(plName)).getUserThread());
            }
        }

        deck.gameReset();

        return "message sent";
    }

    /**
     * Sends messages to client / clients based on current {@link Deck.Response}
     *
     * @param userName String - name of user who made a move
     * @param response - {@link Deck.Response} - current {@link Deck} response
     * @return {@link Deck.Response} - current {@link Deck} response
     */
    public Deck.Response sendMessage(String userName, Deck.Response response, Deck deck) {

        String message = response +
                ",\n\tbank: " + deck.getBank() +
                ", bid: " + deck.getBid() +
                ", last tried: " + response.getLastTried() + "\n";

        if (Boolean.TRUE.equals(response.getMoveAccepted())) {

            for (String plName : response.getPlayingNames()) {
                Server.writeToUser(message, Objects.requireNonNull(Server.getUserFromName(plName)).getUserThread());
            }

            try{
                if (!deck.getResponse().getWinner().isEmpty()) {
                    anyWinner(deck);
                }
            }catch (Exception ignored){
                //no winner
            }

            if (response.getLastTried().contains("\\cya")) {

                Server.userChangeDeck(userName, "", false);

                if (deck.getResponse().getPlaying().size() == 1) {

                    Server.writeToUser("you have won WHOLE GAME!",
                            Objects.requireNonNull(Server.getUserFromName(deck.getPlayingNames().get(0))).getUserThread());

                    Server.userChangeDeck(deck.getPlayingNames().get(0), "", false);
                    server.removeDeck(deck);
                }
            }
        }
        else {
            sendMessage(message);
        }

        return response;
    }

    /**
     * Print commands to server console and return it
     */
    public String showCommands() {
        return """
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
                """;
    }

    /**
     * Returns all users in 1 string after printing them to user
     *
     * @return ret String - all users
     */
    public String showUsers() {
        String usersString = "";
        Set<Server.User> users = Server.getUsers();
        String inDeck = ", in deck: ";

        if (users.isEmpty()) {
            usersString = "[]";
        }
        else if (users.size() == 1) {
            for (Server.User us : users) {
                usersString = "[" + us.getUserName() + inDeck + us.getDeckName() + "]";
            }
        }
        else {
            int i = 0;
            for (Server.User us : users) {
                if (i == 0) {
                    usersString = usersString.concat("[" + us.getUserName() + inDeck + us.getDeckName() + "\n");
                    i++;
                }
                else if (i == users.size() - 1) {
                    usersString = usersString.concat(us.getUserName() + inDeck + us.getDeckName() + "]");
                }
                else {
                    usersString = usersString.concat(us.getUserName() + inDeck + us.getDeckName() + "\n");
                    i++;
                }
            }
        }

        return usersString;
    }

    /**
     * Returns all deck in 1 string after printing them to user
     *
     * @return ret String - all decks
     */
    public String showDecks() {
        String decks = "";

        for (Deck deck : Server.getDecks()) {
            decks = decks.concat(deck.toString() + "\n");
        }

        return decks;
    }

    /**
     * Add a deck to server list of decks
     *
     * @param userName String - name of deck creator
     * @param split    Split - object containing split message of client
     * @return String - result of adding deck
     */
    public String addDeck(String userName, Server.Split split) {
        if (Objects.requireNonNull(Server.getUserFromName(userName)).getInDeck().equals(false)) {

            Server.Split split1 = new Server.Split(split.getMessage());
            String name = split1.getCommand();
            String numOfPlayers = split1.getMessage();

            for (Deck deck : Server.getDecks()) {
                if (deck.getName().equals(name)) {
                    return UNKNOWN_COMMAND;
                }
            }

            String ret = "You have created a deck named: " + name + " for: ";
            switch (numOfPlayers) {
                case "2":
                    server.addDeck(name, this, 2);
                    Server.userChangeDeck(userName, name, true);
                    return ret + 2 + PLAYERS;
                case "3":
                    server.addDeck(name, this, 3);
                    Server.userChangeDeck(userName, name, true);
                    return ret + 3 + PLAYERS;
                case "4":
                    server.addDeck(name, this, 4);
                    Server.userChangeDeck(userName, name, true);
                    return ret + 4 + PLAYERS;
                default:
                    return "Invalid deck name or number of players";
            }
        }
        else {
            return UNKNOWN_COMMAND;
        }
    }

    /**
     * With this method a user can join the deck
     *
     * @param userName String - username of a user
     * @param split    Split - object containing full message from user
     * @return ret Command - returns result of a joining process
     */
    public String joinDeck(String userName, Server.Split split) {
        if (Objects.requireNonNull(Server.getUserFromName(userName)).getInDeck().equals(false)) {

            for (Deck deck : Server.getDecks()) {
                if (deck.getName().equals(split.getMessage()) &&
                        deck.getNumOfPlayers() > deck.getPlayers().size()) {
                    deck.playerJoin(userName);
                    Server.userChangeDeck(userName, split.getMessage(), true);
                    return "You have joined a deck named: ".concat(deck.getName());
                }
            }
            return UNKNOWN_COMMAND;
        }
        else {
            return "you already are in deck";
        }
    }

    /**
     * With this method an UserThread class object can leave a deck
     *
     * @param userName String - name of a person who is leaving deck
     * @param split    Split - already Split message
     * @return message String - based by method behaviour it returns its summary
     */
    public String leaveDeck(String userName, Server.Split split) {
        if (Objects.requireNonNull(Server.getUserFromName(userName)).getInDeck().equals(true)) {

            for (Deck deck : Server.getDecks()) {
                if (deck.getPlayerNames().contains(userName) && deck.getPlayerNames().size() != deck.getNumOfPlayers()) {
                    if (deck.getPlayers().size() > 1) {
                        deck.playerLeave(userName);
                        Server.userChangeDeck(userName, split.getMessage(), false);
                    }
                    else {
                        Server.userChangeDeck(userName, split.getMessage(), false);
                        server.removeDeck(deck);
                    }
                    return "You have left a deck named: ".concat(deck.getName());
                }
            }
            return UNKNOWN_COMMAND;
        }
        else {
            return "you are already not in deck";
        }
    }

    /**
     * @param userName user which want to pop an info
     * @return his message
     */
    public String info(String userName) {
        String message;

        if (Objects.requireNonNull(Server.getUserFromName(userName)).getInDeck().equals(true)) {
            for (Deck deck : Server.getDecks()) {
                if (deck.getPlayerNames().contains(userName) && deck.isStarted()) {
                    message = deck.getResponse() +
                            ",\n\tbank: " + deck.getBank() +
                            ", bid: " + deck.getBid() + "\n" +
                            "NAME: " + userName + "\n" +
                            "credit: " + deck.getPlayerCreditFromName(userName) + "\n" +
                            "Cards: \n" + deck.getPlayingCardsFromName(userName);
                    return message;
                }
            }
        }
        return UNKNOWN_COMMAND;
    }

    /**
     * Handles default user action
     *
     * @param split    Split - client message container
     * @param userName - String client name
     * @return message String - based by method behaviour it returns its summary
     */
    public String defaultAction(Server.Split split, String userName) {
        if (split.getCommand().equals("\\".concat(userName))) {
            Server.writeToUser(UNKNOWN_COMMAND, this);
            return UNKNOWN_COMMAND;
        }
        else {
            for (Server.User user : Server.getUsers()) {
                if (user.getUserCommandName().equals(split.getCommand())) {
                    Server.writeToUser(userName + ": " + split.getMessage(), user.getUserThread());
                    return userName + ": " + split.getMessage();
                }
            }
        }
        Server.writeToUser(UNKNOWN_COMMAND, this);
        return UNKNOWN_COMMAND;
    }

    /**
     * Handles user action and returns its summary
     *
     * @param userName      String - user name
     * @param clientMessage String - client input
     * @return message String - based by method behaviour it returns summary of an action
     */
    public String userAction(String userName, String clientMessage) {
        Server.Split split = new Server.Split(clientMessage);

        utLogger.info(userName + ": " + split.getCommand() + " " + split.getMessage());

        switch (split.getCommand()) {
            case "\\help":
                return sendMessage(showCommands());
            case "\\showusers":
                return sendMessage(showUsers());
            case "\\showdecks":
                return sendMessage(showDecks());
            case "\\adddeck":
                return sendMessage(addDeck(userName, split));
            case "\\joindeck":
                return sendMessage(joinDeck(userName, split));
            case "\\leavedeck":
                return sendMessage(leaveDeck(userName, split));
            case "\\msgall":
                server.broadcast(userName + ": " + split.getMessage(), this);
                return "messaged all";
            case "\\info":
                return sendMessage(info(userName));
            default:
                return defaultAction(split, userName);
        }
    }

    /**
     * Handles user action and returns its summary
     *
     * @param userName String - user name
     * @param deck     {@link Deck} - deck in which user is playing
     * @return {@link Deck.Response} - info about current deck state
     */
    public Deck.Response deckAction(String userName, Deck deck) {
        return sendMessage(userName, deck.getResponse(), deck);
    }

    /**
     * Choose between 2 functions which handle userInput:<br>
     * {@link #deckAction(String, Deck)} <br>
     * {@link #userAction(String, String)}
     *
     * @param userName      String - name of a client
     * @param clientMessage String - his message in format: command message
     */
    public String action(String userName, String clientMessage) {
        boolean send = false;
        Deck deck;
        try {
            if (Objects.requireNonNull(Server.getUserFromName(userName)).getDeck().isStarted()) {
                deck = Objects.requireNonNull(Server.getUserFromName(userName)).getDeck();
                if (
                        Boolean.TRUE.equals(deck.updateResponse(userName, clientMessage).getMoveAccepted()) ||
                                deck.getResponse().getLastTried().contains(" [NOT YOUR TURN]") ||
                                !deck.getResponse().getLastTried().equals(userName + ": " + clientMessage)) {
                    send = true;
                    deckAction(userName, deck);
                    return "send from deck";
                }
            }
        } catch (Exception e) {
            if (!send) {
                userAction(userName, clientMessage);
                return "send from no existing deck";
            }
        }
        if (!send) {
            userAction(userName, clientMessage);
            return "send from deck";
        }
        return "";
    }

    /**
     * If {@link #ping()} found a player which has disconnected from a game it handles whole action
     *
     * @param deck   {@link Deck} - deck from which a player has disconnected
     * @param player String - players name
     * @return ret String - player has left server, game name is closing.
     */
    public String menagePing(Deck deck, String player) {
        List<String> playersToBeWritten = deck.getPlayerNames();
        playersToBeWritten.remove(player);
        Server.User user;

        for (String pl : playersToBeWritten) {

            user = Objects.requireNonNull(Server.getUserFromName(pl));

            Server.writeToUser(player + " has left server, game " + deck.getName() + " is closing.",
                    user.getUserThread());

            if (Boolean.FALSE.equals(deck.isKicked(pl))) {
                Server.userChangeDeck(user.getUserName(), "", false);
            }
        }

        server.removeDeck(deck);
        return player + " has left server, game " + deck.getName() + " is closing.";
    }

    /**
     * Returns inf o about user and closing game
     *
     * @return {@link #menagePing(Deck, String)}
     */
    public String ping() {

        for (Deck deck : Server.getDecks()) {
            for (String player : deck.getPlayerNames()) {
                if (!server.getUserNames().contains(player) && Boolean.TRUE.equals(!deck.isKicked(player))) {
                    return menagePing(deck, player);
                }
            }
        }
        return "";
    }

    /**
     * @param core name which player want to have
     * @return his real name
     */
    public String getFirstAvailableName(String core) {
        String userName = core;
        int i = 1;
        while (server.getUserNames().contains(userName)) {
            userName = userName.concat(String.valueOf(i));
            i += 1;
        }
        return userName;
    }

    /**
     * Reads client input Infinite loop and handles it in {@link #userAction(String, String)}
     */
    @Override
    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            String userName = reader.readLine();

            String newUserName = getFirstAvailableName(userName);
            server.addUserName(newUserName, this);
            sendMessage("Your userName is: " + newUserName);

            this.nm = userName;

            String serverMessage;
            String clientMessage;

            Server.writeToUser("write: \\help to see commands", this);
            do {
                clientMessage = reader.readLine();
                action(userName, clientMessage);
            } while (!clientMessage.equals("\\bye"));

            server.removeUser(userName, this);
            socket.close();

            serverMessage = userName + " has quit.";
            server.broadcast(serverMessage, this);

            ping();
        } catch (IOException ex) {

            server.removeUser(nm, this);

            String serverMessage = nm + " has quit.";
            server.broadcast(serverMessage, this);

            ping();
        }
    }
}
