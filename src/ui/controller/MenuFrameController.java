package ui.controller;

import ui.view.MenuFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuFrameController {
    private MenuFrame menuFrame;
    private JButton playButton;
    private JButton exitButton;
    private JLabel gameTitle;

    private TwoPlayerGameFrameController twoPlayerGameFrameController;

    public MenuFrameController() {
        initMenuComponents();
        initListener();
    }

    private void initMenuComponents() {
        menuFrame = new MenuFrame();
        playButton = menuFrame.getPlayButton();
        exitButton = menuFrame.getExitButton();
        gameTitle = menuFrame.getGameTitle();
    }

    private void initListener() {
        playButton.addActionListener(new PlayButtonListener());
        exitButton.addActionListener(new ExitButtonListener());

    }

    private class PlayButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            twoPlayerGameFrameController = new TwoPlayerGameFrameController();
            twoPlayerGameFrameController.showGameFrameWindow();
            menuFrame.dispose();
        }
    }

    public void showMenuFrameWindow() {
        menuFrame.setVisible(true);
    }

    private class ExitButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent actionEvent) {
            System.exit(0);
        }
    }
}