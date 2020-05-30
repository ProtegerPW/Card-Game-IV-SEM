package com.company;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;


public class GameServer {

    private ServerSocket ss;
    private int numPlayers;
    private int numConPlayers;
    private ServerSideConnection player1;
    private ServerSideConnection player2;
    private ServerSideConnection player3;
    private ServerSideConnection player4;

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
            player1 = ssc;
            try {
                ssc.dataOut.writeInt(1);
                ssc.dataOut.flush();
                ssc.dataOut.writeUTF("Type number of players");
                numPlayers = ssc.dataIn.readInt();
                System.out.println("Number of players is " + numPlayers);
            } catch (IOException ex) {
                System.out.println("IOException from input Player number");
            }
            for(int i = 0; i < numPlayers; i++) {
                ArrayList<PanCard> hand = new ArrayList<PanCard>(Arrays.asList(gameDeck.drawCard(gameDeck.getLength() / numPlayers)));
                playerHand.add(hand);
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
                    player2 = ssc;
                } else if (numConPlayers == 3){
                    player3 = ssc;
                } else {
                    player4 = ssc;
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

        public void deleteElement(int color, int value, int playerID) {
            for(int i = 0; i < playerHand.get(playerID - 1).size(); i++ ) {
                if(playerHand.get(playerID - 1).get(i).getColorInt() == color ) {
                    if(playerHand.get(playerID - 1).get(i).getValueInt() == value) {
                        playerHand.get(playerID - 1).remove(i);
                        break;
                    }
                }
            }
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

        public void addCardToStockpile(int color, int value){
            PanCard tempCard = new PanCard(PanCard.Color.getColor(color), PanCard.Value.getValue(value));
            stockpile.add(tempCard);
        }

        public void performOperation(String text, int playerID) {
            try {
                switch (text) {

                    case "oneCard":
                        int[] tempBuf = {0, 0};
                        for (int i = 0; i < 2; i++) {
                            tempBuf[i] = dataIn.readInt();
                        }
                        deleteElement(tempBuf[0], tempBuf[1], playerID);
                        addCardToStockpile(tempBuf[0], tempBuf[1]);
                        break;

                    case "multiCard":
                        int numOfCards = dataIn.readInt();
                        tempBuf = new int[2 * numOfCards];
                        for (int i = 0; i < 2 * numOfCards; i+=2) {
                            tempBuf[i] = dataIn.readInt();
                            tempBuf[i+1] = dataIn.readInt();
                            deleteElement(tempBuf[i], tempBuf[i+1], playerID);
                            addCardToStockpile(tempBuf[i], tempBuf[i+1]);
                        }
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

                while(true) { //TODO implement method for each players + add condition for 2 / 4 players
                    if(playerID == 1) {
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