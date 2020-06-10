package ui.view;

import javax.swing.*;

public class GameSetup extends JFrame {
    private JPanel setupPanel;
    private JLabel messageField;
    private JPanel button2Panel;
    private JPanel button4Panel;
    private JButton button2;
    private JButton button4;

    public GameSetup() {
        setSize(400,280);
        setTitle("Game setup");
        setContentPane(setupPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public JButton getButton2() {
        return button2;
    }

    public JButton getButton4() {
        return button4;
    }

    public void showGameSetupWindow() {
        this.setVisible(true);
    }

    public void closeGameSetup() {
        this.dispose();
    }
}
