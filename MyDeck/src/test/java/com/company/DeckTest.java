package com.company;

import org.apache.log4j.Logger;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class DeckTest {
    private Deck deck;
    static final Logger logger = Logger.getLogger(DeckTest.class.getName());


    @Before
    public void setUp() {
        deck = new Deck("letsTest", "michal", 3);
        deck.playerJoin("wojtek");
        deck.playerJoin("ola");
        deck.startResponse();
    }

    @After
    public void tearUp() {

    }

    @Test
    public void isKicked() {
        assertFalse(deck.isKicked("wojtek"));
    }

    @Test
    public void playerLeave() {
        deck = new Deck("letsTest", "michal", 3);
        deck.playerJoin("wojtek");

        deck.playerLeave("wojtek");

        assertEquals(1, deck.getPlayers().size());
    }

    @Test
    public void getPlayerNames() {
        deck = new Deck("letsTest", "michal", 3);
        deck.playerJoin("wojtek");

        deck.playerLeave("wojtek");

        assertEquals(1, deck.getPlayerNames().size());
    }

    @Test
    public void getBank() {
        deck = new Deck("letsTest", "michal", 3);
        deck.playerJoin("wojtek");

        deck.playerLeave("wojtek");

        assertEquals(0, deck.getBank());
    }

    @Test
    public void getNumOfPlayers() {
        deck = new Deck("letsTest", "michal", 3);
        deck.playerJoin("wojtek");

        deck.playerLeave("wojtek");

        assertEquals(3, deck.getNumOfPlayers());
    }

    @Test
    public void getResponse() {
        deck = new Deck("letsTest", "michal", 3);
        deck.playerJoin("wojtek");

        deck.playerLeave("wojtek");

        assertEquals(0, deck.getResponse().getPart());
    }

    @Test
    public void getResponsePart() {
        assertEquals(3, deck.getResponse().getPlaying().size());
    }

    @Test
    public void getPlayingNames() {
        assertTrue(deck.getResponse().getPlayingNames().contains("michal"));
        assertTrue(deck.getResponse().getPlayingNames().contains("wojtek"));
        assertTrue(deck.getResponse().getPlayingNames().contains("ola"));
    }

    @Test
    public void getMoveAccepted() {
        deck.updateResponse("michal", "\\bet 10");
        assertTrue(deck.getResponse().getMoveAccepted());
    }

    @Test
    public void getWinner() {

        deck.updateResponse("michal", "\\check");
        deck.updateResponse("wojtek", "\\check");
        deck.updateResponse("ola", "\\check");

        deck.updateResponse("michal", "\\exchange");
        deck.updateResponse("wojtek", "\\exchange");
        deck.updateResponse("ola", "\\exchange");


        deck.setPlayersCredit("wojtek", -2);
        deck.setPlayersCredit("ola", -2);

        deck.updateResponse("michal", "\\check");
        deck.updateResponse("wojtek", "\\cya");
        deck.updateResponse("ola", "\\cya");

        assertEquals(1, deck.getResponse().getWinner().size());
    }

    @Test
    public void getLastTried() {

        deck.updateResponse("michal", "\\check");

        assertEquals("michal: \\check", deck.getResponse().getLastTried());
    }

    @Test
    public void skipping() {

        deck.updateResponse("michal", "\\check");
        deck.updateResponse("wojtek", "\\fold");
        deck.updateResponse("ola", "\\check");

        deck.updateResponse("michal", "\\exchange");
        deck.updateResponse("ola", "\\exchange");


        deck.setPlayersCredit("ola", -2);

        deck.updateResponse("michal", "\\check");
        deck.updateResponse("ola", "\\cya");

        assertEquals(1, deck.getResponse().getWinner().size());
    }

    @Test
    public void responseToString() {
        deck.updateResponse("michal", "\\check");

        assertEquals("last: michal, now: wojtek, next: ola", deck.getResponse().toString());
    }

    @Test
    public void getResponseString() {
        deck.updateResponse("michal", "\\check");

        assertEquals("last: michal, now: wojtek, next: ola, bank: 6, bid: 0", deck.getResponseString());
    }

    @Test
    public void getPlayers() {

        assertTrue(deck.getPlayers().contains(deck.getResponse().getPlaying().get(0)));
        assertTrue(deck.getPlayers().contains(deck.getResponse().getPlaying().get(1)));
        assertTrue(deck.getPlayers().contains(deck.getResponse().getPlaying().get(2)));
    }

    @Test
    public void getPlayingCardsFromName() {

        assertTrue(deck.getPlayingCardsFromName("michal").contains("\t"));
    }

    @Test
    public void getPlNames() {

        assertEquals(3, deck.getPlayingNames().size());
    }

    @Test
    public void getPlCredit() {

        deck.updateResponse("michal", "\\bet 10");

        assertEquals(88, deck.getPlayerCreditFromName("michal"));
        assertEquals(98, deck.getPlayerCreditFromName("wojtek"));
        assertEquals(98, deck.getPlayerCreditFromName("ola"));
    }

    @Test
    public void isStarted() {

        deck.setStarted(true);
        assertTrue(deck.isStarted());
    }

    @Test
    public void getBid() {

        deck.updateResponse("michal", "\\bet 10");

        assertEquals(10, deck.getBid());
    }

    @Test
    public void getAll() {

        deck.updateResponse("michal", "\\check");
        deck.updateResponse("wojtek", "\\check");
        deck.updateResponse("ola", "\\check");

        deck.updateResponse("michal", "\\exchange");
        deck.updateResponse("wojtek", "\\exchange");
        deck.updateResponse("ola", "\\exchange");


        deck.setPlayersCredit("wojtek", -50);
        deck.setPlayersCredit("ola", -50);

        deck.updateResponse("michal", "\\bet 70");
        deck.updateResponse("wojtek", "\\all");
        deck.updateResponse("ola", "\\all");

        assertTrue(deck.getAll());
    }

    @Test
    public void toStr() {

        String dck = """
                Deck named: letsTest, with maximum of: 3 players
                \tdecks players:
                \t\tmichal
                \t\twojtek
                \t\tola
                """;

        assertEquals(dck, deck.toString());
    }

    @Test
    public void call() {

        deck.updateResponse("michal", "\\bet 10");
        deck.updateResponse("wojtek", "\\call");
        deck.updateResponse("ola", "\\call");

        deck.updateResponse("michal", "\\exchange");
        deck.updateResponse("wojtek", "\\exchange 7 5");
        deck.updateResponse("wojtek", "\\exchange sadasd");
        deck.updateResponse("wojtek", "\\exchange");
        deck.updateResponse("ola", "\\exchange");


        deck.setPlayersCredit("wojtek", -2);
        deck.setPlayersCredit("ola", -2);

        deck.updateResponse("michal", "\\check");
        deck.updateResponse("wojtek", "\\cya");
        deck.updateResponse("ola", "\\cya");

        assertEquals(1, deck.getResponse().getWinner().size());
    }

    @Test
    public void raise() {

        deck.updateResponse("michal", "\\bet 1011");
        deck.updateResponse("michal", "\\bet 1");
        deck.updateResponse("michal", "\\bet asdasd");
        deck.updateResponse("michal", "\\bet 10");
        deck.updateResponse("wojtek", "\\bet 10");
        deck.updateResponse("wojtek", "\\raise 21");
        deck.updateResponse("ola", "\\call");
        deck.updateResponse("michal", "\\raise 45");
        deck.updateResponse("wojtek", "\\call");
        deck.updateResponse("ola", "\\call");



        deck.updateResponse("michal", "\\exchange 1 2");
        deck.updateResponse("wojtek", "\\exchange 3 4");
        deck.updateResponse("ola", "\\exchange");


        deck.setPlayersCredit("wojtek", -2);
        deck.setPlayersCredit("ola", -2);

        deck.updateResponse("michal", "\\check");
        deck.updateResponse("wojtek", "\\cya");
        deck.updateResponse("ola", "\\cya");

        assertEquals(1, deck.getResponse().getWinner().size());
    }

    @Test
    public void collect() {

        deck.collectTheCards();

        assertEquals("", deck.getPlayingCardsFromName("michal"));
    }
}
