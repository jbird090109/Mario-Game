import java.awt.*;
import java.awt.image.BufferedImage;

public class PowerUp {
    public enum Type { MUSHROOM, FLOWER, STAR }

    private final Type type;
    private double x;
    private double y;
    private double vx;
    private boolean active = true;
    private boolean emerged;
    private int emergeTimer;
    private final int startY;

    public PowerUp(int x, int y, Type type) {
        this.x = x;
        this.y = y;
        this.startY = y;
        this.type = type;
        this.vx = type == Type.MUSHROOM ? 1.2 : 0;
    }

    public void update(Level level) {
        if (!active) {
            return;
        }
        if (!emerged) {
            emergeTimer++;
            y = startY - emergeTimer * 0.5;
            if (emergeTimer > 32) {
                emerged = true;
                y = startY - SmwConstants.TILE;
            }
            return;
        }
        if (type == Type.MUSHROOM || type == Type.STAR) {
            x += vx;
            if (type == Type.STAR) {
                y += Math.sin(x * 0.1) * 2;
            }
            // simple floor collision
            int tx = (int) (x / SmwConstants.TILE);
            int ty = (int) ((y + 16) / SmwConstants.TILE);
            if (level.isSolidAt(tx, ty, false)) {
                y = ty * SmwConstants.TILE - 16;
            } else {
                y += 0.3;
            }
            if (level.isSolidAt((int) ((x + 16) / SmwConstants.TILE), (int) (y / SmwConstants.TILE), false)
                || level.isSolidAt((int) (x / SmwConstants.TILE), (int) (y / SmwConstants.TILE), false)) {
                vx = -vx;
            }
        }
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }

    public Type getType() {
        return type;
    }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }

    public int getWidth() {
        return 16;
    }

    public int getHeight() {
        return 16;
    }

    public void draw(Graphics2D g) {
        if (!active) {
            return;
        }
        BufferedImage img;
        switch (type) {
            case FLOWER:
                img = SmwAssets.flower;
                break;
            case STAR:
                img = SmwAssets.star;
                break;
            default:
                img = SmwAssets.mushroom;
        }
        g.drawImage(img, getX(), getY(), 16, 16, null);
    }

    public boolean intersects(int px, int py, int pw, int ph) {
        if (!active) {
            return false;
        }
        return px < getX() + 16 && px + pw > getX() && py < getY() + 16 && py + ph > getY();
    }

    public boolean canCollect() {
        return active && (emerged || emergeTimer > 8);
    }
}
