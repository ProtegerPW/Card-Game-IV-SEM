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
    public JPanel stockpilePanel;

    private JButton drawCardsButton;
    private JButton playSelectedButton;

    private int numOfPlayers;
    private int playerID;
    private int cardCount[];
    private ArrayList<PanCard> hand;
    private Map<PanCard, Rectangle> mapPlayerHand;
    private ArrayList<PanCard> stockpile;
    private Map<PanCard, Rectangle> mapStockpile;


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
        stockpilePanel = new StockpilePanel();
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


    public JButton getDrawCardsButton() {
        return drawCardsButton;
    }

    public JButton getPlaySelectedButton() {
        return playSelectedButton;
    }

    public void enableDrawCardsButton() {
        drawCardsButton.setEnabled(true);
    }

    public void disableDrawCardsButton() {
        drawCardsButton.setEnabled(false);
    }

    public void enablePlaySelectedButton() {
        playSelectedButton.setEnabled(true);
    }

    public void disablePlaySelectedButton() {
        playSelectedButton.setEnabled(false);
    }

    // // // // // // // // // // // // // // // // // // // // // // // // // // // // // //

    public class PlayerHand extends JPanel {
        final int CARD_HEIGHT = 180;
        final int CARD_WIDTH = 120;
        final int DELTA_X = 40;
        final int MARGIN = 20;

        public PlayerHand() {
            mapPlayerHand = new HashMap<>(1);
            drawCardsButton = new JButton("draw");
            playSelectedButton = new JButton("play selected card");
            addButtonsToFrame();
            drawCardsButton.setEnabled(false);
            playSelectedButton.setEnabled(false);
            setBackground(Color.CYAN);
        }

        public void addButtonsToFrame() {
            add(drawCardsButton);
            add(playSelectedButton);
        }

        @Override
        public void invalidate() {
            super.invalidate();
            mapPlayerHand.clear();
            int xPos = ((getWidth() - CARD_WIDTH - (hand.size() - 1)*DELTA_X)/2);
            int yPos = getHeight() - MARGIN - CARD_HEIGHT;
            for (PanCard card: hand) {
                Rectangle bounds = new Rectangle(xPos, yPos, CARD_WIDTH, CARD_HEIGHT);
                mapPlayerHand.put(card, bounds);
                xPos += DELTA_X;
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

        final int CARD_HEIGHT = 120;
        final int CARD_WIDTH = 80;
        final int DELTA_X = 27;
        final int MARGIN = 20;

        public OpponentHandHorizontal() {
            mapCards = new ArrayList<>();
            setBackground(Color.MAGENTA);
        }

        @Override
        public void invalidate() {
            super.invalidate();
            mapCards.clear();
            int xPos = ((getWidth() - CARD_WIDTH - (hand.size() - 1)*DELTA_X)/2);
            int yPos = MARGIN;
            int opponentNumberOfCards = cardCount[(playerID + 1)%numOfPlayers];
            for(int i = 0; i < opponentNumberOfCards; ++i ) {
                Rectangle bounds = new Rectangle(xPos, yPos, CARD_WIDTH, CARD_HEIGHT);
                mapCards.add(bounds);
                xPos += DELTA_X;
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

        final int CARD_HEIGHT = 80;
        final int CARD_WIDTH = 120;
        final int DELTA_Y = 27;
        final int MARGIN = 20;

        public OpponentHandVertical(String alignment) {
            setBackground(Color.ORANGE);
            mapCards = new ArrayList<>();
            this.alignment = alignment;
        }

        @Override
        public void invalidate() {
            super.invalidate();
            mapCards.clear();
            int xPos = alignment == "left"? MARGIN: getWidth() - MARGIN - CARD_WIDTH;
            int handHeight = CARD_HEIGHT + (hand.size() - 1)*DELTA_Y;
            int yPos = alignment == "left"? (getHeight() + handHeight)/2  - CARD_HEIGHT: (getHeight() - handHeight)/2;
            int opponentID = alignment == "left"? playerID: playerID + 2;
            int opponentNumberOfCards = cardCount[opponentID % numOfPlayers];
            for(int i = 0; i < opponentNumberOfCards; ++i ) {
                Rectangle bounds = new Rectangle(xPos, yPos, CARD_WIDTH, CARD_HEIGHT);
                mapCards.add(bounds);
                yPos = alignment == "left"? yPos - DELTA_Y: yPos + DELTA_Y;
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

    public class StockpilePanel extends JPanel {
        final int CARD_HEIGHT = 180;
        final int CARD_WIDTH = 120;
        final int DELTA_X = 40;

        public StockpilePanel() {
            stockpile = new ArrayList<>();
            mapStockpile = new HashMap<>(1);
        }

        @Override
        public void invalidate() {
            super.invalidate();
            mapStockpile.clear();
            int xPos = (getWidth() - CARD_WIDTH)/2;
            int yPos = (getHeight() - CARD_HEIGHT)/2;
            for (PanCard card: stockpile) {
                Rectangle bounds = new Rectangle(xPos, yPos, CARD_WIDTH, CARD_HEIGHT);
                mapStockpile.put(card, bounds);
                xPos += DELTA_X;
            }
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            String path;
            for(PanCard card: stockpile) {
                Rectangle bounds = mapStockpile.get(card);
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
}