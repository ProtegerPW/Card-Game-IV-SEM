package com.company;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;


public class Player {

    //part for player info
    private int playerID;
    private int enemyPlayer;
    private boolean buttonsEnable;
    private int cardBuffer[][];


    private ClientSideConnection csc;

    public void connectToSever() {
        csc = new ClientSideConnection();
    }

    public void doSth(){
        csc.setPlayersNumber();
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
//                try {
//                    int startGameFlag = 0;
//                    while (true) {
//                        System.out.println("infinite loop");
//                        startGameFlag = dataIn.readInt();
//                        if (startGameFlag == 1) break;
//                    }
//                    System.out.println("Go out of infinite loop");
//                } catch (IOException ex) {
//                    System.out.println("Exception from exit infi loop");
//                }

                int cardNumber = dataIn.readInt();
                System.out.println("number of cards " + cardNumber );
                cardBuffer = new int[cardNumber][2];
                for(int i = 0; i < cardNumber; i++) {
                    cardBuffer[i][0] = dataIn.readInt();
                    cardBuffer[i][1] = dataIn.readInt();
                }
//                PanCard sorting = new PanCard();
//                sorting.sortTable(cardBuffer);

                PanCard.sortTable(cardBuffer);

                for(int i = 0; i < cardNumber; i++) {
                    System.out.println(cardBuffer[i][0] + " " + cardBuffer[i][1]);
                }
                TimeUnit.SECONDS.sleep(20);

            } catch (IOException | InterruptedException ex) {
                System.out.println("IOException from readCards() ");
            }
        }

    }

    public static void main(String[] args) {
        Player p = new Player();
        p.connectToSever();
        if(p.playerID == 1) {
            p.csc.setPlayersNumber();
        }
        //System.out.println("TESTESTSET");
        p.csc.readCards();


    }
}

//testetstes