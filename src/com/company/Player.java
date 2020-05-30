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
    private int turn;
    private boolean buttonsEnable;
    private ArrayList<ArrayList<Integer>> handOfCards;
    private ArrayList<PanCard> stockpile;
    private PanCard topCard;
    private int[] cardCount;

    private ClientSideConnection csc;

    public Player() {
        playerID = -1;
        handOfCards = new ArrayList<ArrayList<Integer>>();
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
                    ArrayList<Integer> cardBuffer = new ArrayList<Integer>();
                    cardBuffer.add(dataIn.readInt());
                    cardBuffer.add(dataIn.readInt());
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
                    System.out.println(handOfCards.get(i).get(0) + " " + handOfCards.get(i).get(1));
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
                if(card.getValueInt() == handOfCards.get(i).get(1)) {
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

    public void addCardToHand(int color, int value) {

    }

    public void sendCommunicate(String text, ArrayList<PanCard> cards) {
        try {
            switch(text) {
                case "oneCard":
                    csc.dataOut.writeUTF(text);
                    csc.dataOut.writeInt(cards.get(0).getColorInt());
                    csc.dataOut.writeInt(cards.get(0).getValueInt());
                    csc.dataOut.flush();
                    break;

                case "multiCard":
                    csc.dataOut.writeUTF(text);
                    csc.dataOut.writeInt(cards.size());
                    for(int i = 0; i < cards.size(); i++) {
                        csc.dataOut.writeInt(cards.get(i).getColorInt());
                        csc.dataOut.writeInt(cards.get(i).getValueInt());
                    }
                    csc.dataOut.flush();
                    break;

                case "drawCard":
                    csc.dataOut.writeUTF(text);
                    csc.dataOut.flush();
                    int numOfDrawCards = csc.dataIn.readInt();
                    ArrayList<Integer> tempListOfCards;
                    for(int i = 0; i < numOfDrawCards; i++) {
                        tempListOfCards = new ArrayList<Integer>();
                        tempListOfCards.add(csc.dataIn.readInt());
                        tempListOfCards.add(csc.dataIn.readInt());
                        handOfCards.add(tempListOfCards);
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

    // TODO private bool selectedCard(PanCard card); true -> można rzucić więcej; false -> ruch zakończony
    // bool turn = player.selectedCard(card);
    // if(turn) ...W

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