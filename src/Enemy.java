import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Enemy {
    protected double x;
    protected double y;
    protected double vx;
    protected int width = 16;
    protected int height = 16;
    protected boolean dead;
    protected int deadTimer;
    protected int anim;

    public Enemy(double x, double y) {
        this.x = x;
        this.y = y;
        this.vx = -1.2;
    }

    public abstract void update(Level level);

    public abstract void draw(Graphics2D g);

    public boolean isDead() {
        return dead;
    }

    public boolean isDefeated() {
        return dead && deadTimer > 30;
    }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void stomp() {
        if (!dead) {
            dead = true;
            deadTimer = 0;
            vx = 0;
        }
    }

    public void shellKick(int dir) {
        // override in Koopa
    }

    public boolean intersects(int px, int py, int pw, int ph) {
        return !isDefeated() && px < getX() + width && px + pw > getX() && py < getY() + height && py + ph > getY();
    }

    protected void applyGravity(Level level) {
        y += 0.4;
        int tx = (int) (x / SmwConstants.TILE);
        int ty = (int) ((y + height) / SmwConstants.TILE);
        if (level.isSolidAt(tx, ty, false) || level.isSolidAt((int) ((x + width) / SmwConstants.TILE), ty, false)) {
            y = ty * SmwConstants.TILE - height;
        }
    }

    protected void turnAtWalls(Level level) {
        int nextX = vx > 0 ? (int) ((x + width + 2) / SmwConstants.TILE) : (int) ((x - 2) / SmwConstants.TILE);
        int ty = (int) ((y + height / 2) / SmwConstants.TILE);
        if (level.isSolidAt(nextX, ty, false) || level.isSolidAt(nextX, ty - 1, false)) {
            vx = -vx;
        }
    }

    protected BufferedImage frame(BufferedImage[] frames) {
        anim++;
        return frames[(anim / 8) % frames.length];
    }
}
