package com.company;

import java.util.ArrayList;
import java.util.List;

/**
 * hand evaluation class
 */
public class Evaluate {
    private final ArrayList<Deck.Card> cards;
    private int points;
    private int type;
    private int color;
    private int diffRanks;
    private int maxOfOneRank;

    /**
     *
     * @return points of evaluation
     */
    public int getPoints() {
        return points;
    }

    /**
     *
     * @return type of points
     */
    public int getType() {
        return type;
    }

    /**
     *
     */
    private enum Class {
        ROYAL_POKER(10),
        POKER(9),
        FOUR_OF_KIND(8),
        FULL_HOUSE(7),
        FLUSH(6),
        STRAIGHT(5),
        THREE_OF_KIND(4),
        TWO_PAIRS(3),
        ONE_PAIR(2),
        HIGH_CARD(1);

        private final int points;

        Class(int points) {
            this.points = points;
        }

    }

    /**
     *
     * @param playerCards cards to be evaluated
     */
    public Evaluate(List<Deck.Card> playerCards) {
        this.points = 0;
        this.type = 0;
        this.cards = new ArrayList<>(playerCards);
        this.color = 0;
        this.diffRanks = 0;
        this.maxOfOneRank = 0;
    }

    /**
     *
     */
    public void eval() {
        color = howMuchColors();
        diffRanks = howMuchDiffRanks();
        maxOfOneRank = howMuchMaxOfOneRank();

        if (isRoyalPoker()) {
            this.points = Class.ROYAL_POKER.points;
        }
        else if (isPoker()) {
            this.points = Class.POKER.points;
        }
        else if (isFourOfKind()) {
            this.points = Class.FOUR_OF_KIND.points;
        }
        else if (isFullHouse()) {
            this.points = Class.FULL_HOUSE.points;
        }
        else if (isFlush()) {
            this.points = Class.FLUSH.points;
        }
        else if (isStraight()) {
            this.points = Class.STRAIGHT.points;
        }
        else if (isThreeOfKind()) {
            this.points = Class.THREE_OF_KIND.points;
        }
        else if (isTwoPairs()) {
            this.points = Class.TWO_PAIRS.points;
        }
        else if (isOnePair()) {
            this.points = Class.ONE_PAIR.points;
        }
        else {
            this.points = Class.HIGH_CARD.points;
            this.type = cards.get(4).getRank();
        }
    }

    /**
     *
     * @return if is royal poker
     */
    private boolean isRoyalPoker() {
        if (color == 1 && diffRanks == 5 && maxOfOneRank == 1) {
            ArrayList<Integer> ranks = new ArrayList<>(6);
            for (Deck.Card card : cards) {
                if (!ranks.contains(card.getRank())) {
                    ranks.add(card.getRank());
                }
            }
            return ranks.contains(14) && ranks.contains(13) &&
                    ranks.contains(12) && ranks.contains(11) && ranks.contains(10);
        }
        return false;
    }

    /**
     *
     * @return if is poker
     */
    private boolean isPoker() {
        if (color == 1 && diffRanks == 5 && maxOfOneRank == 1) {
            ArrayList<Integer> ranks = new ArrayList<>(6);
            for (Deck.Card card : cards) {
                if (!ranks.contains(card.getRank())) {
                    ranks.add(card.getRank());
                }
            }
            return ranks.contains(13) && ranks.contains(12) &&
                    ranks.contains(11) && ranks.contains(10) && ranks.contains(9);
        }
        return false;
    }

    /**
     *
     * @return if is four of kind
     */
    private boolean isFourOfKind() {
        if (color == 4 && diffRanks == 2 && maxOfOneRank == 4) {
            if (cards.get(0).getRank() == cards.get(1).getRank()) {
                this.type = cards.get(0).getRank();
            }
            else {
                this.type = cards.get(1).getRank();
            }
            return true;
        }
        return false;
    }

    /**
     *
     * @return if is full house
     */
    private boolean isFullHouse() {
        if ((color == 3 || color == 4) && diffRanks == 2 && maxOfOneRank == 3) {
            if (cards.get(0).getRank() == cards.get(1).getRank() && cards.get(1).getRank() == cards.get(2).getRank()) {
                this.type = cards.get(0).getRank();
            }
            else {
                this.type = cards.get(2).getRank();
            }
            return true;
        }
        return false;
    }

    /**
     *
     * @return if is flush
     */
    private boolean isFlush() {
        if (color == 1 && diffRanks == 5 && maxOfOneRank == 1) {
            this.type = cards.get(cards.size() - 1).getRank();
            return true;
        }
        return false;
    }

    /**
     *
     * @return if is straight
     */
    private boolean isStraight() {
        ArrayList<Integer> ranks = new ArrayList<>(6);
        for (Deck.Card card : cards) {
            if (!ranks.contains(card.getRank())) {
                ranks.add(card.getRank());
            }
        }
        if (((color == 2 || color == 3 || color == 4) && diffRanks == 5 && maxOfOneRank == 1) &&
                ((ranks.contains(13) && ranks.contains(12) && ranks.contains(11) && ranks.contains(10) && ranks.contains(9)) ||
                 (ranks.contains(14) && ranks.contains(13) && ranks.contains(12) && ranks.contains(11) && ranks.contains(10)))) {
            this.type = cards.get(cards.size() - 1).getRank();
            return true;
        }
        return false;
    }

    /**
     *
     * @return if is three of kind
     */
    private boolean isThreeOfKind() {
        if ((color == 3 || color == 4) && diffRanks == 3 && maxOfOneRank == 3) {
            if (cards.get(0).getRank() == cards.get(1).getRank() && cards.get(1).getRank() == cards.get(2).getRank()) {
                this.type = cards.get(0).getRank();
            }
            else if (cards.get(1).getRank() == cards.get(2).getRank() && cards.get(2).getRank() == cards.get(3).getRank()) {
                this.type = cards.get(1).getRank();
            }
            else {
                this.type = cards.get(2).getRank();
            }
            return true;
        }
        return false;
    }

    /**
     *
     * @return if is two pairs
     */
    private boolean isTwoPairs() {
        if ((color == 2 || color == 3 || color == 4) && diffRanks == 3 && maxOfOneRank == 2) {
            this.type = cards.get(3).getRank();
            return true;
        }
        return false;
    }

    /**
     *
     * @return if is one pair
     */
    private boolean isOnePair() {
        if ((color == 2 || color == 3 || color == 4) && diffRanks == 4 && maxOfOneRank == 2) {
            if (cards.get(0).getRank() == cards.get(1).getRank() || cards.get(1).getRank() == cards.get(2).getRank()) {
                this.type = cards.get(1).getRank();
            }
            else {
                this.type = cards.get(3).getRank();
            }
            return true;
        }
        return false;
    }

    /**
     *
     * @return how many colors are in hand
     */
    private int howMuchColors() {
        ArrayList<Integer> colors = new ArrayList<>(4);
        for (Deck.Card card : cards) {
            if (!colors.contains(card.getSuit())) {
                colors.add(card.getSuit());
            }
        }
        return colors.size();
    }

    /**
     *
     * @return how much different ranks are in hand
     */
    private int howMuchDiffRanks() {
        ArrayList<Integer> diff = new ArrayList<>(6);
        for (Deck.Card card : cards) {
            if (!diff.contains(card.getRank())) {
                diff.add(card.getRank());
            }
        }
        return diff.size();
    }

    /**
     *
     * @return how much max cards of certain rank exist in hand
     */
    private int howMuchMaxOfOneRank() {
        ArrayList<Integer> ranks = new ArrayList<>(15);
        for (int i = 0; i < 15; i++) {
            ranks.add(0);
        }
        for (Deck.Card card : cards) {
            ranks.set(card.getRank(), ranks.get(card.getRank()) + 1);
        }

        int temp;
        for (int j = 0; j < ranks.size() - 1; j++) {
            if (ranks.get(j) > ranks.get(j + 1)) {
                temp = ranks.get(j + 1);
                ranks.set(j + 1, ranks.get(j));
                ranks.set(j, temp);
            }
        }

        return ranks.get(ranks.size() - 1);
    }
}
