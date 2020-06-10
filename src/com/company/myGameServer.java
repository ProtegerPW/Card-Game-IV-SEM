//package com.company;
//
//import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
//import java.io.*;
//import java.net.*;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//public class myGameServer {
//    private ServerSocket ss;
//    private int numOfPlayers;
//    private int numConPlayers;
//
//
//    public myGameServer() {
//        System.out.println("---- SERVER ----");
//        numOfPlayers = 2;
//        numConPlayers = 0;
//        try {
//            ss = new ServerSocket(5056);
//
//        } catch (IOException ex) {
//            System.out.println("Exception from Server Constructor");
//        }
//    }
//
//    public void acceptConnection() {
//
//        try {
//            System.out.println("Waiting for connection ...");
//            while (numConPlayers != numOfPlayers) {
//
//                Socket s = ss.accept();
//
//                System.out.println("A new client is connected : " + s);
//                numConPlayers++;
//
//                DataInputStream dis = new DataInputStream(s.getInputStream());
//                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
//
//                System.out.println("Assigning new thread for this client");
//                Thread t = new ClientHandler(s, dis, dos);
//
//                t.start();
//            }
//            } catch (IOException ex) {
//                System.out.println("Exception from acceptConnection() ");
//            }
//    }
//
//
//    // ClientHandler class
//    private class ClientHandler extends Thread {
//        final DataInputStream dis;
//        final DataOutputStream dos;
//        final Socket s;
//
//
//        // Constructor
//        public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos) {
//            this.s = s;
//            this.dis = dis;
//            this.dos = dos;
//        }
//
//        private void closeStreams() {
//            try {
//                this.dis.close();
//                this.dos.close();
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public void run() {
//            String received;
//            String toreturn;
//            while (true) {
//                try {
//
//                    // Ask user what he wants
//                    dos.writeUTF("Type number of players: ");
//
//                    // receive the answer from client
//                    numOfPlayers = dis.readInt();
//                    System.out.println("The number of players is " +numOfPlayers);
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//
//    public static void main(String[] args) throws IOException {
//        myGameServer gs = new myGameServer();
//        gs.acceptConnection();
//    }
//}
