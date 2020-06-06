package ui.view;

import com.company.PanCard;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
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

    private int numOfPlayers;
    private int playerID;
    private int cardCount[];
    private ArrayList<PanCard> hand;
    private Map<PanCard, Rectangle> mapPlayerHand;

    public GameView(int playerID, ArrayList<PanCard> hand, int cardCount[]) {
        this.playerID = playerID;
        this.hand = hand;
        this.cardCount = cardCount;
        numOfPlayers = this.cardCount.length;
        setSize(1280, 760);
        setContentPane(mainGamePanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void createUIComponents() {
        playerHand = new PlayerHand();
        opponentHand2 = new OpponentHandHorizontal();
        stockpile = new Stockpile().getPanel();
        if (numOfPlayers == 4) {
            opponentHand1 = new OpponentHandVertical("left");
            opponentHand3 = new OpponentHandVertical("right");
        }
        else {
            opponentHand1 = new JPanel();
            opponentHand3 = new JPanel();
        }
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

    public Map<PanCard, Rectangle> getMapPlayerHand() {
        return mapPlayerHand;
    }

    public void setMapPlayerHand(Map<PanCard, Rectangle> mapPlayerHand) {
        this.mapPlayerHand = mapPlayerHand;
    }

    public int[] getCardCount() {
        return cardCount;
    }

    public void setCardCount(int[] cardCount) {
        this.cardCount = cardCount;
    }

    // // // // // // // // // // // // // // // // // // // // // // // // // // // // // //

    public class PlayerHand extends JPanel {
        public PlayerHand() {
            mapPlayerHand = new HashMap<>(1);
            setBackground(Color.CYAN);
        }

        @Override
        public void invalidate() {
            super.invalidate();
            mapPlayerHand.clear();
            int cardHeight = 180;
            int cardWidth = 120;
            int xDelta = 40;
            int xPos = ((getWidth() - cardWidth - (hand.size() - 1)*xDelta)/2);
            int yPos = getHeight() - 20 - cardHeight;
            for (PanCard card: hand) {
                Rectangle bounds = new Rectangle(xPos, yPos, cardWidth, cardHeight);
                mapPlayerHand.put(card, bounds);
                xPos += xDelta;
            }
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            String path;
            for(PanCard card: hand) {
                Rectangle bounds = mapPlayerHand.get(card);
                if (bounds != null) {
                    try {
                        path = "../img/" + card.toString() + ".png";
                        BufferedImage cardImage;
                        cardImage = ImageIO.read(getClass().getResource(path));
                        g2d.drawImage(cardImage, bounds.x, bounds.y, bounds.width, bounds.height, null);
                        Graphics2D copy = (Graphics2D) g2d.create();
                        g2d.setColor(Color.BLACK);
                        g2d.draw(bounds);
                        paintCard(copy, bounds);
                        copy.dispose();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            g2d.dispose();
        }

        protected void paintCard(Graphics2D g2d, Rectangle bounds) {
            g2d.translate(bounds.x + 5, bounds.y + 5);
            g2d.setClip(0, 0, bounds.width - 5, bounds.height - 5);
        }
    }

    // // // // // // // // // // // // // // // // // // // // // // // // // // // // // //

    public class OpponentHandHorizontal extends JPanel {
        private ArrayList<Rectangle> mapCards;

        public OpponentHandHorizontal() {
            mapCards = new ArrayList<>();
            setBackground(Color.MAGENTA);
        }

        @Override
        public void invalidate() {
            super.invalidate();
            mapCards.clear();
            int cardHeight = 120;
            int cardWidth = 80;
            int xDelta = 27;
            int xPos = ((getWidth() - cardWidth - (hand.size() - 1)*xDelta)/2);
            int yPos = getHeight() - 10 - cardHeight;
            int opponentNumberOfCards = cardCount[(playerID + 1)%numOfPlayers];
            for(int i = 0; i < opponentNumberOfCards; ++i ) {
                Rectangle bounds = new Rectangle(xPos, yPos, cardWidth, cardHeight);
                mapCards.add(bounds);
                xPos += xDelta;
            }
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            BufferedImage cardImage = null;
            String path = "../img/background.png";
            try {
                cardImage = ImageIO.read(getClass().getResource(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
            for(Rectangle bounds: mapCards ) {
                if (bounds != null) {
                    g2d.drawImage(cardImage, bounds.x, bounds.y, bounds.width, bounds.height, null);
                    Graphics2D copy = (Graphics2D) g2d.create();
                    g2d.setColor(Color.BLACK);
                    g2d.draw(bounds);
                    paintCard(copy, bounds);
                    copy.dispose();
                }
            }
            g2d.dispose();
        }

        protected void paintCard(Graphics2D g2d, Rectangle bounds) {
            g2d.translate(bounds.x + 5, bounds.y + 5);
            g2d.setClip(0, 0, bounds.width - 5, bounds.height - 5);
        }
    }

    // // // // // // // // // // // // // // // // // // // // // // // // // // // // // //

    public class OpponentHandVertical extends JPanel {
        String alignment;
        ArrayList<Rectangle> mapCards;

        public OpponentHandVertical(String alignment) {
            setBackground(Color.ORANGE);
            mapCards = new ArrayList<>();
            this.alignment = alignment;
        }

        @Override
        public void invalidate() {
            super.invalidate();
            mapCards.clear();
            int cardHeight = 80;
            int cardWidth = 120;
            int yDelta = 27;
            int xPos = getWidth() - 20 - cardWidth;
            int yPos = (getHeight() - cardHeight - (hand.size() - 1)*yDelta)/2;
            int opponentID = alignment == "left"? playerID: playerID + 2;
            int opponentNumberOfCards = cardCount[opponentID % numOfPlayers];
            for(int i = 0; i < opponentNumberOfCards; ++i ) {
                Rectangle bounds = new Rectangle(xPos, yPos, cardWidth, cardHeight);
                mapCards.add(bounds);
                yPos += yDelta;
            }
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            BufferedImage cardImage = null;
            String path = "../img/background.png";
            try {
                cardImage = ImageIO.read(getClass().getResource(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
            for(Rectangle bounds: mapCards ) {
                if (bounds != null) {
                    g2d.drawImage(cardImage, bounds.x, bounds.y, bounds.width, bounds.height, null);
                    Graphics2D copy = (Graphics2D) g2d.create();
                    g2d.setColor(Color.BLACK);
                    g2d.draw(bounds);
                    paintCard(copy, bounds);
                    copy.dispose();
                }
            }
            g2d.dispose();
        }

        protected void paintCard(Graphics2D g2d, Rectangle bounds) {
            g2d.translate(bounds.x + 5, bounds.y + 5);
            g2d.setClip(0, 0, bounds.width - 5, bounds.height - 5);
        }
    }
}