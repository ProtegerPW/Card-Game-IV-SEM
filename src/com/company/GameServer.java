package com.company;

import com.fasterxml.jackson.databind.ObjectMapper;
import converters.ServerConverter;
import converters.ServerJasonConverter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
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
    private ServerJasonConverter gameServerJsonConverter;
    private int currentPlayer;

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

    public GameServer getGameServer() {
        return this;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(int currentPlayer) { this.currentPlayer = currentPlayer; }

    public int getNumOfPlayers() { return this.numPlayers; };

    public ArrayList<ArrayList<PanCard>> getPlayerHand() { return this.playerHand; }

    public ArrayList<PanCard> getStockpile() { return this.stockpile; }

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
            setFirstPlayer();
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
        System.out.println("Exit connection");
    }

    public void closeConnection() {
        try {
            ss.close();
        } catch (IOException ex) {
            System.out.println("Exception from closeConnection() ");
        }
    }

    public void setFirstPlayer() {
        for(int i = 0; i < numPlayers; i++) {
            for(PanCard card : playerHand.get(i)) {
                if(card.getValueInt() == 0 && card.getColorInt() == 0) {
                    setCurrentPlayer(i + 1);
                    System.out.println("First player is " + getCurrentPlayer());
                }
            }
        }
    }
//test class to implement ObjectMapper to read from .json file and create Object in Java
//    public class ItemDeserializer extends StdDeserializer<ServerConverter> {
//
//        public ItemDeserializer() {
//            this(null);
//        }
//
//        public ItemDeserializer(Class<?> vc) {
//            super(vc);
//        }
//
//        @Override
//        public ServerConverter deserialize(JsonParser jp, DeserializationContext ctxt)
//                throws IOException, JsonProcessingException {
//            JsonNode node = jp.getCodec().readTree(jp);
//            int numOfPlayers = (Integer) ((IntNode) node.get("numOfPlayers")).numberValue();
//            int currentPlayer = (Integer) ((IntNode) node.get("currentPlayer")).numberValue();
//            //JSONArray stockpile
//            String itemName = node.get("itemName").
//            int userId = (Integer) ((IntNode) node.get("createdBy")).numberValue();
//
//            return new Item(id, itemName, new User(userId, null));
//        }
//    }

    public void saveToJson() throws IOException {
        gameServerJsonConverter = new ServerJasonConverter("gameServer.json");
        gameServerJsonConverter.toJson(new ServerConverter(getGameServer()));
        //gameServerJsonConverter.fromJson().ifPresent(System.out::println);
//        Gson g = new Gson();
//        ServerConverter tempServer = g.fromJson("gameServer.json", ServerConverter.class);
//        System.out.println("Num of cards on stockpile is " + tempServer.getStockpile().size());
//        System.out.println("Num of cards for player #1 :" + tempServer.getPlayerHand().get(0).size());
        //Objectmapper tempMapper;
        ObjectMapper mapper = new ObjectMapper();
        ServerConverter tempServer = mapper.readValue(new File("/home/jakub/IdeaProjects/newCardGame"), ServerConverter.class);
        System.out.println("Num of cards on stockpile is " + tempServer.getStockpile().size());
        System.out.println("Num of cards for player #1 :" + tempServer.getPlayerHand().get(0).size());
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
            if(playerHand.get(playerID - 1).contains(card)) { playerHand.get(playerID - 1).remove(card); }
        }

        public void drawCard(int playerID, int numOfCards) {
            try {
                for (int i = 0; i < numOfCards; i++) {
                    dataOut.writeInt(stockpile.get(stockpile.size() - 1).getColorInt());
                    dataOut.writeInt(stockpile.get(stockpile.size() - 1).getValueInt());
                    playerHand.get(playerID - 1).add(stockpile.get(stockpile.size() - 1));
                    stockpile.remove(stockpile.size() - 1);
                }
                dataOut.flush();
                updateStatus(playerID, -1);
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
                updateStatus(playerID, numOfCards);
                System.out.println("Last card on stockpile: " + stockpile.get(stockpile.size() - 1).toString());
            } catch(IOException ex) {
                System.out.println("IOException from readAndUpdateCardStatus() ");
            }
        }

        public void performOperation(String text, int playerID) {
            setCurrentPlayer(playerID);
            try {
                switch (text) {

                    case "addCards":
                        readAndUpdateCardStatus(playerID);
                        break;

                    case "drawCards":
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
                        drawCard(playerID, tempStockSize);
                        break;
                }
            }catch (IOException ex) {
                System.out.println("IOException from performOperation() ");
            }
        }

        public void sendCurrentStockpile(int numOfCards) {
            try{
                if(numOfCards < 0) { //if there is a draw operation server doesn`t need to send new cards
                    dataOut.writeInt(-1);
                } else {
                    dataOut.writeInt(numOfCards);
                    for(int i = numOfCards; i > 0; i--) {
                        dataOut.writeInt(stockpile.get(stockpile.size() - i).getColorInt());
                        dataOut.writeInt(stockpile.get(stockpile.size() - i).getValueInt());
                    }
                }
                dataOut.flush();
            } catch (IOException ex) {
                System.out.println(" IOException from sendCurrentStockpile() ");
            }
        }

        public void updateStatus(int playerID, int numOfCards) {
            for(int i = 0; i < numConPlayers; i++) {
                if((i+1) == playerID) continue;
                players[i].sendCurrentStockpile(numOfCards);
                System.out.println("Send updateStatus() for " + (i+1) + " player");
            }
        }

        public void run() {
            try {
                int numOfCards = (gameDeck.getLength() / numPlayers);
                dataOut.writeInt(numOfCards);
                dataOut.flush();

                for(int i = 0; i < numOfCards; i++) {
                    PanCard tempCard = playerHand.get(playerID - 1).get(i);
                    dataOut.writeInt(tempCard.getColorInt());
                    dataOut.writeInt(tempCard.getValueInt());
                }
                dataOut.writeInt(getCurrentPlayer());
                dataOut.flush();

                while(true) { //TODO send players number of cards of their opponents
                            //TODO send player new stockpile
                    if(playerID == 1) {
                        String readText = dataIn.readUTF();
                        System.out.println("Receive text from " + playerID + ": " + readText);
                        performOperation(readText, playerID);

                    } else if(playerID == 2) {
                        String readText = dataIn.readUTF();
                        System.out.println("Receive text from " + playerID + ": " + readText);
                        performOperation(readText, playerID);
                    }
                    if(numConPlayers == 2) {
                        saveToJson();
                        continue;
                    }
                    if(playerID == 3) {
                        String readText = dataIn.readUTF();
                        System.out.println("Receive text from " + playerID + ": " + readText);
                        performOperation(readText, playerID);
                    } else if(playerID == 4) {
                        String readText = dataIn.readUTF();
                        System.out.println("Receive text from " + playerID + ": " + readText);
                        performOperation(readText, playerID);
                    }
                    saveToJson();
                }
                //TODO send info to other player about number of cards in hand

            } catch (IOException ex) {
                System.out.println("IOException from run() SSC");
            }
        }

    }

    public static void main(String[] args) throws InterruptedException {
        GameServer gs = new GameServer();
        gs.waitForHost();
        gs.acceptConnections();
        //gs.closeConnection();
    }
}
