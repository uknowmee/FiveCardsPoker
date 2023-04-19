package com.company;

import java.util.Objects;

/**
 * Class which constantly writes to users while game running
 */
public class GameStartedThread extends Thread {

    /**
     * Print standard response from deck to any started game
     *
     * @return ret String - response
     */
    public static String gameStarted(long time) {

        for (Deck deck : Server.getDecks()) {
            if (deck.getNumOfPlayers() == deck.getPlayers().size() &&
                    deck.isStarted() &&
                    deck.getResponse().getTime() + 15 * 1000 < time) {
                for (String player : deck.getResponse().getPlayingNames()) {
                    Server.writeToUser(deck.getResponseString() +"\n",
                            Objects.requireNonNull(Server.getUserFromName(player)).getUserThread());
                }
                return deck.getResponseString();
            }
        }
        return "";
    }

    /**
     * Runs {@link #gameStarted(long)} in infinite loop
     */
    @Override
    public void run() {
        while (true) {
            try {
                sleep(15000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            gameStarted(System.currentTimeMillis());
        }
    }
}