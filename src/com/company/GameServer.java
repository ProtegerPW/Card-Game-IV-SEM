package com.company;

import java.io.*;
import java.net.*;

public class GameServer {

    private ServerSocket ss;
    private int numPlayers;
    private int numConPlayers;
    private ServerSideConnection player1;
    private ServerSideConnection player2;
    private ServerSideConnection player3;
    private ServerSideConnection player4;



    public GameServer() {
        System.out.println("---- Game Server ----");
        numPlayers = 2;
        numConPlayers = 0;
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
                        }
                    } catch (IOException ex) {
                        System.out.println("IOException from input PLayer number");
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
