package ui.view;

import com.company.PanCard;
import com.company.Player;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

public class GameView extends JFrame {
    private JPanel mainGamePanel;
    private JPanel playerHand;
    private JPanel opponentHand2;
    private JPanel opponentHand1;
    private JPanel opponentHand3;
    private JPanel stockpile;

    public GameView() {
        setSize(1260, 720);
        setContentPane(mainGamePanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void showGameWindow() {
        pack();
        setVisible(true);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        playerHand = new PlayerHand().getPlayerHand();
        opponentHand1 = new OpponentHandVertical().getPanel();
        opponentHand2 = new OpponentHandHorizontal().getPanel();
        opponentHand3 = new OpponentHandVertical().getPanel();
        stockpile = new Stockpile().getPanel();
    }
}
