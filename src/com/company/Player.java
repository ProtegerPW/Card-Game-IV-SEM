package com.company;

import java.io.*;
import java.net.*;
import java.util.Scanner;


public class Player {

    //part for player info
    private int playerID;
    private int enemyPlayer;
    private boolean buttonsEnable;

    private ClientSideConnection csc;

    public void connectToSever() {
        csc = new ClientSideConnection();
    }

    public void doSth(){
        csc.setPlayersNumber();
    }

    //Client conections
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
                int playerNum = keyboard.nextInt();
                dataOut.writeInt(playerNum);
                dataOut.flush();
            } catch (IOException ex) {
                System.out.println("IOException from setPlayerNum() CSC");
            }
        }

    }

    public static void main(String[] args) {
        Player p = new Player();
        p.connectToSever();
        if(p.playerID == 1) {
            p.doSth();
        }
    }
}

//testetstes