package com.company;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


public class EvaluateTest {
    private List<Deck.Card> cards = new ArrayList<>();
    static final Logger logger = Logger.getLogger(EvaluateTest.class.getName());


    @Before
    public void setUp() {
        Deck deck = new Deck("amamam", "asdasd", 3);
        cards = deck.fabric();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void royalPoker() {
        List<Deck.Card> royalPoker = new ArrayList<>();
        royalPoker.add(cards.get(4));
        royalPoker.add(cards.get(8));
        royalPoker.add(cards.get(12));
        royalPoker.add(cards.get(16));
        royalPoker.add(cards.get(20));

        Evaluate evaluate = new Evaluate(royalPoker);
        evaluate.eval();

        assertEquals(10, evaluate.getPoints());
        assertEquals(0, evaluate.getType());
    }

    @Test
    public void poker() {
        List<Deck.Card> royalPoker = new ArrayList<>();
        royalPoker.add(cards.get(4));
        royalPoker.add(cards.get(8));
        royalPoker.add(cards.get(12));
        royalPoker.add(cards.get(16));
        royalPoker.add(cards.get(0));

        Evaluate evaluate = new Evaluate(royalPoker);
        evaluate.eval();

        assertEquals(9, evaluate.getPoints());
        assertEquals(0, evaluate.getType());
    }

    @Test
    public void fourOfKind() {
        List<Deck.Card> royalPoker = new ArrayList<>();
        royalPoker.add(cards.get(0));
        royalPoker.add(cards.get(1));
        royalPoker.add(cards.get(2));
        royalPoker.add(cards.get(3));
        royalPoker.add(cards.get(6));

        Evaluate evaluate = new Evaluate(royalPoker);
        evaluate.eval();

        assertEquals(8, evaluate.getPoints());
        assertEquals(9, evaluate.getType());
    }

    @Test
    public void fullHouse() {
        List<Deck.Card> royalPoker = new ArrayList<>();
        royalPoker.add(cards.get(0));
        royalPoker.add(cards.get(1));
        royalPoker.add(cards.get(2));
        royalPoker.add(cards.get(7));
        royalPoker.add(cards.get(6));

        Evaluate evaluate = new Evaluate(royalPoker);
        evaluate.eval();

        assertEquals(7, evaluate.getPoints());
        assertEquals(9, evaluate.getType());
    }

    @Test
    public void flush() {
        List<Deck.Card> royalPoker = new ArrayList<>();
        royalPoker.add(cards.get(0));
        royalPoker.add(cards.get(4));
        royalPoker.add(cards.get(8));
        royalPoker.add(cards.get(12));
        royalPoker.add(cards.get(20));

        Evaluate evaluate = new Evaluate(royalPoker);
        evaluate.eval();

        assertEquals(6, evaluate.getPoints());
        assertEquals(14, evaluate.getType());
    }

    @Test
    public void straight() {
        List<Deck.Card> royalPoker = new ArrayList<>();
        royalPoker.add(cards.get(0));
        royalPoker.add(cards.get(5));
        royalPoker.add(cards.get(10));
        royalPoker.add(cards.get(12));
        royalPoker.add(cards.get(19));

        Evaluate evaluate = new Evaluate(royalPoker);
        evaluate.eval();

        assertEquals(5, evaluate.getPoints());
        assertEquals(13, evaluate.getType());
    }

    @Test
    public void threeOfKind() {
        List<Deck.Card> royalPoker = new ArrayList<>();
        royalPoker.add(cards.get(0));
        royalPoker.add(cards.get(1));
        royalPoker.add(cards.get(2));
        royalPoker.add(cards.get(12));
        royalPoker.add(cards.get(19));

        Evaluate evaluate = new Evaluate(royalPoker);
        evaluate.eval();

        assertEquals(4, evaluate.getPoints());
        assertEquals(9, evaluate.getType());
    }

    @Test
    public void twoPairs() {
        List<Deck.Card> royalPoker = new ArrayList<>();
        royalPoker.add(cards.get(0));
        royalPoker.add(cards.get(1));
        royalPoker.add(cards.get(13));
        royalPoker.add(cards.get(12));
        royalPoker.add(cards.get(19));

        Evaluate evaluate = new Evaluate(royalPoker);
        evaluate.eval();

        assertEquals(3, evaluate.getPoints());
        assertEquals(12, evaluate.getType());
    }

    @Test
    public void onePair() {
        List<Deck.Card> royalPoker = new ArrayList<>();
        royalPoker.add(cards.get(0));
        royalPoker.add(cards.get(1));
        royalPoker.add(cards.get(11));
        royalPoker.add(cards.get(19));
        royalPoker.add(cards.get(20));

        Evaluate evaluate = new Evaluate(royalPoker);
        evaluate.eval();

        assertEquals(2, evaluate.getPoints());
        assertEquals(9, evaluate.getType());
    }

    @Test
    public void highCard() {
        List<Deck.Card> royalPoker = new ArrayList<>();
        royalPoker.add(cards.get(0));
        royalPoker.add(cards.get(5));
        royalPoker.add(cards.get(11));
        royalPoker.add(cards.get(19));
        royalPoker.add(cards.get(20));

        Evaluate evaluate = new Evaluate(royalPoker);
        evaluate.eval();

        assertEquals(1, evaluate.getPoints());
        assertEquals(14, evaluate.getType());
    }
}
