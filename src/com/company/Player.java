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
import java.util.concurrent.TimeUnit;


public class Player {
    //part for player info
    private int playerID;
    private int currentPlayer;
    private int turn;
    private boolean buttonsEnable;
    private ArrayList<PanCard> handOfCards;
    private ArrayList<PanCard> stockpile;
    private PanCard topCard;
    private int[] cardCount;

    private ClientSideConnection csc;

    public Player() {
        playerID = -1;
        handOfCards = new ArrayList<PanCard>();
    }

    public int getPlayerID() {
        return playerID;
    }

    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }

    public ClientSideConnection getCsc() {
        return csc;
    }

    public void setTopCard(PanCard topCard) {
        this.topCard = topCard;
    }

    public void connectToServer() {
        csc = new ClientSideConnection();
    }

    public void setUpButtons() {
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JButton b = (JButton) actionEvent.getSource();

            }
        };
    }

    public void setPlayersNumber(int playerNum) {
        try {
            csc.dataOut.writeInt(playerNum);
            csc.dataOut.flush();
        } catch (IOException ex) {
            System.out.println("IOException from setPlayerNum() CSC");
        }
    }

    //Client connections
    public class ClientSideConnection {
        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;

        public ClientSideConnection() {
            System.out.println("---- Client ----");
            try {
                socket = new Socket("localhost", 55557);
                socket.setSoTimeout(30*1000);
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
                System.out.println("Constructed");
                setPlayerID(dataIn.readInt());
                System.out.println("Connected to server as player number " + playerID);
            } catch (IOException ex) {
                System.out.println("IO Exception from CSC constructor");
            }
        }

        public void readCards() {
            try {
                int cardNumber = dataIn.readInt();
                System.out.println("number of cards " + cardNumber );
                initCardCount(cardNumber);
                for(int i = 0; i < cardNumber; i++) {
                    int color = dataIn.readInt();
                    int value = dataIn.readInt();
                    PanCard cardBuffer = new PanCard(PanCard.Color.getColor(color), PanCard.Value.getValue(value));
                    handOfCards.add(cardBuffer);
//                    cardBuf[0] = dataIn.readInt();
//                    cardBuf[1] = dataIn.readInt();
//                    handOfCards.add(cardBuf)
                }
                if(dataIn.readInt() == 1) {
                    System.out.println("You begin!");
                }
                PanCard.sortTable(handOfCards);

                for(int i = 0; i < cardNumber; i++) {
                    System.out.println(handOfCards.get(i).getColorInt() + " " + handOfCards.get(i).getValueInt());
                }
                TimeUnit.SECONDS.sleep(20);

            } catch (IOException | InterruptedException ex) {
                System.out.println("IOException from readCards() ");
            }
        }
    }

    private void initCardCount(int cardNumber) {
        if(6 == cardNumber)
            cardCount = new int[]{6, 6, 6, 6};
        else
            cardCount = new int[]{12, 12};
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

    public boolean checkCardIsValid(PanCard card) {
        return stockpile.get(stockpile.size() - 1).getValueInt() <= card.getValueInt();
    }

    public void deleteCardFromHand(PanCard card) {
        handOfCards.remove(card);
    }

    public void sendAndDeleteCard(String text, ArrayList<PanCard> cards) {
        try {
            csc.dataOut.writeUTF(text);
            csc.dataOut.writeInt(cards.size());
            //eventually add csc.dataOut.flush();
            for (PanCard card : cards) {
                csc.dataOut.writeInt(card.getColorInt());
                csc.dataOut.writeInt(card.getValueInt());
                deleteCardFromHand(card);
            }
            csc.dataOut.flush();
        } catch(IOException ex) {
            System.out.println("IOException from sendAndDeleteCard() ");
        }
    }

    public PanCard readCard() {
        PanCard tempCard = new PanCard(PanCard.Color.getColor(0),PanCard.Value.getValue(0));
        try {
            return new PanCard(PanCard.Color.getColor(csc.dataIn.readInt()), PanCard.Value.getValue(csc.dataIn.readInt()));
        } catch (IOException ex) {
            System.out.println(" IOException from readCard() ");
        }
        return tempCard;
    }

    public void receiveUpdate() {
        try {
            int numOfCards = csc.dataIn.readInt(); //if numOfCards == -1 it means the opponent draw cards
            if(numOfCards < 0) {
                for(int i = 0; i < 3; i++) {
                    if(stockpile.size() == 1) break;
                    stockpile.remove(stockpile.size() - 1);
                }
            } else {
                for(int i = 0; i < numOfCards; i++) {
                  stockpile.add(readCard());
                }
            }
        } catch(IOException ex) {
            System.out.println(" IOException from receiveUpdate() ");
        }
    }

    public void sendCommunicate(String text, ArrayList<PanCard> cards) {
        try {
            switch(text) {
                case "addCards":
                    sendAndDeleteCard(text, cards);
                    break;

                case "drawCards":
                    csc.dataOut.writeUTF(text);
                    csc.dataOut.flush();
                    int numOfDrawCards = csc.dataIn.readInt();
                    for(int i = 0; i < numOfDrawCards; i++) {
                        int color = csc.dataIn.readInt();
                        int value = csc.dataIn.readInt();
                        PanCard tempListOfCard = new PanCard(PanCard.Color.getColor(color),PanCard.Value.getValue(value));
                        handOfCards.add(tempListOfCard);
                    }
                    PanCard.sortTable(handOfCards);
                    break;
            }

        } catch(IOException ex){
            System.out.println("IOException from sendCommunicate() ");
        }
    }



    // TODO private void drawCards();
        // draw = true;
    // player.drawCards();

    // TODO private bool selectedCard(PanCard card); true -> może zrobić więcej; false -> nie może zrobić więcej - ruch zakończony
    // if(player.selectedCard(card)) player.getMultipleCards(ArrayList<PanCard>);
    // canPlay(card); true -> rzuć, false -> nic nie rób


    public static void main(String[] args) {
        Player player = new Player();
        Menu menu = new Menu();
        Controller controller = new Controller(player, menu);
        controller.initController();
        // TODO frameController.obtainData(p);
        // TODO p <- action (draw/play 1 card/play multiple cards)
        // TODO communicate with server
        // TODO frameController.update(), p.update();
    }
}