package com.company;

import ui.view.GameView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

//Client connections
public class ClientSideConnection {
    private Player player;
    //
    private Socket socket;
    private DataInputStream dataIn;
    private DataOutputStream dataOut;

    public ClientSideConnection(Player p) {
        player = p;
    }

    public void connectToServer() {
        try {
            System.out.println("---- Client ----");
            socket = new Socket("localhost", 55557);    // create new connection
            socket.setSoTimeout(30*1000);                         // set idle time to 30 seconds
            dataIn = new DataInputStream(socket.getInputStream());
            dataOut = new DataOutputStream(socket.getOutputStream());
            System.out.println("Constructed");
            player.setPlayerID(dataIn.readInt());                 // wait for server to deliver ID
            System.out.println("Connected to server as player number " + player.getPlayerID());
        } catch (IOException ex) {
            System.out.println("IO Exception from CSC constructor");
        }
    }

        // obtain initial hand from server
    public void getPlayerInitialHand() {
        try {
            int cardNumber = dataIn.readInt();
            System.out.println("number of cards " + cardNumber );
            player.initCardCount(cardNumber);
            for(int i = 0; i < cardNumber; i++) {
                int color = dataIn.readInt();
                int value = dataIn.readInt();
                PanCard cardBuffer = new PanCard(PanCard.Color.getColor(color), PanCard.Value.getValue(value));
                player.addCardToHand(cardBuffer);
            }
            if(dataIn.readInt() == 1) {
                System.out.println("You begin!");
            }
            player.sortHand();
            player.printHand();
        } catch (IOException ex) {
            System.out.println("IOException from readCards() ");
        }
    }

        // send players number to server
    public void setPlayersNumber(int playerNum) {
        try {
            dataOut.writeInt(playerNum);
            dataOut.flush();
        } catch (IOException ex) {
            System.out.println("IOException from setPlayerNum() CSC");
        }
    }

    public void sendAndDeleteCard(String text, ArrayList<PanCard> cards) {
        try {
            dataOut.writeUTF(text);
            dataOut.writeInt(cards.size());
            // optionally: add csc.dataOut.flush();
            for(int i = 0; i < cards.size(); i++) {
                int color = cards.get(i).getColorInt();
                int value = cards.get(i).getValueInt();
                dataOut.writeInt(color);
                dataOut.writeInt(value);
                player.pushStockpile(new PanCard(PanCard.Color.getColor(color), PanCard.Value.getValue(value)));
                player.deleteCardFromHand(cards.get(i));
            }
            dataOut.flush();
        } catch(IOException ex) {
            System.out.println("IOException from sendAndDeleteCard() ");
        }
    }

    public void receiveUpdate() {
        try {
            int numOfCards = dataIn.readInt(); //if numOfCards == -1 it means the opponent draws cards
            if(numOfCards < 0) {
                for(int i = 0; i < 3; i++) {
                    if(player.getStockpileSize() == 1) break;
                    player.popStockpile();
                    player.changeCardCount(player.getCurrentPlayer(), numOfCards);
                }
            } else {
                for(int i = 0; i < numOfCards; i++) {
                    player.pushStockpile(readCard());
                    player.changeCardCount(player.getCurrentPlayer(), numOfCards);
                }
            }
            player.setCurrentPlayer(player.lastColorOnStockpile());
        } catch(IOException ex) {
            System.out.println(" IOException from receiveUpdate() ");
        }
    }

    public PanCard readCard() {
        PanCard tempCard = new PanCard(PanCard.Color.getColor(0),PanCard.Value.getValue(0));
        try {
            return new PanCard(PanCard.Color.getColor(dataIn.readInt()), PanCard.Value.getValue(dataIn.readInt()));
        } catch (IOException ex) {
            System.out.println(" IOException from readCard() ");
        }
        return tempCard;
    }

    public void sendCommunicate(String text, ArrayList<PanCard> cards) {
        try {
            int color = 0;
            int value = 0;
            switch(text) {
                case "addCards":
                    sendAndDeleteCard(text, cards);
                    break;

                case "drawCards":
                    dataOut.writeUTF(text);
                    dataOut.flush();
                    int numOfDrawCards = dataIn.readInt();
                    for(int i = 0; i < numOfDrawCards; i++) {
                        //TODO can be optimized: readInt() into PanCard constructor & new PanCard as argument to addCardToHand()
                        color = dataIn.readInt();
                        value = dataIn.readInt();
                        PanCard tempListOfCard = new PanCard(PanCard.Color.getColor(color),PanCard.Value.getValue(value));
                        player.addCardToHand(tempListOfCard);
                        player.popStockpile();
                    }
                    player.sortHand();
                    break;
            }

        } catch(IOException ex){
            System.out.println("IOException from sendCommunicate() ");
        }
    }
}