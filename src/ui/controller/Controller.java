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
                if (player.getCurrentPlayer() != player.getPlayerID()) {
                    Thread t = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            clientSideConnection.receiveUpdate();
                        }
                    });
                    t.start();
                }
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
        gameView = new GameView(player.getPlayerID(), player.getHandOfCards(), player.getCardCount());
        gameView.showGameWindow();
        gameView.playerHand.addMouseListener(new PlayerHandListener());
        gameView.getDrawCardsButton().addActionListener(new DrawCardsListener());
        gameView.getPlaySelectedButton().addActionListener(new PlaySelectedListener());
    }

    boolean mouseEnable = false;

    public void mouseEnable() {
        mouseEnable = true;
    }

    public void mouseDisable() {
        mouseEnable = false;
    }

    public class PlayerHandListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if(!mouseEnable) {           // activates only on player's turn
                return;
            }
            System.out.println( "Clicked" );    // --delete later
            PanCard clicked = getClickedCard(e);
            if(clicked == null) {        // if player clicked no card -> return (do nothing)
                System.out.println("No cards selected");    // --delete later
                resetSelectedCards();
                return;
            }
//            if(!player.checkCardIsValid(clicked)) {       // check if clicked card can be put onto the stockpile
//                System.out.println("Illegal move");         // --delete later
//                deselectCard();
//                return;
//            }
            if(!player.checkMultiCard(clicked)) {        // check if player has all cards of clicked card value
                System.out.println("Play clicked card");    // if no -> play clicked card   // --delete later
                resetSelectedCards();
                // TODO send played card to server
                // TODO remove card from hand
                // TODO update stockpile, update player hand (Konrad)
//                player.deleteCardFromHand(clicked);
//                gameView.setHand(player.getHandOfCards());
//                gameView.playerHand.invalidate();
//                gameView.playerHand.repaint();   // update playerHand view
            }
            else {                // player can play multiple cards
                System.out.println( "Multiple cards option" );  // --delete later
                ArrayList<PanCard> selectedCards = player.getSelectedCards();
                if(selectedCards == null) {             // if there is no card already selected
                    pullCardUp(clicked);                    // display selection on player's hand
                    player.pushCardToSelected(clicked);     // mark card as selected
                    gameView.enablePlaySelectedButton();
                    return;
                }
                for(PanCard card: selectedCards) {
                    if (card == clicked) {              // if player clicks a card already selected
                        pullCardDown(clicked);              // deselect card
                        player.removeCardFromSelected(clicked);
                        return;
                    }
                }
                // player clicked another card of the same value -> pull it up
                gameView.disablePlaySelectedButton();
                pullCardUp(clicked);                    // display selection on player's hand
                player.pushCardToSelected(clicked);     // mark card as selected
                // TODO if all cards selected? continue: return;
                // TODO send cards to server
                // TODO remove all played cards from hand
                // TODO update stockpile, update player hand (Konrad)

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
            // TODO draw cards
            // TODO update player hand, stockpile (Konrad)
            // TODO next player turn
            // TODO disable buttons
        }
    }

    private class PlaySelectedListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            // TODO play selected card
            // TODO update player hand, stockpile (Konrad)
            // TODO next player turn
            // TODO disable buttons
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