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




    public GameServer() {
        System.out.println("---- Game Server ----");
        numPlayers = 2;
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

    public void acceptConnections() {
        try {
            System.out.println("Waiting for connections ...");
            while (numConPlayers != numPlayers) {
                Socket s = ss.accept();
                numConPlayers++;
                System.out.println("Player #" + numConPlayers + " has connected");

                ServerSideConnection ssc = new ServerSideConnection(s, numConPlayers);
                if (numConPlayers == 1) {
                    player1 = ssc;
                } else if (numConPlayers == 2){
                    player2 = ssc;
                } else if (numConPlayers == 3){
                    player3 = ssc;
                } else {
                    player4 = ssc;
                }
                Thread t = new Thread(ssc);
                t.start();
            }
        } catch (IOException ex) {
            System.out.println("IOException from acceptConnections");
        }
//        for(int i = 0; i < numPlayers; i++) {
//            ArrayList<PanCard> hand = new ArrayList<PanCard>(Arrays.asList(gameDeck.drawCard(gameDeck.getLength() / numPlayers)));
//            playerHand.add(hand);
//        }
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

        public void run() {
            try {
                dataOut.writeInt(playerID);
                dataOut.flush();
                try {
                    if (playerID == 1) {
                        dataOut.writeUTF("Type number of players");
                        numPlayers = dataIn.readInt();
                        System.out.println("Number of players is " + numPlayers);
                        for(int i = 0; i < numPlayers; i++) {
                            ArrayList<PanCard> hand = new ArrayList<PanCard>(Arrays.asList(gameDeck.drawCard(gameDeck.getLength() / numPlayers)));
                            playerHand.add(hand);
                        }
                    }
                } catch (IOException ex) {
                    System.out.println("IOException from input PLayer number");
                }

//                while(numConPlayers != numPlayers) {
//                    wait(100);
//                    //System.out.println(numConPlayers);
//                    if(numConPlayers == numPlayers) {
//                        break;
//                    }
//                };

//                notifyAll();
//                dataOut.writeInt(1);
//                dataOut.flush();

                int numOfCards = (gameDeck.getLength() / numPlayers);
                dataOut.writeInt(numOfCards);
                dataOut.flush();

                for(int i = 0; i < numOfCards; i++) {
                    PanCard tempCard = playerHand.get(playerID - 1).get(i);
                    dataOut.writeInt(tempCard.getColorInt());
                    dataOut.writeInt(tempCard.getValueInt());
                    dataOut.flush();
                }

            } catch (IOException ex) {
                System.out.println("IOException from run() SSC");
            }
        }

    }

    public static void main(String[] args) {
        GameServer gs = new GameServer();
        gs.acceptConnections();
        gs.closeConnection();
    }
}
