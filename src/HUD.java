import java.awt.*;

public class HUD {
    private int lives = 3;
    private int coins;
    private int score;
    private int timeLeft = 300;
    private long lastSecond;

    public HUD() {
        lastSecond = System.currentTimeMillis();
    }

    public void update() {
        long now = System.currentTimeMillis();
        if (now - lastSecond >= 1000) {
            lastSecond = now;
            if (timeLeft > 0) {
                timeLeft--;
            }
        }
    }

    public void draw(Graphics2D g, int screenW) {
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, screenW, 36);

        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.setColor(Color.WHITE);

        g.drawString("MARIO", 12, 14);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString(String.format("%06d", score), 12, 30);

        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("x", 118, 28);
        g.drawString(String.format("%02d", coins), 132, 28);

        g.drawString("WORLD", screenW / 2 - 30, 14);
        g.drawString("1-1", screenW / 2 - 8, 30);

        g.drawString("TIME", screenW - 72, 14);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString(String.format("%03d", timeLeft), screenW - 72, 30);

        for (int i = 0; i < lives; i++) {
            g.setColor(new Color(220, 0, 0));
            g.fillOval(screenW - 120 + i * 18, 8, 12, 12);
        }
    }

    public void addCoin() {
        coins++;
        if (coins >= 100) {
            coins -= 100;
            lives++;
        }
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void loseLife() {
        lives--;
    }

    public int getLives() {
        return lives;
    }

    public boolean isGameOver() {
        return lives <= 0;
    }
}
