package com.company;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class PlayerTest {

    @Test
    void setNextPlayer() {
        //TODO has to make test based on created stockpile
        Player tempPlayer = new Player();
        tempPlayer.pushStockpile(new PanCard(PanCard.Color.getColor(1), PanCard.Value.getValue(0)));
        tempPlayer.initCardCount(6); //it means there are 4 players
        tempPlayer.setCurrentPlayer(1);
        for(int i = 7; i > 0; i--) {
            tempPlayer.setNextPlayer();    //in descending order
            Assert.assertEquals(((i % 4) + 1), tempPlayer.getCurrentPlayer());
        }
        tempPlayer.pushStockpile(new PanCard(PanCard.Color.getColor(0), PanCard.Value.getValue(0)));
        tempPlayer.setCurrentPlayer(1);
        for(int i = 1; i < 10; i++) {
            tempPlayer.setNextPlayer(); //in ascending order
            Assert.assertEquals(((i % 4) + 1), tempPlayer.getCurrentPlayer());
        }

        Player tempPlayerTwo = new Player();
        tempPlayerTwo.initCardCount(12); //it means there are 2 players
        tempPlayerTwo.pushStockpile(new PanCard(PanCard.Color.getColor(1), PanCard.Value.getValue(0)));
        tempPlayerTwo.setCurrentPlayer(1);
        for(int i = 3; i > 0; i--) {
            tempPlayerTwo.setNextPlayer();     //in descending order
            Assert.assertEquals(((i % 2) + 1), tempPlayerTwo.getCurrentPlayer());
        }
        tempPlayerTwo.pushStockpile(new PanCard(PanCard.Color.getColor(0), PanCard.Value.getValue(0)));
        tempPlayerTwo.setCurrentPlayer(1);
        for(int i = 1; i < 10; i++) {
            tempPlayerTwo.setNextPlayer();     //in ascending order
            Assert.assertEquals(((i % 2) + 1), tempPlayerTwo.getCurrentPlayer());
        }
    }

    @Test
    void setAndGetHandOfCards() {
        ArrayList<PanCard> tempHandOfCard = new ArrayList<PanCard>();
        for(int i = 0; i < 4; i++) {
            tempHandOfCard.add(new PanCard(PanCard.Color.getColor(i),PanCard.Value.getValue(i)));
        }
        Player tempPlayer = new Player();
        tempPlayer.setHandOfCards(tempHandOfCard);
        for(int i = 0; i < 4; i++) {
            Assert.assertEquals(i, tempPlayer.getHandOfCards().get(i).getColorInt());
            Assert.assertEquals(i, tempPlayer.getHandOfCards().get(i).getValueInt());
        }
    }

    // temporary redundant
//    @Test
//    void getSelectedCard() {
//    }
//
//    @Test
//    void setSelectedCard() {
//    }

    @Test
    void initCardCount() {
        Player tempPlayerOne = new Player();
        tempPlayerOne.initCardCount(12);
        Assert.assertEquals(2,tempPlayerOne.getCardCount().length);
        Player tempPlayerTwo = new Player();
        tempPlayerTwo.initCardCount(6);
        Assert.assertEquals(4,tempPlayerTwo.getCardCount().length);
        tempPlayerOne = new Player();
    }

    @Test
    void changeCardCount() {
        Player tempPlayer = new Player();
        tempPlayer.initCardCount(6);
        tempPlayer.setCardCount(1,-1);
        tempPlayer.setCardCount(2,1);
        tempPlayer.setCardCount(3,-1);
        tempPlayer.setCardCount(3, -1);
        tempPlayer.setCardCount(2,1);
        Assert.assertEquals(5,tempPlayer.getCardCount()[0]);
        Assert.assertEquals(8,tempPlayer.getCardCount()[1]);
        Assert.assertEquals(4,tempPlayer.getCardCount()[2]);
        Assert.assertEquals(6,tempPlayer.getCardCount()[3]);
    }

    @Test
    void addCardToHand() {
    }

    @Test
    void printHand() {
    }

    @Test
    void popStockpile() {
        Player tempPlayer = new Player();
        for(int i = 0; i < 4; i++) {
            tempPlayer.pushStockpile(new PanCard(PanCard.Color.getColor(i), PanCard.Value.getValue(i)));
        }
        tempPlayer.popStockpile();
        tempPlayer.popStockpile();
        Assert.assertEquals(2, tempPlayer.getStockpileSize());
    }

    @Test
    void pushStockpile() {
        Player tempPlayer = new Player();
        for(int i = 0; i < 4; i++) {
            tempPlayer.pushStockpile(new PanCard(PanCard.Color.getColor(i), PanCard.Value.getValue(i)));
        }
        Assert.assertEquals(4, tempPlayer.getStockpileSize());
    }

    @Test
    void checkCardIsValid() {
        Player tempPlayer = new Player();
        for(int i = 0; i < 4; i++) {
            tempPlayer.pushStockpile(new PanCard(PanCard.Color.getColor(i), PanCard.Value.getValue(i)));
        }
        PanCard tempCard = new PanCard(PanCard.Color.getColor(0), PanCard.Value.getValue(2));
        PanCard tempCardTwo = new PanCard(PanCard.Color.getColor(3), PanCard.Value.getValue(4));
        PanCard tempCardThree = new PanCard(PanCard.Color.getColor(2), PanCard.Value.getValue(5));
        Assert.assertFalse(tempPlayer.checkCardIsValid(tempCard));
        Assert.assertTrue(tempPlayer.checkCardIsValid(tempCardTwo));
        Assert.assertTrue(tempPlayer.checkCardIsValid(tempCardThree));
    }

    @Test
    void lastColorOnStockpile() {
        Player tempPlayer = new Player();
        for(int i = 0; i < 4; i++) {
            tempPlayer.pushStockpile(new PanCard(PanCard.Color.getColor(i), PanCard.Value.getValue(i)));
        }
        tempPlayer.popStockpile();
        Assert.assertFalse(tempPlayer.lastColorOnStockpile());
        tempPlayer.popStockpile();
        Assert.assertTrue(tempPlayer.lastColorOnStockpile());
    }

    @Test
    void deleteCardFromHand() {
        Player tempPlayer = new Player();
        for(int i = 0; i < 4; i++) {
            tempPlayer.addCardToHand(new PanCard(PanCard.Color.getColor(i), PanCard.Value.getValue(i)));
        }
        tempPlayer.deleteCardFromHand(new PanCard(PanCard.Color.getColor(2), PanCard.Value.getValue(3)));
        Assert.assertEquals(4,tempPlayer.getHandOfCards().size());
        tempPlayer.deleteCardFromHand(new PanCard(PanCard.Color.getColor(2), PanCard.Value.getValue(2)));
        Assert.assertEquals(3,tempPlayer.getHandOfCards().size());
        Assert.assertEquals(3,tempPlayer.getHandOfCards().get(2).getValueInt());
    }
}