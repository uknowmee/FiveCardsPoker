package com.company;

import java.util.Objects;

/**
 * Class which write to users when game started
 */
public class GameStartThread extends Thread {

    /**
     *
     * @return ret String - returns info when starting game
     * @throws InterruptedException - exeption
     */
    public static String gameStart() throws InterruptedException {

        sleep(1000);
        for (Deck deck : Server.getDecks()) {
            if (deck.getNumOfPlayers() == deck.getPlayers().size() && !deck.isStarted()) {
                for (String player : deck.getPlayerNames()) {
                    Server.writeToUser("game has started!",
                            Objects.requireNonNull(Server.getUserFromName(player)).getUserThread());
                }
                deck.setStarted(true);
                deck.startResponse();
                GameStartedThread gameStartedThread = new GameStartedThread();
                gameStartedThread.start();
                return "game has started!";
            }
        }
        return "";
    }

    /**
     * Runs {@link #gameStart()} in infinite loop
     */
    @Override
    public void run() {
        while (true) {
            try {
                gameStart();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
