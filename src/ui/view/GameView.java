package ui.view;

import javax.swing.*;
import java.util.ArrayList;

public class GameView extends JFrame {
    private JPanel mainGamePanel;
    private JButton playerCard0;
    private JButton opponentCard0;

    private ArrayList<JButton> playerHand;
    private ArrayList<JButton> opponentHand;

    public GameView() {
        playerHand = new ArrayList<JButton>();
        opponentHand = new ArrayList<JButton>();
        setSize(1260, 720);
        setContentPane(mainGamePanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public ArrayList<JButton> getPlayerHand() {
        playerHand.add(playerCard0);
        return playerHand;
    }

    public ArrayList<JButton> getOpponentHand() {
        opponentHand.add(opponentCard0);
        return opponentHand;
    }

    public void showGameWindow() {
        this.setVisible(true);
    }
}
