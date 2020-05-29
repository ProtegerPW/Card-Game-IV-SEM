package ui.view;

import javax.swing.*;

public class MenuFrame extends JFrame {
    private JPanel menuPanel;
    private JButton playButton;
    private JButton exitButton;
    private JLabel gameTitle;

    public MenuFrame() {
        setSize(600,400);
        setContentPane(menuPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public JButton getPlayButton() {
        return playButton;
    }

    public JButton getExitButton() {
        return exitButton;
    }

    public JLabel getGameTitle() {
        return gameTitle;
    }
}
