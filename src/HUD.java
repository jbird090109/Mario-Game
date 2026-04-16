import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class HUD {
    private int lives = 3;
    private int coins = 0;
    private long startTime;
    private int elapsedSeconds = 0;
    private static final int COUNTDOWN_START = 150;

    // Images for HUD elements (will be replaced later)
    private BufferedImage lifeImage;
    private BufferedImage coinImage;
    private BufferedImage timerImage;

    // HUD positioning
    private static final int PADDING = 20;
    private static final int ICON_SIZE = 32;
    private static final int SPACING = 15;

    public HUD() {
        startTime = System.currentTimeMillis();
        loadHUDImages();
    }

    private void loadHUDImages() {
        try {
            File assetsDir = new File("assets");
            if (!assetsDir.exists()) {
                assetsDir = new File("./assets");
            }
            if (!assetsDir.exists()) {
                assetsDir = new File(System.getProperty("user.dir") + "/assets");
            }

            // Load placeholder images - these can be replaced with actual graphics
            lifeImage = ImageIO.read(new File(assetsDir, "life.png"));
            coinImage = ImageIO.read(new File(assetsDir, "coin.png"));
            timerImage = ImageIO.read(new File(assetsDir, "timer.png"));

            if (lifeImage != null) {
                System.out.println("HUD images loaded successfully from: " + assetsDir.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not load HUD images: " + e.getMessage());
            System.err.println("HUD will display with placeholder boxes");
        }
    }

    public void update() {
        int secondsElapsed = (int) ((System.currentTimeMillis() - startTime) / 1000);
        elapsedSeconds = Math.max(0, COUNTDOWN_START - secondsElapsed);
    }

    public void draw(Graphics2D g, int panelWidth, int panelHeight) {
        // Set font for text
        Font font = new Font("Arial", Font.BOLD, 18);
        g.setFont(font);
        g.setColor(Color.WHITE);

        int yPosition = PADDING + ICON_SIZE / 2;

        // Draw Lives
        int livesX = PADDING;
        drawHUDElement(g, "Lives", lives, lifeImage, livesX, yPosition, panelWidth);

        // Draw Coins
        int coinsX = PADDING + 200;
        drawHUDElement(g, "Coins", coins, coinImage, coinsX, yPosition, panelWidth);

        // Draw Timer - aligned to the right
        int timerX = panelWidth - PADDING - 200;
        drawHUDElement(g, "Time", elapsedSeconds, timerImage, timerX, yPosition, panelWidth);
    }

    private void drawHUDElement(Graphics2D g, String label, int value, BufferedImage image, int x, int y, int panelWidth) {
        // Draw background box
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(x - 5, y - ICON_SIZE / 2 - 5, 150, ICON_SIZE + 10, 5, 5);

        // Draw border
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(x - 5, y - ICON_SIZE / 2 - 5, 150, ICON_SIZE + 10, 5, 5);

        // Draw image if loaded
        if (image != null) {
            g.drawImage(image, x, y - ICON_SIZE / 2, ICON_SIZE, ICON_SIZE, null);
        } else {
            // Draw placeholder box
            g.setColor(Color.GRAY);
            g.fillRect(x, y - ICON_SIZE / 2, ICON_SIZE, ICON_SIZE);
            g.setColor(Color.WHITE);
            g.drawRect(x, y - ICON_SIZE / 2, ICON_SIZE, ICON_SIZE);
        }

        // Draw label and value
        g.setColor(Color.WHITE);
        g.drawString(label + ": " + value, x + ICON_SIZE + SPACING, y + 7);
    }

    // Getters and setters
    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = Math.max(0, lives);
    }

    public void addLife() {
        lives++;
    }

    public void loseLife() {
        lives--;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = Math.max(0, coins);
    }

    public void addCoin() {
        coins++;
    }

    public int getElapsedSeconds() {
        return elapsedSeconds;
    }

    public boolean isGameOver() {
        return lives <= 0;
    }

    public void resetTimer() {
        startTime = System.currentTimeMillis();
        elapsedSeconds = COUNTDOWN_START;
    }
}
