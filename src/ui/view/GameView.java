package ui.view;

import com.company.PanCard;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.AffineTransformOp;
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

    private int currentPlayer;

    private int numOfPlayers;
    private int playerID;
    private int cardCount[];
    private ArrayList<PanCard> hand;
    private Map<PanCard, Rectangle> mapPlayerHand;
    private ArrayList<PanCard> stockpile;
    private Map<PanCard, Rectangle> mapStockpile;

    public GameView(int playerID, int currentPlayer, ArrayList<PanCard> hand, int cardCount[]) {
        this.playerID = playerID;
        this.currentPlayer = currentPlayer;
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

    public ArrayList<PanCard> getStockpile() {
        return stockpile;
    }

    public void setStockpile(ArrayList<PanCard> stockpile) {
        this.stockpile = stockpile;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
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

    public boolean endGameWindow() {
        boolean newGame;
        String message = "Game Finished - Player #" + currentPlayer + " lost.\n Do you want to play again?";
        int option = JOptionPane.showConfirmDialog(this, message,"Game finished", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION)
            newGame= true;
        else
            newGame = false;
        return newGame;
    }

    // // // // // // // // // // // // // // // // // // // // // // // // // // // // // //

    public class PlayerHand extends JPanel {
        int cardHeight;
        int cardWidth;
        int deltaX;
        int bottomMargin;

        final int SMALL_BOTTOM = 10;
        final int BOTTOM = 20;
        final int TOP_MARGIN = 50;
        final int SIDE_MARGIN = 10;
        final int MAX_CARD_HEIGHT = 230;

        public PlayerHand() {
            mapPlayerHand = new HashMap<>(1);
            drawCardsButton = new JButton("draw");
            playSelectedButton = new JButton("play selected card");
            drawCardsButton.setEnabled(false);
            playSelectedButton.setEnabled(false);
            addElementsToFrame();
            //setBackground(Color.CYAN);
        }

        public void addElementsToFrame() {
            add(drawCardsButton);
            add(playSelectedButton);
        }

        @Override
        public void invalidate() {
            super.invalidate();
            mapPlayerHand.clear();
            if(hand.size() == 0)
                return;
            //
            bottomMargin = getHeight() < 200? SMALL_BOTTOM : BOTTOM;
            cardHeight = Math.min(getHeight() - TOP_MARGIN - bottomMargin, MAX_CARD_HEIGHT);
            cardWidth = 2 * cardHeight / 3;
            deltaX = Math.min(2 * cardHeight / 9, (getWidth() - 2 * SIDE_MARGIN - cardWidth)/hand.size());
            //
            int xPos = ((getWidth() - cardWidth - (hand.size() - 1)* deltaX)/2);
            int yPos = getHeight() - bottomMargin - cardHeight;
            for (PanCard card: hand) {
                Rectangle bounds = new Rectangle(xPos, yPos, cardWidth, cardHeight);
                mapPlayerHand.put(card, bounds);
                xPos += deltaX;
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

        int cardHeight;
        int cardWidth;
        int deltaX;
        int margin;

        final int SMALL_MARGIN = 10;
        final int MARGIN = 20;
        final int MAX_CARD_HEIGHT = 160;

        public OpponentHandHorizontal() {
            mapCards = new ArrayList<>();
            //setBackground(Color.MAGENTA);
        }

        @Override
        public void invalidate() {
            super.invalidate();
            mapCards.clear();
            int index = numOfPlayers == 2? playerID % numOfPlayers : (playerID + 1) % numOfPlayers;
            int opponentNumberOfCards = cardCount[index];
            if(opponentNumberOfCards == 0) {
                return;
            }
            margin = getHeight() < 140? SMALL_MARGIN : MARGIN;
            cardHeight = Math.min(getHeight() - 2 * margin, MAX_CARD_HEIGHT);
            cardWidth = 2 * cardHeight / 3;
            deltaX = Math.min(2 * cardHeight / 9, (getWidth() - 2 * SMALL_MARGIN - cardWidth)/opponentNumberOfCards);
            //
            int xPos = ((getWidth() - cardWidth - (opponentNumberOfCards - 1)* deltaX)/2);
            int yPos = margin;
            for(int i = 0; i < opponentNumberOfCards; ++i ) {
                Rectangle bounds = new Rectangle(xPos, yPos, cardWidth, cardHeight);
                mapCards.add(bounds);
                xPos += deltaX;
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
        //
        int cardHeight;
        int cardWidth;
        int deltaY;
        int margin;
        //
        final int SMALL_MARGIN = 10;
        final int MARGIN = 20;
        final int MAX_CARD_WIDTH = 160;
        final int ROT_DEG_LEFT = -90;
        final int ROT_DEG_RIGHT = 90;

        public OpponentHandVertical(String alignment) {
            mapCards = new ArrayList<>();
            this.alignment = alignment;
            //setBackground(Color.ORANGE);
        }

        @Override
        public void invalidate() {
            super.invalidate();
            mapCards.clear();
            int opponentID = alignment.equals("left") ? playerID: playerID + 2;
            int opponentNumberOfCards = cardCount[opponentID % numOfPlayers];
            if(opponentNumberOfCards == 0) {
                return;
            }
            //
            margin = getWidth() < 140? SMALL_MARGIN : MARGIN;
            int xPos = alignment.equals("left") ? margin : getWidth() - margin - cardWidth;
            cardWidth = Math.min(getWidth() - 2 * margin, MAX_CARD_WIDTH);
            cardHeight = 2 * cardWidth / 3;
            deltaY = Math.min(2 * cardWidth / 9, (getHeight() - 2 * SMALL_MARGIN - cardHeight)/opponentNumberOfCards);
            //
            int handHeight = cardHeight + (opponentNumberOfCards - 1) * deltaY;
            int yPos = alignment.equals("left") ? (getHeight() + handHeight)/2  - cardHeight : (getHeight() - handHeight)/2;
            for(int i = 0; i < opponentNumberOfCards; ++i ) {
                Rectangle bounds = new Rectangle(xPos, yPos, cardWidth, cardHeight);
                mapCards.add(bounds);
                yPos = alignment.equals("left") ? yPos - deltaY : yPos + deltaY;
            }
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            BufferedImage cardImage = null;
            BufferedImage rotatedImage = null;
            String path = "../img/background.png";
            int degrees = alignment.equals("left") ? ROT_DEG_LEFT: ROT_DEG_RIGHT;
            try {
                cardImage = ImageIO.read(getClass().getResource(path));
                rotatedImage = new BufferedImage(cardImage.getHeight(), cardImage.getWidth(), cardImage.getType());
            } catch (IOException e) {
                e.printStackTrace();
            }
            assert cardImage != null;
            rotateImage(cardImage, rotatedImage, degrees);
            for(Rectangle bounds: mapCards ) {
                if (bounds != null) {
                    g2d.drawImage(rotatedImage, bounds.x, bounds.y, bounds.width, bounds.height, null);
                    Graphics2D copy = (Graphics2D) g2d.create();
                    g2d.setColor(Color.BLACK);
                    g2d.draw(bounds);
                    paintCard(copy, bounds);
                    copy.dispose();
                }
            }
            g2d.dispose();
        }

        protected void rotateImage(BufferedImage source, BufferedImage destination, int degrees) {
            AffineTransform at = new AffineTransform();
            at.translate((float) source.getHeight()/2, (float) source.getWidth()/2);
            at.rotate(Math.toRadians(degrees),0,0);
            at.translate((float) -source.getWidth()/2, (float) -source.getHeight()/2);
            AffineTransformOp rotateImage = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
            rotateImage.filter(source, destination);
        }

        protected void paintCard(Graphics2D g2d, Rectangle bounds) {
            g2d.translate(bounds.x + 5, bounds.y + 5);
            g2d.setClip(0, 0, bounds.width - 5, bounds.height - 5);
        }
    }

    // // // // // // // // // // // // // // // // // // // // // // // // // // // // // //

    public class StockpilePanel extends JPanel {
        private final ArrayList<PanCard> cardsForDisplay;
        private final Path2D turnIcon;
        private JLabel[] names;
        //
        int cardHeight;
        int cardWidth;
        int deltaX;

        final int OVERLAP = 5;
        final int MARGIN = 20;
        final int PILE_MARGIN = 50;
        final int MAX_CARD_HEIGHT = 180;
        final int TURN_ICON_WIDTH = 40;
        final int TURN_ICON_HEIGHT = 20;

        public StockpilePanel() {
            setLayout(new BorderLayout());
            stockpile = new ArrayList<>();
            cardsForDisplay = new ArrayList<>();
            mapStockpile = new HashMap<>(1);
            turnIcon = new Path2D.Double();
            setLabels();
            //setBackground(Color.YELLOW);
        }

        @Override
        public void invalidate() {
            super.invalidate();
            mapStockpile.clear();
            cardsForDisplay.clear();
            PanCard card;
            Rectangle bounds;
            turnIcon.reset();
            setLabelsPositions();
            //
            cardHeight = Math.min(getHeight() - 2 * PILE_MARGIN, MAX_CARD_HEIGHT);
            cardWidth = 2 * cardHeight / 3;
            deltaX = 2 * cardHeight / 9;
            //
            int xPos = (getWidth() - cardWidth)/2;
            int yPos = (getHeight() - cardHeight)/2;
            int pileSize = stockpile.size();
            int i = Math.max(pileSize - 4, 0);
            if(pileSize > 4) {
                xPos -= OVERLAP;
                card = stockpile.get(pileSize - 5);
                bounds = new Rectangle(xPos, yPos, cardWidth, cardHeight);
                mapStockpile.put(card, bounds);
                cardsForDisplay.add(card);
                xPos += OVERLAP;
            }
            for(; i < pileSize; ++i) {
                card = stockpile.get(i);
                bounds = new Rectangle(xPos, yPos, cardWidth, cardHeight);
                mapStockpile.put(card, bounds);
                cardsForDisplay.add(card);
                xPos += deltaX;
            }
            getTurnIcon();
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            Rectangle bounds;
            String path;
            for(PanCard card: cardsForDisplay) {
                bounds = mapStockpile.get(card);
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
            // draw icon
            g2d.setColor(Color.YELLOW);
            g2d.fill(turnIcon);
            g2d.setColor(Color.BLACK);
            g2d.draw(turnIcon);
            g2d.dispose();
        }

        protected void paintCard(Graphics2D g2d, Rectangle bounds) {
            g2d.translate(bounds.x + 5, bounds.y + 5);
            g2d.setClip(0, 0, bounds.width - 5, bounds.height - 5);
        }

        private void getTurnIcon() {
            int posX, posY, iconPosition;
            if(numOfPlayers == 2) {
                if(currentPlayer == playerID)
                    iconPosition = 0;
                else
                    iconPosition = 2;
            }
            else {
                iconPosition = currentPlayer - playerID;
                iconPosition += iconPosition < 0 ? 4 : 0;
            }
            switch(iconPosition) {
                case 0:
                    posX = (getWidth() - TURN_ICON_WIDTH)/2;
                    posY = getHeight() - TURN_ICON_HEIGHT - MARGIN;
                    turnIcon.moveTo(posX, posY);
                    turnIcon.lineTo(posX + TURN_ICON_WIDTH, posY);
                    turnIcon.lineTo(posX + TURN_ICON_WIDTH/2, posY + TURN_ICON_HEIGHT);
                    turnIcon.closePath();
                    break;

                case 1:
                    posX = TURN_ICON_HEIGHT + MARGIN;
                    posY = (getHeight() - TURN_ICON_WIDTH)/2;
                    turnIcon.moveTo(posX, posY);
                    turnIcon.lineTo(posX, posY + TURN_ICON_WIDTH);
                    turnIcon.lineTo(posX - TURN_ICON_HEIGHT, posY + TURN_ICON_WIDTH/2);
                    turnIcon.closePath();
                    break;

                case 2:
                    posX = (getWidth() - TURN_ICON_WIDTH)/2;
                    posY = TURN_ICON_HEIGHT + MARGIN;
                    turnIcon.moveTo(posX, posY);
                    turnIcon.lineTo(posX + TURN_ICON_WIDTH, posY);
                    turnIcon.lineTo(posX + TURN_ICON_WIDTH/2, posY - TURN_ICON_HEIGHT);
                    turnIcon.closePath();
                    break;

                case 3:
                    posX = getWidth() - TURN_ICON_HEIGHT - MARGIN;
                    posY = (getHeight() - TURN_ICON_WIDTH)/2;
                    turnIcon.moveTo(posX, posY);
                    turnIcon.lineTo(posX, posY + TURN_ICON_WIDTH);
                    turnIcon.lineTo(posX + TURN_ICON_HEIGHT, posY + TURN_ICON_WIDTH/2);
                    turnIcon.closePath();
                    break;
            }
        }

        private void setLabels() {
            names = new JLabel[numOfPlayers];
            int ID;
            for(int i = 0; i < numOfPlayers; ++i) {
                ID = playerID + i;
                ID -= ID > numOfPlayers? numOfPlayers : 0;
                names[i] = new JLabel("#" + ID);
            }
        }

        private void setLabelsPositions() {
            add(names[0], BorderLayout.SOUTH);
            names[0].setHorizontalAlignment(SwingConstants.CENTER);
            if(numOfPlayers == 2) {
                add(names[1], BorderLayout.NORTH);
                names[1].setHorizontalAlignment(SwingConstants.CENTER);
            }
            else {
                add(names[1], BorderLayout.WEST);
                add(names[2], BorderLayout.NORTH);
                names[2].setHorizontalAlignment(SwingConstants.CENTER);
                add(names[3], BorderLayout.EAST);
            }
        }
    }
}