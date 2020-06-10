package com.company;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

public class PanDeckTest {
    @Test
    void setDeckAndGetLenght() {
        PanDeck testDeck = new PanDeck();
        testDeck.setDeck();
        Assert.assertEquals(24, testDeck.getLength());
    }

    @Test
    void getDeck() {
        PanDeck testDeck = new PanDeck();
        testDeck.setDeck();
        for(int i = 0; i < testDeck.getLength(); i++) {
            Assert.assertEquals((i % 6), testDeck.getDeck()[i].getValueInt());
            Assert.assertEquals(i / 6, testDeck.getDeck()[i].getColorInt());
        }
    }

    @Test
    void drawCard() {
        PanDeck testDeck = new PanDeck();
        testDeck.setDeck();
        PanCard[] tempListOfCard = testDeck.drawCard(2);
        Assert.assertEquals(2, tempListOfCard.length);
        tempListOfCard = testDeck.drawCard(6);
        Assert.assertEquals(1, tempListOfCard[2].getValueInt());
        Assert.assertEquals(3, tempListOfCard[3].getColorInt());
    }
}
