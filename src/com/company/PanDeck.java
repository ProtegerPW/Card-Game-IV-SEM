package com.company;

import java.util.Random;

public class PanDeck {
    private PanCard[] cards;
    private int cardsInDeck;

    public PanDeck() {
        cards = new PanCard[24];
        this.reset();
    }

    public void reset () {
        PanCard.Color[] colors = PanCard.Color.values();
        cardsInDeck = 0;

        for(int i = 0; i < colors.length; i++) {
            for(int j = 0; j < PanCard.Value.values().length; j++) {
                cards[cardsInDeck++] = new PanCard(PanCard.Color.getColor(i), PanCard.Value.getValue(j));
            }
        }
    }

    public void shuffle() {
        int n = cards.length;
        Random random = new Random();

        for(int i = 0; i < cards.length; i++) {
            int randomNum = i + random.nextInt(n - i );
            PanCard tempCard = cards[randomNum];
            cards[randomNum] = cards[i];
            cards[i] = tempCard;
        }
    }

    public int getLength() {
        return cards.length;
    }

    public PanCard[] drawCard(int cardNum) {
        if(cardNum < 0) {
            throw new IllegalArgumentException("Must draw positive cards!");
        }

        if(cardNum > cardsInDeck) {
            throw new IllegalArgumentException("Cannot draw more cards than it is in the deck");
        }

        PanCard[] tempDeck = new PanCard[cardNum];

        for(int i = 0; i < cardNum; i++) {
            tempDeck[i] = cards[--cardsInDeck];
        }
        return tempDeck;
    }

    public void deleteCard(int cardNum) {

    }
}
