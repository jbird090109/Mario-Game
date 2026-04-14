import java.awt.*;

public class Enemy {
    private int x;
    private int y;
    private int width = 32;
    private int height = 32;
    private int velocityX;
    private int speed = 2;

    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
        this.velocityX = -speed;
    }

    public void update() {
        x += velocityX;

        if (x < 0 || x > 800) {
            velocityX = -velocityX;
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(new Color(139, 69, 19));
        g.fillRect(x, y, width, height);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
