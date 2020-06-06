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
    private PanCard selectedCard;
    private ArrayList<PanCard> stockpile;
    private PanCard topCard;

    public Player() {
        playerID = -1;
        handOfCards = new ArrayList<>();
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public ArrayList<PanCard> getHandOfCards() {
        return handOfCards;
    }

    public ArrayList<PanCard> getReversedHandOfCards() {
        ArrayList<PanCard> reversedHandOfCards = new ArrayList<>(handOfCards);
        Collections.reverse(reversedHandOfCards);
        return reversedHandOfCards;
    }

    public void setHandOfCards(ArrayList<PanCard> handOfCards) {
        this.handOfCards = handOfCards;
    }

    public int[] getCardCount() {
        return cardCount;
    }

    public void setCardCount(int[] cardCount) {
        this.cardCount = cardCount;
    }

    public PanCard getSelectedCard() {
        return selectedCard;
    }

    public void setSelectedCard(PanCard selectedCard) {
        this.selectedCard = selectedCard;
    }

    public void setTopCard(PanCard topCard) {
        this.topCard = topCard;
    }

    public void initCardCount(int cardNumber) {
        if(6 == cardNumber)
            cardCount = new int[]{6, 6, 6, 6};
        else
            cardCount = new int[]{12, 12};
    }

    public void addCardToHand(PanCard card) {
        handOfCards.add(card);
    }

    public void sortHand() {
        PanCard.sortTable(handOfCards);
    }

    public void printHand() {
        for(int i = 0; i < cardCount[playerID - 1]; i++) {
            System.out.println(handOfCards.get(i).getColorInt() + " " + handOfCards.get(i).getValueInt());
        }
    }

    public boolean checkMultiCard(PanCard card) {
        if(checkCardIsValid(card)) {
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
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public int getStockpileSize() {
        return stockpile.size();
    }

    public void popStockpile() {
        stockpile.remove(stockpile.size() - 1);
    }

    public void pushStockpile(PanCard card) {
        stockpile.add(card);
    }

    public boolean checkCardIsValid(PanCard card) {
        return stockpile.get(stockpile.size() - 1).getValueInt() <= card.getValueInt();
    }

    public void deleteCardFromHand(PanCard card) {
        handOfCards.remove(card);
    }

    // TODO private void drawCards();
        // draw = true;
    // player.drawCards();

    // TODO private bool selectedCard(PanCard card); true -> może zrobić więcej; false -> nie może zrobić więcej - ruch zakończony
    // if(player.selectedCard(card)) player.getMultipleCards(ArrayList<PanCard>);
    // canPlay(card); true -> rzuć, false -> nic nie rób


//    public static void main(String[] args) {
//        Player player = new Player();
//        Menu menu = new Menu();
//        Controller controller = new Controller(player, menu);
//        controller.initController();
//        // TODO frameController.obtainData(p);
//        // TODO p <- action (draw/play 1 card/play multiple cards)
//        // TODO communicate with server
//        // TODO frameController.update(), p.update();
//    }
}