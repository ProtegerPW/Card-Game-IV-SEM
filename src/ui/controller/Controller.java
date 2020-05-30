package ui.controller;

import com.company.Player;
import ui.view.GameFrame;
import ui.view.MenuFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Controller {
    private Player player;
    //
    private MenuFrame menuFrame;
    //
    private GameFrame gameFrame;

    public Controller(Player p, MenuFrame menu) {
        player = p;
        menuFrame = menu;
    }

    public void initController() {
        menuFrame.showMenuFrameWindow();
        initMenuListeners();
    }

    private void initMenuListeners() {
        menuFrame.getPlayButton().addActionListener(new Controller.PlayButtonListener());
        menuFrame.getExitButton().addActionListener(new Controller.ExitButtonListener());
    }

    private class PlayButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            menuFrame.closeMenuFrame();
            // TODO if(1 == playerID) -> GameSetupFrame
            initGameFrameComponents();
            showGameFrameWindow();
        }
    }

    private class ExitButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            System.exit(0);
        }
    }

    // // // // // // // // // // // // // // // // // // // // // // // // // // // // //

    private void initGameFrameComponents() {
        gameFrame = new GameFrame();
    }

    public void showGameFrameWindow() {
        gameFrame.setVisible(true);
    }
}