package com.company;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void setNextPlayer() {
        Player tempPlayer = new Player();
        tempPlayer.initCardCount(6); //it means there are 4 players
        tempPlayer.setCurrentPlayer(1);
        for(int i = 4; i > 0; i--) {
            tempPlayer.setNextPlayer(1);    //in descending order
            Assert.assertEquals(i, tempPlayer.getCurrentPlayer());
        }
        for(int i = 1; i < 6; i++) {
            tempPlayer.setNextPlayer(0); //in ascending order
            Assert.assertEquals(((i % 4) + 1), tempPlayer.getCurrentPlayer());
        }

        Player tempPlayerTwo = new Player();
        tempPlayerTwo.initCardCount(12); //it means there are 2 players
        tempPlayerTwo.setCurrentPlayer(1);
        for(int i = 2; i > 0; i--) {
            tempPlayerTwo.setNextPlayer(1);
            Assert.assertEquals(i, tempPlayerTwo.getCurrentPlayer());
        }
        for(int i = 1; i < 4; i++) {
            tempPlayerTwo.setNextPlayer(0);
            Assert.assertEquals(((i % 2) + 1), tempPlayerTwo.getCurrentPlayer());
        }
    }

    @Test
    void getHandOfCards() {
    }

    @Test
    void setHandOfCards() {
    }

    @Test
    void setCardCount() {
    }

    @Test
    void getSelectedCard() {
    }

    @Test
    void setSelectedCard() {
    }

    @Test
    void setTopCard() {
    }

    @Test
    void initCardCount() {
        Player tempPlayerOne = new Player();
        tempPlayerOne.initCardCount(12);
        Assert.assertEquals(2,tempPlayerOne.getCardCount().length);
        Player tempPlayerTwo = new Player();
        tempPlayerTwo.initCardCount(6);
        Assert.assertEquals(4,tempPlayerTwo.getCardCount().length);
    }

    @Test
    void changeCardCount() {
    }

    @Test
    void addCardToHand() {
    }

    @Test
    void printHand() {
    }

    @Test
    void getStockpileSize() {
    }

    @Test
    void popStockpile() {
    }

    @Test
    void pushStockpile() {
    }

    @Test
    void checkCardIsValid() {
    }

    @Test
    void lastColorOnStockpile() {
    }

    @Test
    void deleteCardFromHand() {
    }
}