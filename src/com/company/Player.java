package com.company;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ui.controller.FrameController;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
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

    public Player() {
        handOfCards = new ArrayList<ArrayList<Integer>>();
    }

    public void setTopCard(PanCard topCard) {
        this.topCard = topCard;
    }

    private ClientSideConnection csc;

    public void connectToSever() {
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

    //Client connections
    private class ClientSideConnection {
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
                playerID = dataIn.readInt();
                System.out.println("Connected to server as player number " + playerID);
            } catch (IOException ex) {
                System.out.println("IO Exception from CSC constructor");
            }
        }

        public void setPlayersNumber() {
            try {
                Scanner keyboard = new Scanner(System.in);
                System.out.println(dataIn.readUTF());
                int playerNum = 0;
                while( playerNum != 2 && playerNum != 4 ) {
                    playerNum = keyboard.nextInt();
                    if( playerNum != 2 && playerNum != 4 )
                        System.out.println("Error. Please select 2 or 4 players");
                }
                dataOut.writeInt(playerNum);
                dataOut.flush();
            } catch (IOException ex) {
                System.out.println("IOException from setPlayerNum() CSC");
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

    public static void main(String[] args) {
        Player p = new Player();
        p.connectToSever();
        if(p.playerID == 1) {
            p.csc.setPlayersNumber();
        }
        FrameController frameController = new FrameController();
        // TODO frameController.obtainData(p);
        // TODO p <- action (draw/play 1 card/play multiple cards)
        // TODO communicate with server
        // TODO frameController.update(), p.update();
        p.csc.readCards();
    }
}