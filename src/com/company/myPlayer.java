//package com.company;
//
//import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.net.InetAddress;
//import java.net.Socket;
//import java.util.Scanner;
//
//public class myPlayer
//{
//    public static void main(String[] args) throws IOException
//    {
//        try
//        {
//            Scanner scn = new Scanner(System.in);
//
//            // getting localhost ip
//            InetAddress ip = InetAddress.getByName("localhost");
//
//            // establish the connection with server port 5056
//            Socket s = new Socket(ip, 5056);
//
//            // obtaining input and out streams
//            DataInputStream dis = new DataInputStream(s.getInputStream());
//            DataOutputStream dos = new DataOutputStream(s.getOutputStream());
//
//            // the following loop performs the exchange of
//            // information between client and client handler
//            while (true)
//            {
//                System.out.println(dis.readUTF());
//                String numOfPlayer = scn.nextLine();
//                int numOfPl = Integer.parseInt(numOfPlayer);
//                dos.writeInt(numOfPl);
//            }
//
//        }catch(IOException ex){
//            System.out.println("Exception from myPlayer sending()");
//        }
//    }
//}