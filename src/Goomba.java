import java.awt.*;
import java.awt.image.BufferedImage;

public class Goomba extends Enemy {
    public Goomba(double x, double y) {
        super(x, y);
    }

    @Override
    public void update(Level level) {
        if (isDefeated()) {
            return;
        }
        if (dead) {
            deadTimer++;
            return;
        }
        x += vx;
        applyGravity(level);
        turnAtWalls(level);
    }

    @Override
    public void draw(Graphics2D g) {
        if (isDefeated()) {
            return;
        }
        if (dead) {
            g.drawImage(SmwAssets.goombaWalk[0], getX(), getY() + 8, 16, 8, null);
            return;
        }
        BufferedImage img = frame(SmwAssets.goombaWalk);
        g.drawImage(img, getX(), getY(), 16, 16, null);
    }
}
