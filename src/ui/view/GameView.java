package ui.view;

import com.company.PanCard;
import com.company.PanDeck;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameView extends JFrame {
    private JPanel mainGamePanel;
    public JPanel playerHand;
    public JPanel opponentHand1;
    public JPanel opponentHand2;
    public JPanel opponentHand3;
    public JPanel stockpile;

    private ArrayList<PanCard> hand;
    private Map<PanCard, Rectangle> mapCards;
    private int cardCount[];

    public GameView() {
        setSize(1280, 720);
        setContentPane(mainGamePanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void createUIComponents() {
        playerHand = new PlayerHand();
        opponentHand1 = new OpponentHandVertical();
        opponentHand2 = new OpponentHandHorizontal();
        opponentHand3 = new OpponentHandVertical();
        stockpile = new Stockpile().getPanel();
    }

    public void showGameWindow() {
        setVisible(true);
    }

    public ArrayList<PanCard> getHand() {
        return hand;
    }

    public void setHand(ArrayList<PanCard> hand) {
        this.hand = hand;
    }

    public Map<PanCard, Rectangle> getMapCards() {
        return mapCards;
    }

    public void setMapCards(Map<PanCard, Rectangle> mapCards) {
        this.mapCards = mapCards;
    }

    public int[] getCardCount() {
        return cardCount;
    }

    public void setCardCount(int[] cardCount) {
        this.cardCount = cardCount;
    }

    public class PlayerHand extends JPanel {
        public PlayerHand() {
            mapCards = new HashMap<>(5);
            setSize(-1, 150);
            setPreferredSize(new Dimension(-1,150));
            setMaximumSize(new Dimension(-1,200));
            setBackground(Color.CYAN);
        }
//
//        @Override
//        public void invalidate() {
//            super.invalidate();
//            mapCards.clear();
//            int cardHeight = (getHeight() - 20) / 3;
//            int cardWidth = (int) (cardHeight * 0.6);
//            int xDelta = cardWidth / 2;
//            int xPos = (int) ((getWidth() / 2) - (cardWidth * (hand.size() / 4.0)));
//            int yPos = (getHeight() - 20) - cardHeight;
//            for (PanCard card: hand) {
//                Rectangle bounds = new Rectangle(xPos, yPos, cardWidth, cardHeight);
//                mapCards.put(card, bounds);
//                xPos += xDelta;
//            }
//        }
//
//        protected void paintComponent(Graphics g) {
//            super.paintComponent(g);
//            Graphics2D g2d = (Graphics2D) g.create();
//            for(PanCard card: hand) {
//                Rectangle bounds = mapCards.get(card);
//                System.out.println(bounds);
//                if (bounds != null) {
//                    g2d.setColor(Color.WHITE);
//                    g2d.fill(bounds);
//                    g2d.setColor(Color.BLACK);
//                    g2d.draw(bounds);
//                    Graphics2D copy = (Graphics2D) g2d.create();
//                    paintCard(copy, card, bounds);
//                    copy.dispose();
//                }
//            }
//            g2d.dispose();
//        }
//
//        protected void paintCard(Graphics2D g2d, PanCard card, Rectangle bounds) {
//            g2d.translate(bounds.x + 5, bounds.y + 5);
//            g2d.setClip(0, 0, bounds.width - 5, bounds.height - 5);
//        }
    }

    public class OpponentHandHorizontal extends JPanel {
        public OpponentHandHorizontal() {
            setSize(-1, 150);
            setPreferredSize(new Dimension(-1,150));
            setMaximumSize(new Dimension(-1,200));
            setBackground(Color.MAGENTA);
        }
    }

    public class OpponentHandVertical extends JPanel {
        public OpponentHandVertical() {
            setSize(150, -1);
            setPreferredSize(new Dimension(150,-1));
            setMaximumSize(new Dimension(200,-1));
            setBackground(Color.ORANGE);
        }
    }
}
