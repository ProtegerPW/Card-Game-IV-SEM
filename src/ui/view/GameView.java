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
    private JPanel opponentHand1;
    private JPanel opponentHand2;
    private JPanel opponentHand3;
    private JPanel stockpile;

    public GameView() {
        setSize(1280, 720);
        setContentPane(mainGamePanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public JPanel getPlayerHand() {
        return playerHand;
    }

    public void setPlayerHand(JPanel playerHand) {
        this.playerHand = playerHand;
    }

    public JPanel getOpponentHand1() {
        return opponentHand1;
    }

    public void setOpponentHand1(JPanel opponentHand1) {
        this.opponentHand1 = opponentHand1;
    }

    public JPanel getOpponentHand2() {
        return opponentHand2;
    }

    public void setOpponentHand2(JPanel opponentHand2) {
        this.opponentHand2 = opponentHand2;
    }

    public JPanel getOpponentHand3() {
        return opponentHand3;
    }

    public void setOpponentHand3(JPanel opponentHand3) {
        this.opponentHand3 = opponentHand3;
    }

    public JPanel getStockpile() {
        return stockpile;
    }

    public void setStockpile(JPanel stockpile) {
        this.stockpile = stockpile;
    }

    public void showGameWindow() {
        setVisible(true);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        playerHand = new PlayerHand();
        opponentHand1 = new OpponentHandVertical();
        opponentHand2 = new OpponentHandHorizontal();
        opponentHand3 = new OpponentHandVertical();
        stockpile = new Stockpile().getPanel();
    }
}
