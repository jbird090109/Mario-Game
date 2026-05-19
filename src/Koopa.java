import java.awt.*;
import java.awt.image.BufferedImage;

public class Koopa extends Enemy {
    private boolean shell;
    private int shellSpeed;

    public Koopa(double x, double y) {
        super(x, y);
    }

    public boolean isShell() {
        return shell;
    }

    public boolean isShellMoving() {
        return shell && Math.abs(vx) > 0.5;
    }

    @Override
    public void stomp() {
        if (!shell) {
            shell = true;
            vx = 0;
            height = 14;
            y += 2;
        }
    }

    public void kickShell(int dir) {
        shellKick(dir);
    }

    @Override
    public void shellKick(int dir) {
        shell = true;
        shellSpeed = dir * 6;
        vx = shellSpeed;
    }

    @Override
    public void update(Level level) {
        if (isDefeated()) {
            return;
        }
        if (dead && !shell) {
            deadTimer++;
            return;
        }
        if (shell) {
            x += vx;
            applyGravity(level);
            turnAtWalls(level);
            for (Enemy e : level.enemies) {
                if (e != this && !e.isDead() && intersects(e.getX(), e.getY(), e.getWidth(), e.getHeight())) {
                    e.stomp();
                }
            }
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
        BufferedImage img = shell ? SmwAssets.koopaShell : frame(SmwAssets.koopaWalk);
        g.drawImage(img, getX(), getY(), 16, height, null);
    }
}
