package ui.controller;

import ui.view.GameFrame;
import ui.view.MenuFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class FrameController {
    private MenuFrame menuFrame;
    private JButton playButton;
    private JButton exitButton;
    //
    private GameFrame gameFrame;
    private ArrayList<JButton> playerHand;




    public FrameController() {
        initMenuComponents();
        showMenuFrameWindow();
        initMenuListeners();
    }

    private void initMenuComponents() {
        menuFrame = new MenuFrame();
        playButton = menuFrame.getPlayButton();
        exitButton = menuFrame.getExitButton();
    }

    private void initMenuListeners() {
        playButton.addActionListener(new FrameController.PlayButtonListener());
        exitButton.addActionListener(new FrameController.ExitButtonListener());
    }

    private class PlayButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            closeMenuFrame();
            // TODO if(1 == playerID) -> GameSetupFrame; else -> GameFrame
            initGameFrameComponents();
            showGameFrameWindow();
        }
    }

    private class ExitButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            System.exit(0);
        }
    }

    public void showMenuFrameWindow() {
        menuFrame.setVisible(true);
    }

    private void closeMenuFrame() {
        menuFrame.dispose();
    }

    private void initGameFrameComponents() {
        gameFrame = new GameFrame();
        playerHand = gameFrame.getPlayerHand();
    }

    public void showGameFrameWindow() {
        gameFrame.setVisible(true);
    }
}