package ui.controller;

import com.company.ClientSideConnection;
import com.company.PanCard;
import com.company.Player;
import ui.view.GameSetup;
import ui.view.GameView;
import ui.view.Menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Controller {
    private final Player player;
    private final ClientSideConnection clientSideConnection;

    private final Menu menu;
    private GameSetup gameSetup;
    private GameView gameView;

    boolean mouseStatus = false;

    final int PULL_CARD_DELTA_Y = 20;

    public Controller(Player player, ClientSideConnection clientSideConnection, Menu menu) {
        this.player = player;
        this.clientSideConnection = clientSideConnection;
        this.menu = menu;
    }

        // display menu frame, create listeners for menu
    public void displayMenu() {
        menu.showMenuWindow();
        menu.getPlayButton().addActionListener(new Controller.PlayButtonListener());
        menu.getExitButton().addActionListener(new ExitButtonListener());
    }

        // if playerID == 1 -> open game setup, otherwise -> open main game frame
    private class PlayButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            clientSideConnection.connectToServer();
            if(player.getPlayerID() == -1)      // connection unsuccessful
                return;
            if(player.getPlayerID() == 1) {     // playerID == 1
                initGameSetup();
            }
            else {
                clientSideConnection.getPlayerInitialHand();
                initGameView();
                menu.closeMenu();               // close menu frame
            }
        }
    }

        // if player chooses exit -> close game
    private static class ExitButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            System.exit(0);
        }
    }

    // // // // // // // // // // // // // // // // // // // // // // // // // // // // //

        // display game setup frame -> first player chooses number of players
    private void initGameSetup() {
        gameSetup = new GameSetup();
        gameSetup.showGameSetupWindow();
        gameSetup.getButton2().addActionListener(new Controller.NumPlayerListener());
        gameSetup.getButton4().addActionListener(new Controller.NumPlayerListener());
    }

        // listener for 2 options in game setup frame - select 2 or 4 players
    private class NumPlayerListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            JButton b = (JButton) actionEvent.getSource();
            int numPlayers = Integer.parseInt(b.getText());
            clientSideConnection.setPlayersNumber(numPlayers);  // send selected player number to server
            clientSideConnection.getPlayerInitialHand();
            System.out.println("Number of players: " + numPlayers);     // --log
            initGameView();                                     // display game frame
            gameSetup.dispose();                                //
            menu.dispose();                                     // close other windows
        }
    }

    // // // // // // // // // // // // // // // // // // // // // // // // // // // // //

        // display game frame, create listener for player hand
    private void initGameView() {
        gameView = new GameView(player.getPlayerID(), player.getCurrentPlayer(), player.getHandOfCards(), player.getCardCount());
        gameView.showGameWindow();
        gameView.playerHand.addMouseListener(new PlayerHandListener());
        gameView.getDrawCardsButton().addActionListener(new DrawCardsListener());
        gameView.getPlaySelectedButton().addActionListener(new PlaySelectedListener());
        updateGameState();
    }

        // fires when player hits "draw cards" button
    private class DrawCardsListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            clientSideConnection.sendCommunicate("drawCards", null);
            updateStockpile();
            updatePlayerHand();
            updateGameState();
        }
    }

        // fires when player hits "play selected card" button
    private class PlaySelectedListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            clientSideConnection.sendCommunicate("addCards", player.getSelectedCards());
            resetSelectedCards();
            updateStockpile();
            updatePlayerHand();
            updateGameState();
        }
    }

        // main listener: checks where a click was made and, if player clicks a card,
        // changes gameView state according to implemented decision trees
    public class PlayerHandListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if(!mouseStatus) {           // activates only on player's turn
                return;
            }
            System.out.println( "Clicked" );                    // --log
            PanCard clicked = getClickedCard(e);
            if(clicked == null) {                          // if player clicked no card -> return (do nothing)
                System.out.println("No cards selected");        // --log
                return;
            }
            if (!player.checkCardIsValid(clicked)) {       // check if the move is valid
                System.out.println("Illegal move");             // --log
                return;
            }
            if(player.getSelectedCards().size() == 0) {
                if(!player.checkMultiCard(clicked)) {        // check if player has all cards of clicked card value
                    System.out.println("Play clicked card");       // if false -> play clicked card  --log
                    clientSideConnection.sendCommunicate("addCards", new ArrayList<PanCard>() {{ add(clicked); }});
                    updateStockpile();
                    updatePlayerHand();
                    updateGameState();
                }
                else {
                    pullCardUp(clicked);                    // display selection on player's hand
                    player.pushCardToSelected(clicked);     // mark card as selected
                    updateButtons();
                }
            }
            else {      // player can play multiple cards
                System.out.println("Multiple cards option");    // --log
                if(player.getSelectedCards().get(0).getValue() != clicked.getValue()) {     // there are cards selected, but player clicked card of another value
                    return;                                                                     // do nothing
                }
                for (PanCard card : player.getSelectedCards()) {
                    if (card == clicked ) {         // if player clicks a card already selected
                        if(player.checkCardIsHeartNine(clicked))
                            return;
                        pullCardDown(clicked);                      // deselect card
                        player.removeCardFromSelected(clicked);     // mark card as deselected
                        updateButtons();
                        return;
                    }
                }
                    // player clicked another card of the same value -> pull it up
                pullCardUp(clicked);                    // display selection on player's hand
                player.pushCardToSelected(clicked);     // mark card as selected
                updateButtons();
                    // if player selected 4 cards of any value OR selected 3 9s and none of them is 9 of hearts -> play all selected cards
                if (player.getSelectedCards().size() == 4
                    || (player.getSelectedCards().size() == 3 && player.getSelectedCards().get(0).getValueInt() == 0
                        && player.getSelectedCards().get(0).getColorInt() != 0)) {
                    clientSideConnection.sendCommunicate("addCards", player.getSelectedCards());
                    resetSelectedCards();
                    updateStockpile();
                    updatePlayerHand();
                    updateGameState();
                }
            }
        }

            // obtain information about card clicked by player, if player clicks outside of card area -> return null
        public PanCard getClickedCard(MouseEvent e) {
            for(PanCard card: player.getReversedHandOfCards()) {
                Rectangle bounds = gameView.getMapPlayerHand().get(card);
                if(bounds.contains(e.getPoint())) {
                    return card;
                }
            }
            return null;
        }
    }

        // obtain changes about game after every move, if game ends -> begin new game sequence
    public void updateGameState() {
        if(!player.checkIsEnd()) {  // if the game is on
            if (player.getCurrentPlayer() != player.getPlayerID()) {    // if it's another player's turn
                mouseDisable();                             // disable listeners
                gameView.disableDrawCardsButton();
                gameView.disablePlaySelectedButton();
                Thread t = new Thread(new Runnable() {      // wait for another turn
                    @Override
                    public void run() {
                        updateStatus();
                    }
                });
                t.start();
                System.out.println("Mouse disable");            // --log
            } else {    // it's player's turn
                mouseEnable();                              // enable listeners if an action can be made
                if (player.getStockpileSize() >= 2)
                    gameView.enableDrawCardsButton();
                System.out.println("Mouse enable");             // --log
            }
        } else {        // game ended
            gameView.disablePlaySelectedButton();           // disable listeners
            gameView.disableDrawCardsButton();
            mouseDisable();
            System.out.println("The game session has ended");   // --log
            boolean newGame = gameView.endGameWindow();     // display new game option panel, wait for player response
            if(newGame) {
                clientSideConnection.sendCommunicate("Yes",null);
                if("No".equals(clientSideConnection.getdataInUTF())) {
                    gameView.dispose();
                } else {
                    clientSideConnection.getPlayerInitialHand();
                    resetGameView();
                }
            }
            else {
                clientSideConnection.sendCommunicate("No",null);
                gameView.dispose();
            }
        }
    }

           // update game view after any opponent's turn
    public void updateStatus() {
        clientSideConnection.receiveUpdate();
        updateOpponentHands();
        player.setNextPlayer();
        updateStockpile();
        updateGameState();
    }

        // disable/enable buttons depending on amount of cards selected
    public void updateButtons() {
        if(player.getSelectedCards().size() == 0) {
            gameView.disablePlaySelectedButton();
            gameView.enableDrawCardsButton();
        } else if(player.getSelectedCards().size() == 1) {
            gameView.enablePlaySelectedButton();
            gameView.disableDrawCardsButton();
        } else {
            gameView.disablePlaySelectedButton();
            gameView.disableDrawCardsButton();
        }
    }

        // update stockpile view after any change
    public void updateStockpile() {
        gameView.setStockpile(player.getStockpile());
        gameView.setCurrentPlayer(player.getCurrentPlayer());
        gameView.stockpilePanel.invalidate();
        gameView.stockpilePanel.repaint();
    }

        // update player hand view after any change
    public void updatePlayerHand() {
        gameView.setHand(player.getHandOfCards());
        gameView.playerHand.invalidate();
        gameView.playerHand.repaint();
    }

        // update opponent hands views after any change
    public void updateOpponentHands() {
        int[] tmp = player.getCardCount();
        gameView.setCardCount(tmp);
        gameView.opponentHand2.invalidate();
        gameView.opponentHand2.repaint();
        if(tmp.length == 4) {
            gameView.opponentHand1.invalidate();
            gameView.opponentHand1.repaint();
            gameView.opponentHand3.invalidate();
            gameView.opponentHand3.repaint();
        }
    }

        // reset view after a new game is launched
    public void resetGameView() {
        updatePlayerHand();
        updateStockpile();
        updateOpponentHands();
        updateGameState();
    }

    public void mouseEnable() {
        mouseStatus = true;
    }

    public void mouseDisable() {
        mouseStatus = false;
    }

        // pull down and mark as deselected any selected cards in player's hand
    public void resetSelectedCards() {
        for(PanCard card: player.getSelectedCards()) {
            pullCardDown(card);
        }
        player.resetSelectedCards();
    }

        // move card up in playerHand view -> select clicked card
    public void pullCardUp(PanCard card) {
        gameView.getMapPlayerHand().get(card).y -= PULL_CARD_DELTA_Y;
        gameView.playerHand.repaint();
    }

        // move card down in playerHand view -> deselect highlighted card
    public void pullCardDown(PanCard card) {
        gameView.getMapPlayerHand().get(card).y += PULL_CARD_DELTA_Y;
        gameView.playerHand.repaint();
    }
}