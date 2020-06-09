package com.company;

import java.util.ArrayList;
import java.util.Collections;


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
        if(cardCount[currentPlayer - 1] == 0) {
            setNextPlayer();
        }
        System.out.println("Next player is: " + getCurrentPlayer());
    }

    public ArrayList<PanCard> getHandOfCards() { return handOfCards; }

    public void setHandOfCards(ArrayList<PanCard> handOfCards) { this.handOfCards = handOfCards; }

    public ArrayList<PanCard> getReversedHandOfCards() {
        ArrayList<PanCard> reversedHandOfCards = new ArrayList<>(handOfCards);
        Collections.reverse(reversedHandOfCards);
        return reversedHandOfCards;
    }

    public int[] getCardCount() { return cardCount; }

    public ArrayList<PanCard> getSelectedCards() { return selectedCards; }

    public void pushCardToSelected(PanCard clickedCard) { this.selectedCards.add(clickedCard); }

    public void removeCardFromSelected(PanCard clickedCard) {
        selectedCards.remove(clickedCard);
    }

    public void resetSelectedCards() {
        selectedCards.clear();
    }

    public ArrayList<PanCard> getStockpile() {
        return stockpile;
    }

    public void initCardCount(int cardNumber) {
        if(6 == cardNumber)
            cardCount = new int[]{6, 6, 6, 6};
        else
            cardCount = new int[]{12, 12};
    }

    public void setCardCount(int playerID, int change) {
        if (change < 0) {
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

    public boolean checkMultiCard(PanCard card) {
        int multipleCards = 0;
        for(int i = 0; i < handOfCards.size(); i++) {
            if(card.getValueInt() == handOfCards.get(i).getValueInt()) {
                multipleCards++;
            }
        }
        if(stockpile.size() == 0 && multipleCards == 4) {
            return true;
        }
        if(card.getValueInt() == 0 && card.getColorInt() != 0 && multipleCards == 3 ) {
            return true;
        } else return multipleCards == 4;
    }

    public int getStockpileSize() { return stockpile.size(); }

    public void popStockpile() { stockpile.remove(stockpile.size() - 1); }

    public void pushStockpile(PanCard card) { stockpile.add(card); }

    public boolean checkCardIsValid(PanCard card) {
        if(stockpile.size() == 0) {
            if(selectedCards.size() == 0) {
                return checkCardIsHeartNine(card);
            }
            else if(checkCardIsHeartNine(selectedCards.get(0))) {
                return card.getValueInt() == 0;
            }
            return false;
        }
        return stockpile.get(stockpile.size() - 1).getValueInt() <= card.getValueInt();
    }

    public boolean checkCardIsHeartNine(PanCard card) {
        return card.getColorInt() == 0 && card.getValueInt() == 0;
    }

    public boolean lastColorOnStockpile() {
        if(stockpile.get(stockpile.size() - 1).getColorInt() == 1) {
            return true;
        } else {
            return false;
        }
    }

    public void deleteCardFromHand(PanCard card) { handOfCards.remove(card); }

    public boolean checkIsEnd(){
        int calculateWinners = 0;
        for(int i = 0; i < cardCount.length; i++) {
            if(cardCount[i] == 0) calculateWinners++;
        }
        if(cardCount.length == 2 && calculateWinners == 1 ||
           cardCount.length == 4 && calculateWinners == 3) {
            return true;
        } else {
            return false;
        }
    }
}