package converters;

import com.company.GameServer;
import com.company.PanCard;

import java.util.ArrayList;

public class ServerConverter {

    private int numOfPlayers;
    private int currentPlayer;
    private ArrayList<PanCard> stockpile;
    private ArrayList<ArrayList<PanCard>> playerHand;


    public ServerConverter(GameServer gameSever) {
        numOfPlayers = gameSever.getNumOfPlayers();
        currentPlayer = gameSever.getCurrentPlayer();
        stockpile = gameSever.getStockpile();
        playerHand = gameSever.getPlayerHand();
    }
}
