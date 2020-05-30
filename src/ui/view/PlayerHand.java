package ui.view;

import com.company.PanCard;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class PlayerHand extends JPanel {
    private Map<PanCard, Rectangle> hand;

    public PlayerHand() {
        setSize(-1, 150);
        setPreferredSize(new Dimension(-1,150));
        setMaximumSize(new Dimension(-1,200));
        setBackground(Color.CYAN);
    }
}