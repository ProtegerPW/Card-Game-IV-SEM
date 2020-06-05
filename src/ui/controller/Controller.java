package ui.controller;

import com.company.PanCard;
import com.company.Player;
import ui.view.GameSetup;
import ui.view.GameView;
import ui.view.Menu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Controller {
    private Player player;
    //
    private Menu menu;
    private GameSetup gameSetup;
    private GameView gameView;

    public Controller(Player p, Menu menu) {
        player = p;
        this.menu = menu;
    }

    public void initController() {
        menu.showMenuWindow();
        initMenuListeners();
    }

    private void initMenuListeners() {
        menu.getPlayButton().addActionListener(new Controller.PlayButtonListener());
        menu.getExitButton().addActionListener(new Controller.ExitButtonListener());
    }

    private class PlayButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            player.connectToServer();
            if(player.getPlayerID() == -1)
                return;
            initGameView();
            if(player.getPlayerID() == 1) {
                initGameSetup();
                initGameSetupListeners();
            }
            else {
                gameView.showGameWindow();
                menu.closeMenu();
            }
        }
    }

    private class ExitButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            System.exit(0);
        }
    }

    // // // // // // // // // // // // // // // // // // // // // // // // // // // // //

    private void initGameSetup() {
        gameSetup = new GameSetup();
        gameSetup.showGameSetupWindow();
    }

    private void initGameSetupListeners() {
        gameSetup.getButton2().addActionListener(new Controller.NumPlayerListener());
        gameSetup.getButton4().addActionListener(new Controller.NumPlayerListener());
    }

    private class NumPlayerListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            JButton b = (JButton) actionEvent.getSource();
            int numPlayers = Integer.parseInt(b.getText());
            player.setPlayersNumber(numPlayers);
            gameView.showGameWindow();
            gameSetup.dispose();
            menu.dispose();
        }
    }

    // // // // // // // // // // // // // // // // // // // // // // // // // // // // //

    private void initGameView() {
        gameView = new GameView();
        gameView.setHand(player.getHandOfCards());
        gameView.playerHand.addMouseListener(new PlayerHandListener());
    }

    public class PlayerHandListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            System.out.println( "Clicked" );
            PanCard selected = player.getSelectedCard();
            if (selected != null) {
                Rectangle bounds = gameView.getMapCards().get(selected);
                bounds.y += 20;
                gameView.playerHand.repaint();
            }
            for(PanCard card: player.getHandOfCards()) {
                Rectangle bounds = gameView.getMapCards().get(card);
                if(bounds.contains(e.getPoint())) {
                    player.setSelectedCard(card);
                    bounds.y -= 20;
                    gameView.playerHand.repaint();
                    break;
                }
            }
        }
    }
}