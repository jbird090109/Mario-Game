import java.awt.*;
import java.awt.image.BufferedImage;

public class Obstacles {
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected BufferedImage image;

    public Obstacles(int x, int y, int width, int height, BufferedImage image) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
    }

    public void draw(Graphics2D g) {
        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
        }
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

    public boolean intersects(int px, int py, int pw, int ph) {
        return px < x + width &&
               px + pw > x &&
               py < y + height &&
               py + ph > y;
    }
}
