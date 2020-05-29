package ui.controller;

import ui.view.TwoPlayerGameFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class TwoPlayerGameFrameController {
    private TwoPlayerGameFrame twoPlayerGameFrame;
    private ArrayList<JButton> playerHand;
    private ArrayList<JButton> opponentHand;

    public TwoPlayerGameFrameController() {
        initGameFrameComponents();
        initListener();
    }

    private void initListener() {
        PlayCardListener playCardListener = new PlayCardListener();
        playerHand.get(0).addActionListener(playCardListener);
        opponentHand.get(0).addActionListener(playCardListener);
    }

    private void initGameFrameComponents() {
        twoPlayerGameFrame = new TwoPlayerGameFrame();
        playerHand = twoPlayerGameFrame.getPlayerHand();
        opponentHand = twoPlayerGameFrame.getOpponentHand();
    }

    public void showGameFrameWindow() {
        twoPlayerGameFrame.setVisible(true);
    }

    private class PlayCardListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.out.println(e.getActionCommand());
        }
    }
}