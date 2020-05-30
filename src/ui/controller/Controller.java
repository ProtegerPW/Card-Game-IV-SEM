package ui.controller;

import com.company.Player;
import ui.view.GameSetup;
import ui.view.GameView;
import ui.view.Menu;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
            if(1 == player.getPlayerID()) {
                gameSetup = new GameSetup();
                gameSetup.showGameSetupWindow();
                initGameSetupListeners();
            }
            else {
                gameView = new GameView();
                gameView.showGameFrameWindow();
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

    private void initGameSetupListeners() {
        gameSetup.getButton2().addActionListener(new Controller.NumPlayerListener());
        gameSetup.getButton4().addActionListener(new Controller.NumPlayerListener());
    }

    private class NumPlayerListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            JButton b = (JButton) actionEvent.getSource();
            int numPlayers = Integer.parseInt(b.getText());
            player.getCsc().setPlayersNumber(numPlayers);
        }
    }
}