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
    private Player player;
    private ClientSideConnection clientSideConnection;
    //
    private Menu menu;
    private GameSetup gameSetup;
    private GameView gameView;

    public Controller(Player player, ClientSideConnection clientSideConnection, Menu menu) {
        this.player = player;
        this.clientSideConnection = clientSideConnection;
        this.menu = menu;
    }

        // display menu frame, create listeners for menu
    public void displayMenu() {
        menu.showMenuWindow();
        menu.getPlayButton().addActionListener(new Controller.PlayButtonListener());
        menu.getExitButton().addActionListener(new Controller.ExitButtonListener());
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
    private class ExitButtonListener implements ActionListener {
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
            System.out.println("Number of players: " + numPlayers);
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
        configButtons();
    }

    boolean mouseStatus = false;

    public void mouseEnable() {
        mouseStatus = true;
    }

    public void mouseDisable() {
        mouseStatus = false;
    }

    public class PlayerHandListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if(!mouseStatus) {           // activates only on player's turn
                return;
            }
            System.out.println( "Clicked" );    // --delete later
            PanCard clicked = getClickedCard(e);
            if(clicked == null) {                          // if player clicked no card -> return (do nothing)
                System.out.println("No cards selected");        // --delete later
                return;
            }
            if (!player.checkCardIsValid(clicked)) {       // check if the move is valid
                System.out.println("Illegal move");             // --delete later
                return;
            }
            if(player.getSelectedCards().size() == 0) {
                if(!player.checkMultiCard(clicked)) {        // check if player has all cards of clicked card value
                    System.out.println("Play clicked card");    // if no -> play clicked card   // --delete later
                    clientSideConnection.sendCommunicate("addCards", new ArrayList<PanCard>() {{ add(clicked); }});
                    configButtons();
                    updateStockpile();
                    updatePlayerHand();     // update playerHand view
                }
                else {
                    pullCardUp(clicked);                    // display selection on player's hand
                    player.pushCardToSelected(clicked);     // mark card as selected
                    updateMoveOptions();
                }
            }
            else {                                          // player can play multiple cards
                System.out.println("Multiple cards option");    // --delete later
                if(player.getSelectedCards().get(0).getValue() != clicked.getValue()) {     // there are cards selected, but player clicked card of another value
                    return;                                                                    // do nothing
                }
                for (PanCard card : player.getSelectedCards()) {
                    if (card == clicked ) {         // if player clicks a card already selected
                        if(player.checkCardIsHeartNine(clicked))
                            return;
                        pullCardDown(clicked);                  // deselect card
                        player.removeCardFromSelected(clicked);
                        updateMoveOptions();
                        return;
                    }
                }
                    // player clicked another card of the same value -> pull it up
                pullCardUp(clicked);                    // display selection on player's hand
                player.pushCardToSelected(clicked);     // mark card as selected
                updateMoveOptions();
                    // check if all selected cards can be played
                if (player.getSelectedCards().size() == 4
                    || (player.getSelectedCards().size() == 3 && player.getSelectedCards().get(0).getValueInt() == 0 && player.getSelectedCards().get(0).getColorInt() != 0)) {
                    clientSideConnection.sendCommunicate("addCards", player.getSelectedCards());
                    resetSelectedCards();
                    configButtons();
                    updateStockpile();
                    updatePlayerHand();
                }
            }
        }

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

    private class DrawCardsListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            clientSideConnection.sendCommunicate("drawCards", null);
            configButtons();
            updateStockpile();
            updatePlayerHand();
        }
    }

    private class PlaySelectedListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            clientSideConnection.sendCommunicate("addCards", player.getSelectedCards());
            resetSelectedCards();
            configButtons();
            updateStockpile();
            updatePlayerHand();
        }
    }

    public void updateStatus() {
        clientSideConnection.receiveUpdate();
        updateOpponentHand();
        player.setNextPlayer();
        updateStockpile();
        configButtons();
    }

    public void updateMoveOptions() {
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

    public void updateStockpile() {
        gameView.setStockpile(player.getStockpile());
        gameView.setCurrentPlayer(player.getCurrentPlayer());
        gameView.stockpilePanel.invalidate();
        gameView.stockpilePanel.repaint();
    }

    public void updatePlayerHand() {
        gameView.setHand(player.getHandOfCards());
        gameView.playerHand.invalidate();
        gameView.playerHand.repaint();
    }

    public void updateOpponentHand() {
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

    public void configButtons() {
        if(!player.checkIsEnd()) {
            if (player.getCurrentPlayer() != player.getPlayerID()) {
                mouseDisable();
                gameView.disableDrawCardsButton();
                gameView.disablePlaySelectedButton();
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        updateStatus();
                    }
                });
                t.start();
                System.out.println("Mouse disable");
            } else {
                mouseEnable();
                if (player.getStockpileSize() >= 2)
                    gameView.enableDrawCardsButton();
                System.out.println("Mouse enable");
            }
        } else {
            gameView.disablePlaySelectedButton();
            gameView.disableDrawCardsButton();
            mouseDisable();
            System.out.println("The game session has ended");
            gameView.showEndGameWindow();
            //TODO if() send Yes or No based on clicked Button
        }
    }

    public void resetSelectedCards() {
        for(PanCard card: player.getSelectedCards()) {
            pullCardDown(card);
        }
        player.resetSelectedCards();
    }

    final int PULL_CARD_DELTA_Y = 20;

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