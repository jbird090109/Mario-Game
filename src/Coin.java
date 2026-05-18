import java.awt.*;
import java.awt.image.BufferedImage;

public class Coin {
    private final int x;
    private final int y;
    private final boolean blockCoin;
    private boolean collected;
    private int anim;

    public Coin(int x, int y) {
        this(x, y, false);
    }

    public Coin(int x, int y, boolean blockCoin) {
        this.x = x;
        this.y = y;
        this.blockCoin = blockCoin;
    }

    public void update() {
        anim++;
    }

    public boolean isCollected() {
        return collected;
    }

    public void collect() {
        collected = true;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y + (blockCoin ? -Math.min(anim, 20) : (int) (Math.sin(anim * 0.15) * 3));
    }

    public int getWidth() {
        return 12;
    }

    public int getHeight() {
        return 16;
    }

    public void draw(Graphics2D g) {
        if (collected) {
            return;
        }
        BufferedImage img = SmwAssets.coinSpin;
        g.drawImage(img, x, getY(), 12, 16, null);
    }

    public boolean intersects(int px, int py, int pw, int ph) {
        return !collected && px < x + 12 && px + pw > x && py < getY() + 16 && py + ph > getY();
    }
}
