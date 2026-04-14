import javax.swing.*;

public class Game extends JFrame {
    private GamePanel gamePanel;

    public Game() {
        setTitle("Mario Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        gamePanel = new GamePanel();
        add(gamePanel);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void run() {
        gamePanel.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Game());
    }
}
