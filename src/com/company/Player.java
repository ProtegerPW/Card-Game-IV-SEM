package com.company;

import ui.controller.Controller;
import ui.view.Menu;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;


public class Player {
    //part for player info
    private int playerID;
    private int currentPlayer;
    private int turn;
    private boolean buttonsEnable;
    private ArrayList<PanCard> handOfCards;


    private int[] cardCount;
    private ArrayList<PanCard> selectedCards;
    private ArrayList<PanCard> stockpile;

    public Player() {
        playerID = -1;
        handOfCards = new ArrayList<>();
        selectedCards = new ArrayList<>();
        stockpile = new ArrayList<>();
    }

    public int getPlayerID() { return playerID; }

    public void setPlayerID(int playerID) { this.playerID = playerID; }

    public int getCurrentPlayer() { return currentPlayer; }

    public void setCurrentPlayer(int numOfPlayer) { this.currentPlayer = numOfPlayer;}

    public void setNextPlayer() {
        if(lastColorOnStockpile()) {
            if(currentPlayer == 1) {
                currentPlayer = cardCount.length;
            } else {
                currentPlayer--;
            }
        } else {
            if(cardCount.length == 2 && currentPlayer == 2 || cardCount.length == 4 && currentPlayer == 4) {
                currentPlayer = 1;
            } else {
                currentPlayer++;
            }
        }
    }

    public ArrayList<PanCard> getHandOfCards() { return handOfCards; }

    public void setHandOfCards(ArrayList<PanCard> handOfCards) { this.handOfCards = handOfCards; }

    public ArrayList<PanCard> getReversedHandOfCards() {
        ArrayList<PanCard> reversedHandOfCards = new ArrayList<>(handOfCards);
        Collections.reverse(reversedHandOfCards);
        return reversedHandOfCards;
    }

    public int[] getCardCount() { return cardCount; }

    public void setCardCount(int[] cardCount) { this.cardCount = cardCount; }

    public ArrayList<PanCard> getSelectedCards() { return selectedCards; }

    public void pushCardToSelected(PanCard clickedCard) { this.selectedCards.add(clickedCard); }

    public void removeCardFromSelected(PanCard clickedCard) {
        selectedCards.remove(clickedCard);
    }

    public void resetSelectedCards() {
        selectedCards.clear();
    }

    public void initCardCount(int cardNumber) {
        if(6 == cardNumber)
            cardCount = new int[]{6, 6, 6, 6};
        else
            cardCount = new int[]{12, 12};
    }

    public void changeCardCount(int playerID, int direction) {
        if (direction < 0) {
            this.cardCount[playerID - 1]--;
        } else {
            this.cardCount[playerID - 1]++;
        }
    }

    public void addCardToHand(PanCard card) { handOfCards.add(card); }

    public void sortHand() { PanCard.sortTable(handOfCards); }

    public void printHand() {
        for(int i = 0; i < cardCount[playerID - 1]; i++) {
            System.out.println(handOfCards.get(i).getColorInt() + " " + handOfCards.get(i).getValueInt());
        }
    }

    public boolean checkMultiCard(PanCard card) {   // TODO 3 dziewiątki bez kieru / 4 dziewiątki z kierem
        int multipleCards = 0;
        for(int i = 0; i < handOfCards.size(); i++) {
            if(card.getValueInt() == handOfCards.get(i).getValueInt()) {
                multipleCards++;
            }
        }
        if(card.getValueInt() == 0 && multipleCards == 3) {
            return true;
        } else if (multipleCards == 4) {
            return true;
        }
        return false;
    }

    public int getStockpileSize() { return stockpile.size(); }

    public void popStockpile() { stockpile.remove(stockpile.size() - 1); }

    public void pushStockpile(PanCard card) { stockpile.add(card); }

    public boolean checkCardIsValid(PanCard card) {
        if(stockpile.size() == 0)
            return checkCardIsHeartNine(card);
        return stockpile.get(stockpile.size() - 1).getValueInt() <= card.getValueInt();
    }

    public boolean checkCardIsHeartNine(PanCard card) {
        if(card.getColorInt() == 0) {
            if(card.getValueInt() == 0) {
                return true;
            }
        }
        return false;
    }

    public boolean lastColorOnStockpile() {
        if(stockpile.get(stockpile.size() - 1).getColorInt() == 1) {
            return true;
        } else {
            return false;
        }
    }

    public void deleteCardFromHand(PanCard card) { handOfCards.remove(card); }
}