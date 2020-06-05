package com.company;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;


public class GameServer {

    private ServerSocket ss;
    private int numPlayers;
    private int numConPlayers;
    private ServerSideConnection[] players;

    private PanDeck gameDeck;
    private ArrayList<ArrayList<PanCard>> playerHand;
    private ArrayList<PanCard> stockpile;
    private PanCard.Color validColor;
    private PanCard.Value validValue;

    int currentPlayer;

    public GameServer() {
        System.out.println("---- Game Server ----");
        numPlayers = 1;
        numConPlayers = 0;
        players = new ServerSideConnection[4];
        gameDeck = new PanDeck();
        gameDeck.shuffle();
        stockpile = new ArrayList<PanCard>();
        playerHand = new ArrayList<ArrayList<PanCard>>(); // tablica tablic z kartami graczy

        try {
            ss = new ServerSocket(55557);
        } catch (IOException ex) {
            System.out.println("IOException from GameSever Constructor");
        }
    }

    public void waitForHost() {
        try {
            System.out.println("Waiting for host...");
            Socket s = ss.accept();
            System.out.println("Player #1 has connected");
            ServerSideConnection ssc = new ServerSideConnection(s, 1);
            players[0] = ssc;
            try {
                ssc.dataOut.writeInt(1);
                ssc.dataOut.flush();
                numPlayers = ssc.dataIn.readInt();
                System.out.println("Number of players is " + numPlayers);
            } catch (IOException ex) {
                System.out.println("IOException from input Player number");
            }
            for(int i = 0; i < numPlayers; i++) {
                ArrayList<PanCard> hand = new ArrayList<PanCard>(Arrays.asList(gameDeck.drawCard(gameDeck.getLength() / numPlayers)));
                playerHand.add(hand);
                System.out.println("Created hand #" + (i + 1));
            }
            numConPlayers++;
            Thread t = new Thread(ssc);
            t.start();
        } catch (IOException ex) {
            System.out.println("IOException from acceptConnections");
        }
    }

    public void acceptConnections() {
        try {
            System.out.println("Waiting for connections ...");
            while (numConPlayers != numPlayers) {
                Socket s = ss.accept();
                numConPlayers++;
                System.out.println("Player #" + numConPlayers + " has connected");

                ServerSideConnection ssc = new ServerSideConnection(s, numConPlayers);
                if (numConPlayers == 2){
                    players[1] = ssc;
                } else if (numConPlayers == 3){
                    players[2] = ssc;
                } else {
                    players[3] = ssc;
                }
                ssc.dataOut.writeInt(ssc.playerID);
                ssc.dataOut.flush();
                Thread t = new Thread(ssc);
                t.start();
            }
        } catch (IOException ex) {
            System.out.println("IOException from acceptConnections");
        }
    }

    public void closeConnection() {
        try {
            ss.close();
        } catch (IOException ex) {
            System.out.println("Exception from closeConnection() ");
        }
    }

    private class ServerSideConnection implements Runnable {
        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        private int playerID;

        public ServerSideConnection(Socket s, int id) {
            socket = s;
            playerID = id;
            try {
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
            } catch (IOException ex) {
                System.out.println("IOException from run SSC");
            }
        }

        public void deleteCardFromHand(PanCard card, int playerID) {
            if(playerHand.get(playerID - 1).contains(card)) playerHand.remove(card);
        }

        public void drawCard(int numOfCards) {
            try {
                for (int i = 0; i < numOfCards; i++) {
                    dataOut.writeInt(stockpile.get(stockpile.size() - 1).getColorInt());
                    dataOut.writeInt(stockpile.get(stockpile.size() - 1).getValueInt());
                    stockpile.remove(stockpile.size() - 1);
                }
                dataOut.flush();
            } catch (IOException ex) {
                System.out.println("IOException from drawCard() ");
            }
        }

        public void addCardToStockpile(PanCard card){
            stockpile.add(card);
        }

        public void readAndUpdateCardStatus(int playerID) {
            try{
                int numOfCards = dataIn.readInt();
                for (int i = 0; i < numOfCards; i ++) {
                    int color = dataIn.readInt();
                    int value = dataIn.readInt();
                    PanCard tempCard = new PanCard(PanCard.Color.getColor(color), PanCard.Value.getValue(value));
                    deleteCardFromHand(tempCard, playerID);
                    addCardToStockpile(tempCard);
                }
            } catch(IOException ex) {
                System.out.println("IOException from readAndUpdateCardStatus() ");
            }
        }

        public void performOperation(String text, int playerID) {
            try {
                switch (text) {

                    case "addCards":
                        readAndUpdateCardStatus(playerID);
                        break;

                    case "drawCard":
                        int tempStockSize = 0;
                        if(stockpile.size() > 3) {
                            tempStockSize = 3;
                        } else if(stockpile.size() == 3) {
                            tempStockSize = 2;
                        } else if(stockpile.size() == 2) {
                            tempStockSize = 1;
                        }
                        dataOut.writeInt(tempStockSize);
                        dataOut.flush();
                        drawCard(tempStockSize);
                        break;
                }
            }catch (IOException ex) {
                System.out.println("IOException from performOperation() ");
            }
        }

        public void run() {
            try {
                int numOfCards = (gameDeck.getLength() / numPlayers);
                int playerFlag = 0;
                dataOut.writeInt(numOfCards);
                dataOut.flush();

                for(int i = 0; i < numOfCards; i++) {
                    PanCard tempCard = playerHand.get(playerID - 1).get(i);
                    dataOut.writeInt(tempCard.getColorInt());
                    dataOut.writeInt(tempCard.getValueInt());
                    if(tempCard.getColorInt() == 0 && tempCard.getValueInt() == 0) {
                        currentPlayer = playerID;
                        System.out.println("First player is " + playerID);
                        playerFlag = 1;
                    }
                }
                dataOut.writeInt(playerFlag);
                dataOut.flush();

                while(true) { //TODO send players number of cards of theri opponents
                            //TODO send player new stockpile
                    if(playerID == 1) {
                        String readText = dataIn.readUTF();
                        performOperation(readText, playerID);

                    } else if(playerID == 2) {
                        String readText = dataIn.readUTF();
                        performOperation(readText, playerID);
                    }
                    if(numConPlayers == 2) continue;
                    if(playerID == 3) {
                        String readText = dataIn.readUTF();
                        performOperation(readText, playerID);
                    } else if(playerID == 4) {
                        String readText = dataIn.readUTF();
                        performOperation(readText, playerID);
                    }
                }
                //TODO send info to other player about number of cards in hand

            } catch (IOException ex) {
                System.out.println("IOException from run() SSC");
            }
        }

    }

    public static void main(String[] args) {
        GameServer gs = new GameServer();
        gs.waitForHost();
        gs.acceptConnections();
        //gs.closeConnection();
    }
}