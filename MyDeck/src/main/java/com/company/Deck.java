package com.company;


import java.util.*;


/**
 * Class representing classic Deck, 24 cords existing players.<br>
 * It can run poker  draw game with 2, 3 or 4 players.
 */
public class Deck {
    private final Random random;

    private final String name;
    private final List<Player> players;
    private final int numOfPlayers;
    private List<Card> cards;
    private boolean started;
    private int bank;
    private int bid;
    private Response response;
    private static final String NOT_ALLOWED = " [not allowed]";


    /**
     * Class describing a single Card
     */
    protected static class Card {
        private final Rank rank;
        private final Suit suit;
        private final int num;

        /**
         * Enum representing the rank of single card
         */
        private enum Rank {

            DZIEWIATKA(9),
            DZIESIATKA(10),
            WALET(11),
            DAMA(12),
            KROL(13),
            AS(14);

            private final int value;

            Rank(int value) {
                this.value = value;
            }
        }

        /**
         * Enum representing color of the single card
         */
        private enum Suit {

            PIK(0),
            TREFL(1),
            KIER(2),
            KARO(3);

            private final int numOfColor;

            Suit(int numOfColor) {
                this.numOfColor = numOfColor;
            }
        }

        public int getRank() {
            return rank.value;
        }

        public int getSuit() {
            return suit.numOfColor;
        }

        @Override
        public String toString() {
            return this.rank + " (" + this.rank.value + ") " + this.suit;
        }

        /**
         * Base constructor
         *
         * @param suitToSet {@link String} - suit which card will be connected with
         * @param rankToSet {@link String} - rank which card will be connected with
         */
        private Card(String suitToSet, String rankToSet, int num) {
            this.rank = Rank.valueOf(rankToSet);
            this.suit = Suit.valueOf(suitToSet);
            this.num = num;
        }
    }

    /**
     * Class describing a single Player
     */
    private static class Player {

        private final List<Card> playerCards;
        private final String playerName;
        private boolean fold;
        private boolean check;

        private boolean bet;
        private boolean raise;
        private boolean exchange;
        private boolean turn;
        private final boolean kicked;
        private int credit;
        private int diff;
        private int points;
        private int type;


        /**
         * Base constructor
         *
         * @param playerName {@link String} - name of the player
         */
        protected Player(String playerName) {
            this.playerName = playerName;
            this.playerCards = new ArrayList<>();
            this.credit = 100;
            this.fold = false;
            this.check = false;
            this.bet = false;
            this.raise = false;
            this.turn = false;
            this.kicked = false;
        }

        /**
         * @return ret Boolean - true if kicked
         */
        public boolean isKicked() {
            return kicked;
        }

        /**
         * @return {@link #playerName} String - player name as string
         */
        @Override
        public String toString() {
            return playerName;
        }
    }

    /**
     * Static response from Deck with running game to server
     */
    static class Response {
        private int part;
        private List<Player> playing;
        private String whoLast;
        private String whoNow;
        private String whoNext;
        private Boolean moveAccepted;
        private Boolean all;
        private ArrayList<String> winner;
        private String lastTried;
        private long time;

        /**
         * Base constructor
         */
        Response() {
            this.part = 0;
            this.moveAccepted = false;
            this.winner = new ArrayList<>();
            this.lastTried = "";
            this.all = false;
        }

        /**
         * @param players base deck players
         */
        Response(ArrayList<Player> players) {
            this.part = 1;
            this.playing = new ArrayList<>(players);
            this.whoLast = "";
            this.whoNow = this.playing.get(0).playerName;
            this.whoNext = this.playing.get(1).playerName;
            this.all = false;
        }

        public int getPart() {
            return part;
        }

        public List<Player> getPlaying() {
            return playing;
        }

        public List<String> getPlayingNames() {
            ArrayList<String> playerNames = new ArrayList<>();
            for (Player player : playing) {
                playerNames.add(player.playerName);
            }
            return playerNames;
        }

        public Boolean getMoveAccepted() {
            return moveAccepted;
        }

        public List<String> getWinner() {
            return winner;
        }

        public String getLastTried() {
            return lastTried;
        }

        public long getTime() {
            return time;
        }

        /**
         *
         */
        public void skipping() {
            while (playing.get(0).fold) {
                Player player = playing.get(0);
                playing.remove(0);
                playing.add(player);
            }
            whoNow = playing.get(0).playerName;
            if (playing.size() == 1) {
                whoNext = playing.get(0).playerName;
            }
            else {
                whoNext = playing.get(1).playerName;
            }
        }

        /**
         *
         * @param accepted if update should be accepted (queue switched)
         */
        private void update(Boolean accepted) {
            moveAccepted = accepted;
            if (Boolean.TRUE.equals(accepted)) {
                Player player = playing.get(0);
                playing.remove(0);
                whoLast = player.playerName;
                if (player.credit >= 0) {
                    playing.add(player);
                }
                skipping();
                playing.get(0).turn = true;
            }
        }

        /**
         *
         * @param players - used to calculate the highest player points
         * @return highestPoints Int
         */
        private int getHighestPoints(ArrayList<Player> players) {
            int maxPoints = players.get(0).points;
            for (Player pl : players) {
                if (pl.points > maxPoints && !pl.fold) {
                    maxPoints = pl.points;
                }
            }
            return maxPoints;
        }

        /**
         *
         * @param players all plaing players
         * @param maxPoints max points of playing players
         * @return type of certain points
         */
        private int getHighestType(ArrayList<Player> players, int maxPoints) {
            int maxType = 0;
            for (Player pl : players) {
                if (pl.type > maxType && pl.points == maxPoints && !pl.fold) {
                    maxType = pl.type;
                }
            }
            return maxType;
        }

        public void setMoveAccepted(Boolean toSet) {
            moveAccepted = toSet;
        }

        /**
         *
         * @return list of winning guys
         */
        private ArrayList<String> winEval() {
            ArrayList<Player> pls = new ArrayList<>(playing);
            ArrayList<String> maxPlayers = new ArrayList<>();

            int maxPoints = getHighestPoints(pls);
            int typeOfMaxPoints = getHighestType(pls, maxPoints);
            for (Player pl : pls) {
                if (pl.points == maxPoints && pl.type == typeOfMaxPoints && !pl.fold) {
                    maxPlayers.add(pl.playerName);
                }
            }

            return maxPlayers;
        }

        /**
         * @param userName name of player to be updated
         * @param message message of player
         * @param accepted if accepted
         */
        private void playerUpdate(String userName, String message, Boolean accepted) {
            update(accepted);
            lastTried = userName + ": " + message;
            if ((playing.get(0).bet || playing.get(0).raise || playing.get(0).check) && part % 2 == 1) {
                for (Player pl : playing) {
                    if (!pl.fold) {
                        pl.check = false;
                        pl.bet = false;
                        pl.raise = false;
                        pl.exchange = true;
                    }
                }
            }
            else if ((playing.get(0).bet || playing.get(0).raise || playing.get(0).check || all) && part % 2 == 0) {
                winner = winEval();
                for (Player pl : playing) {
                    pl.check = false;
                    pl.bet = false;
                    pl.raise = false;
                    pl.exchange = false;
                    pl.fold = false;
                }
                all = false;
                part++;
            }
            time = System.currentTimeMillis();
        }

        /**
         * Returns response as string
         *
         * @return ret String - response represented by string
         */
        @Override
        public String toString() {
            return "last: " + whoLast + ", now: " + whoNow + ", next: " + whoNext;
        }
    }

    /**
     * Base constructor
     *
     * @param deckName     {@link String} - name of the deck
     * @param userName     {@link String} - name of deck creator
     * @param numOfPlayers {@link Integer} - max number of players
     */
    public Deck(String deckName,
                String userName, int numOfPlayers) {

        this.response = new Response();
        this.random = new Random();

        this.players = new ArrayList<>();
        this.players.add(new Player(userName));

        this.cards = new ArrayList<>(fabric());

        this.name = deckName;
        this.numOfPlayers = numOfPlayers;

        this.bid = 0;
        this.bank = 0;

        this.started = false;
    }

    /**
     * Returns deck response as string
     *
     * @return ret String - deck response represented by string
     */
    public String getResponseString() {
        return response.toString() + ", bank: " + bank + ", bid: " + bid;
    }

    /**
     * Returns deck response as class object
     *
     * @return {@link #response} {@link Response} - object representing response of the deck
     */
    public Response getResponse() {
        return response;
    }

    /**
     * Return players connected to deck
     *
     * @return {@link #players} ArrayList - players on deck
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * @param name player of which cards will be returned
     * @return cards as string
     */
    public String getPlayingCardsFromName(String name) {
        StringBuilder ret = new StringBuilder();
        int i=0;

        for (Player player : response.playing) {
            if (player.playerName.equals(name)) {
                for (Card card : player.playerCards) {
                    ret.append("\t").append("[").append(i).append("]").append(card).append("\n");
                    i++;
                }
                return ret.toString();
            }
        }
        return null;
    }

    /**
     * @return all names of playing players
     */
    public List<String> getPlayingNames() {
        ArrayList<String> names = new ArrayList<>();

        for (Player pl : response.playing) {
            names.add(pl.playerName);
        }

        return names;
    }

    /**
     * @param name name of player which credit we will return
     * @return credit Int - credit of a player
     */
    public int getPlayerCreditFromName(String name) {
        for (Player player : response.playing) {
            if (player.playerName.equals(name)) {
                return player.credit;
            }
        }
        return 0;
    }

    /**
     * Return player names connected to deck
     *
     * @return playerNames ArrayList - player names on deck
     */
    public List<String> getPlayerNames() {
        ArrayList<String> playerNames = new ArrayList<>();
        for (Player player : players) {
            playerNames.add(player.playerName);
        }
        return playerNames;
    }

    /**
     * @param player player to be checked
     * @return true if kicked
     */
    public Boolean isKicked(String player) {
        for (Player pl : players) {
            if (pl.playerName.equals(player)) {
                return pl.isKicked();
            }
        }
        return false;
    }

    /**
     * Return name of the deck
     *
     * @return {@link #name} String - deck name
     */
    public String getName() {
        return name;
    }

    /**
     * Method says if certain game already started
     *
     * @return {@link #started} Boolean - true if game started
     */
    public boolean isStarted() {
        return started;
    }

    /**
     * @return bank of the game
     */
    public int getBank() {
        return bank;
    }

    /**
     * @return bid of the game
     */
    public int getBid() {
        return bid;
    }

    /**
     * @return returns true if there is any allIn move in game
     */
    public boolean getAll() {
        return response.all;
    }

    /**
     * Return number of maximum players
     *
     * @return {@link #numOfPlayers} Integer - max number of players
     */
    public int getNumOfPlayers() {
        return numOfPlayers;
    }

    /**
     * GameStartThread set this to true when starting the game
     *
     * @param started Boolean - this.started to set
     */
    public void setStarted(boolean started) {
        this.started = started;
    }

    /**
     * helper methof for test purposes
     * @param plName name to set credit
     * @param toSet credit to set
     */
    public void setPlayersCredit(String plName, int toSet) {
        for (Player pl : response.playing) {
            if (pl.playerName.equals(plName)) {
                pl.credit = toSet;
                break;
            }
        }
    }

    /**
     * Returns deck as string
     *
     * @return ret String - deck represented by string
     */
    @Override
    public String toString() {
        StringBuilder deck = new StringBuilder("Deck named: " + name +
                ", with maximum of: " + numOfPlayers + " players\n" + "\tdecks players:\n");

        for (Player player : players) {
            deck.append("\t\t").append(player.playerName).append("\n");
        }

        return deck.toString();
    }

    /**
     * @param userName joins the player to the game
     */
    public void playerJoin(String userName) {
        players.add(new Player(userName));
    }

    /**
     * @param userName helps with leaving the game
     */
    public void playerLeave(String userName) {
        for (Player player : players) {
            if (player.playerName.equals(userName)) {
                players.remove(player);
                break;
            }
        }
    }

    /**
     * Void method which deals cards between players
     */
    public void dealTheCards() {

        if (!response.playing.isEmpty()) {
            for (Player player : response.playing) {
                if (!player.fold) {
                    int num = 5 - player.playerCards.size();
                    for (int i = num; i > 0; i--) {
                        player.playerCards.add(cards.remove(cards.size() - 1));
                    }
                }
            }
        }
    }

    /**
     * sort cards
     */
    public void sortCards() {
        Card card;
        for (Player player : response.playing) {
            for (int i = 0; i < player.playerCards.size() - 1; i++) {
                for (int j = 0; j < player.playerCards.size() - 1 - i; j++) {
                    if (player.playerCards.get(j).num > player.playerCards.get(j + 1).num) {
                        card = player.playerCards.get(j + 1);
                        player.playerCards.set(j + 1, player.playerCards.get(j));
                        player.playerCards.set(j, card);
                    }
                }
            }
        }
    }

    /**
     * Void method which collects cards from each player and put them back to the card heap list
     */
    public void collectTheCards() {

        if (!response.playing.isEmpty()) {
            for (Player player : response.playing) {
                for (int i = player.playerCards.size() - 1; i >= 0; i--) {
                    cards.add(player.playerCards.remove(i));
                }
            }
        }
    }

    /**
     * @param userName collect cards from certain user
     */
    public void collectCardsFromUser(String userName) {
        if (!response.playing.isEmpty()) {
            for (Player player : response.playing) {
                if (player.playerName.equals(userName)) {
                    for (int i = player.playerCards.size() - 1; i >= 0; i--) {
                        cards.add(player.playerCards.remove(i));
                    }
                }
            }
        }
    }

    /**
     * Shuffle cards in deck
     */
    public void shuffle() {

        ArrayList<Card> sorted = new ArrayList<>(this.cards);
        Card card;
        int first;
        int second;

        for (int i = 0; i < 100; i++) {
            first = this.random.nextInt(sorted.size());
            second = this.random.nextInt(sorted.size());
            while (first == second) {
                second = this.random.nextInt(sorted.size());
            }
            card = sorted.get(first);
            sorted.set(first, sorted.get(second));
            sorted.set(second, card);
        }

        cards = sorted;
    }

    /**
     * Method is used by setCards method
     */
    public List<Card> fabric() {

        int num = 0;
        ArrayList<Card> fabric = new ArrayList<>();
        for (Card.Rank rank : Card.Rank.values()) {
            for (Card.Suit suit : Card.Suit.values()) {
                fabric.add(new Card(suit.name(), rank.name(), num));
                num++;
            }
        }
        return fabric;
    }

    /**
     *
     * @param userName user to fold
     * @param split his message
     */
    public void fold(String userName, Split split) {
        rank();
        if (response.playing.get(0).credit > 0 && !response.playing.get(0).exchange) {
            response.playing.get(0).fold = true;
            response.playing.get(0).turn = false;
            collectCardsFromUser(userName);
            response.playerUpdate(userName, split.getCommand(), true);

        }
        else {
            response.playerUpdate(userName, split.getCommand() + NOT_ALLOWED, false);
        }
    }

    /**
     *
     * @param userName user to check
     * @param split his message
     */
    public void check(String userName, Split split) {
        rank();
        if (bid == 0 && response.playing.get(0).credit > 0 && !response.playing.get(0).exchange) {
            response.playing.get(0).check = true;
            response.playing.get(0).turn = false;
            response.playerUpdate(userName, split.getCommand(), true);
        }
        else {
            response.playerUpdate(userName, split.getCommand() + NOT_ALLOWED, false);
        }
    }

    /**
     *
     * @param userName user to call
     * @param split his message
     */
    public void call(String userName, Split split) {
        rank();
        if (response.playing.get(0).credit > bid && bid != 0 && !response.playing.get(0).exchange) {
            response.playing.get(0).turn = false;
            response.playing.get(0).credit -= response.playing.get(0).diff;
            bank += response.playing.get(0).diff;
            response.playing.get(0).diff = 0;
            response.playerUpdate(userName, split.getCommand(), true);
        }
        else {
            response.playerUpdate(userName, split.getCommand() + NOT_ALLOWED, false);
        }
    }

    /**
     *
     * @param myInt int to be bet
     * @param userName username
     * @param split his message
     */
    public void betMyInt(int myInt, String userName, Split split) {
        bid = myInt;
        bank += myInt;
        response.playing.get(0).credit -= myInt;
        response.playing.get(0).bet = true;
        response.playing.get(0).turn = false;
        for (int i = 1; i < response.playing.size(); i++) {
            if (!response.playing.get(i).fold) {
                response.playing.get(i).diff += myInt;
                response.playing.get(i).check = false;
            }
        }
        response.playerUpdate(userName, split.getCommand() + " " + myInt, true);
    }

    /**
     *
     * @param userName user to bet
     * @param split his message
     */
    public void bet(String userName, Split split) {
        rank();
        if (bid == 0 && response.playing.get(0).credit > 2 && !getAll() && !response.playing.get(0).exchange) {
            int myInt;
            try {
                myInt = Integer.parseInt(split.getMessage());
                if (2 < myInt && myInt < response.playing.get(0).credit) {
                    betMyInt(myInt, userName, split);
                }
                else if (myInt <= 2) {
                    response.playerUpdate(userName, myInt + " is too small", false);
                }
                else {
                    response.playerUpdate(userName, myInt + " is too big", false);

                }
            } catch (Exception e) {
                response.playerUpdate(userName, "wrong number: " + split.getMessage(), false);
            }
        }
        else {
            response.playerUpdate(userName, split.getCommand() + NOT_ALLOWED, false);
        }
    }

    /**
     *
     * @param myInt int to be raised
     * @param userName username
     * @param split his message
     */
    public void raiseMyInt(int myInt, String userName, Split split) {
        bank += myInt;
        response.playing.get(0).credit -= myInt;
        response.playing.get(0).raise = true;
        response.playing.get(0).turn = false;
        for (int i = 1; i < response.playing.size(); i++) {
            if (!response.playing.get(i).fold) {
                response.playing.get(i).diff += myInt - bid;
                response.playing.get(i).check = false;
                response.playing.get(i).bet = false;
                response.playing.get(i).raise = false;
            }
        }
        bid = myInt;
        response.playerUpdate(userName, split.getCommand() + " " + myInt, true);
    }

    /**
     *
     * @param userName user to raise
     * @param split his message
     */
    public void raise(String userName, Split split) {
        rank();
        if (bid != 0 && response.playing.get(0).credit > 2 * bid && !getAll() && !response.playing.get(0).exchange) {
            int myInt;
            try {
                myInt = Integer.parseInt(split.getMessage());
                if (2 * bid < myInt && myInt < response.playing.get(0).credit) {
                    raiseMyInt(myInt, userName, split);
                }
                else if (myInt < response.playing.get(0).credit) {
                    response.playerUpdate(userName, split.getCommand() + " " + myInt + " [is too small]", false);
                }
                else if (myInt >= response.playing.get(0).credit) {
                    response.playerUpdate(userName, split.getCommand() + " " + myInt + " [is too big]", false);

                }
            } catch (Exception e) {
                response.playerUpdate(userName, split.getCommand() + " " + split.getMessage() + " [wrong number]", false);
            }
        }
        else {
            response.playerUpdate(userName, split.getCommand() + NOT_ALLOWED, false);
        }
    }

    /**
     *
     * @param userName user to all
     * @param split his message
     */
    public void all(String userName, Split split) {
        rank();
        if (response.playing.get(0).credit - bid <= 0 && bid != 0 && !response.playing.get(0).exchange) {
            response.playing.get(0).turn = false;
            bank += response.playing.get(0).credit;
            response.playing.get(0).credit = 0;
            response.playing.get(0).diff = 0;
            response.all = true;
            response.playerUpdate(userName, split.getCommand(), true);
        }
        else {
            response.playerUpdate(userName, split.getCommand() + NOT_ALLOWED, false);
        }
    }

    /**
     *
     * @param userName user to cya
     * @param split his message
     */
    public void cya(String userName, Split split) {
        rank();
        if (response.playing.get(0).credit < 0 && !response.playing.get(0).exchange) {
            collectCardsFromUser(userName);
            response.playerUpdate(userName, split.getCommand(), true);
        }
        else {
            response.playerUpdate(userName, split.getCommand() + NOT_ALLOWED, false);

        }
    }

    /**
     *
     * @param userName user to be exchanged with cards
     * @param intToBeRemoved which he wants to remove
     * @param splitText his exchange message
     * @return list which he wants to remove
     */
    public List<Integer> exchangeGetNums(String userName, List<Integer> intToBeRemoved, String[] splitText) {
        if (!splitText[0].equals("")) {
            int temp;
            for (String s : splitText) {
                try {
                    temp = Integer.parseInt(s);
                    if (temp >= 0 && temp <= 4) {
                        intToBeRemoved.add(temp);
                    }
                    else {
                        response.playerUpdate(userName, "wrong card number", false);
                        intToBeRemoved.clear();
                        return intToBeRemoved;
                    }
                } catch (Exception e) {
                    response.playerUpdate(userName, "wrong card number", false);
                    intToBeRemoved.clear();
                    return intToBeRemoved;
                }
            }
        }
        return intToBeRemoved;
    }

    /**
     *
     * @param userName user to be exchanged with cards
     * @param split his message
     */
    public void exchangeRemoveCards(String userName, Split split) {
        String[] splitText = split.getMessage().split(" ");
        List<Integer> intToBeRemoved = new ArrayList<>();

        intToBeRemoved = exchangeGetNums(userName, intToBeRemoved, splitText);

        if (!intToBeRemoved.isEmpty() || splitText[0].equals("")) {

            for (int toAdd : intToBeRemoved) {
                cards.add(response.playing.get(0).playerCards.get(toAdd));
            }

            for (int i = response.playing.get(0).playerCards.size() - 1; i >= 0; i--) {
                if (intToBeRemoved.contains(i)) {
                    response.playing.get(0).playerCards.remove(i);
                }
            }

            response.playing.get(0).exchange = false;
            response.playerUpdate(userName, split.getCommand(), true);
        }
    }

    /**
     *
     * @param userName his name
     * @param split his message
     */
    public void exchange(String userName, Split split) {

        if (response.playing.get(0).exchange) {

            exchangeRemoveCards(userName, split);

            if (!response.playing.get(0).exchange) {
                shuffle();
                dealTheCards();

                response.part++;
                bid = 0;
                rank();
                response.playerUpdate(userName, split.getCommand(), false);
                response.setMoveAccepted(true);
            }
        }
        else {
            response.playerUpdate(userName, split.getCommand() + NOT_ALLOWED, false);
        }
    }

    /**
     * @param userName name
     * @param text un parted message
     * @return his response
     */
    public Response updateResponse(String userName, String text) {

        if (response.playing.size() == 1) {
            response.winner.add(response.playing.get(0).playerName);
            return response;
        }

        Split split = new Split(text);

        for (Player pl : response.playing) {
            if (pl.playerName.equals(userName) && pl.turn) {
                switch (split.getCommand()) {
                    case "\\fold" -> fold(userName, split);
                    case "\\check" -> check(userName, split);
                    case "\\call" -> call(userName, split);
                    case "\\bet" -> bet(userName, split);
                    case "\\raise" -> raise(userName, split);
                    case "\\all" -> all(userName, split);
                    case "\\cya" -> cya(userName, split);
                    case "\\exchange" -> exchange(userName, split);
                    default -> response.playerUpdate(userName, text, false);
                }
                break;
            }
            else if (pl.playerName.equals(userName)) {
                if (Arrays.asList("\\fold", "\\call", "\\bet",
                        "\\raise", "\\all", "\\cya", "\\exchange").contains(split.getCommand())) {

                    response.playerUpdate(userName, text + " [NOT YOUR TURN]", false);
                }
                else {
                    response.playerUpdate(userName, text, false);
                }
            }
        }
        return response;
    }

    /**
     * evaluate players hands
     */
    public void rank() {
        sortCards();

        for (Player pl : response.playing) {
            if (!pl.fold) {
                Evaluate evaluate = new Evaluate(pl.playerCards);
                evaluate.eval();
                pl.points = evaluate.getPoints();
                pl.type = evaluate.getType();
            }
        }
    }

    /**
     * resets the game between rounds
     */
    public void gameReset() {
        bid = 0;
        response.moveAccepted = false;
        response.lastTried = "";

        for (Player pl : response.playing) {
            if (response.winner.contains(pl.playerName)) {
                pl.credit += bank / response.winner.size();
            }
        }
        bank = 0;
        response.winner = new ArrayList<>();

        collectTheCards();
        shuffle();
        dealTheCards();

        bank += response.playing.size() * 2;
        for (Player player : response.playing) {
            player.credit -= 2;
        }

        players.get(0).turn = true;
        response.whoLast = "";
        response.whoNow = response.playing.get(0).playerName;
        response.whoNext = response.playing.get(1).playerName;

        rank();
    }

    /**
     * after getting all players in game it starts
     */
    public void startResponse() {
        this.bank += this.players.size() * 2;
        for (Player player : players) {
            player.credit -= 2;
        }
        players.get(0).turn = true;
        this.response = new Response(new ArrayList<>(players));
        shuffle();
        dealTheCards();

        rank();

        this.response.time = System.currentTimeMillis();
    }
}
