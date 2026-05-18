import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class GamePanel extends JPanel implements KeyListener, Runnable {
    private static final int LOGIC_W = SmwConstants.SNES_WIDTH;
    private static final int LOGIC_H = SmwConstants.SNES_HEIGHT;
    private static final int SCREEN_W = SmwConstants.SCREEN_W;
    private static final int SCREEN_H = SmwConstants.SCREEN_H;

    private GameState state;
    private Camera camera;
    private Thread gameThread;
    private boolean running;
    private int deathTimer;
    private boolean paused;

    public GamePanel() {
        setPreferredSize(new Dimension(SCREEN_W, SCREEN_H + 40));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        SmwAssets.init();
        resetLevel();
    }

    private void resetLevel() {
        Level level = Level.yoshisIsland1();
        HUD hud = new HUD();
        int startY = (level.height - 2) * SmwConstants.TILE - SmwConstants.MARIO_H_SMALL;
        state = new GameState(null, level, hud);
        state.player = new Player(state, startY);
        camera = new Camera(LOGIC_W, LOGIC_H, level.getPixelWidth(), level.getPixelHeight());
        deathTimer = 0;
        paused = false;
        state.gameOver = false;
        state.levelComplete = false;
    }

    public void start() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        long last = System.nanoTime();
        double nsPerTick = 1_000_000_000.0 / SmwConstants.FPS;
        double delta = 0;
        while (running) {
            long now = System.nanoTime();
            delta += (now - last) / nsPerTick;
            last = now;
            while (delta >= 1) {
                update();
                repaint();
                delta--;
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private void update() {
        if (paused || state.levelComplete) {
            return;
        }

        state.hud.update();
        state.hud.setScore(state.score);

        if (state.invincibleFrames > 0) {
            state.invincibleFrames--;
            if (state.invincibleFrames <= 0) {
                state.starPower = false;
            }
        }

        if (state.player.isDead()) {
            deathTimer++;
            state.player.update();
            if (deathTimer > 90) {
                if (state.hud.isGameOver()) {
                    state.gameOver = true;
                } else {
                    resetLevel();
                }
            }
            return;
        }

        state.player.update();
        camera.update(state.player);

        for (Coin c : state.level.coins) {
            c.update();
            if (c.intersects(state.player.getHitboxX(), state.player.getHitboxY(),
                    state.player.getHitboxW(), state.player.getHitboxH())) {
                c.collect();
                state.hud.addCoin();
                state.score += 200;
            }
        }

        for (PowerUp p : state.level.powerUps) {
            p.update(state.level);
            if (p.intersects(state.player.getHitboxX(), state.player.getHitboxY(),
                    state.player.getHitboxW(), state.player.getHitboxH())) {
                state.player.powerUp(p.getType());
                p.deactivate();
            }
        }

        for (Enemy e : state.level.enemies) {
            e.update(state.level);
            if (e.isDefeated()) {
                continue;
            }
            if (!e.intersects(state.player.getHitboxX(), state.player.getHitboxY(),
                    state.player.getHitboxW(), state.player.getHitboxH())) {
                continue;
            }
            boolean stomp = state.player.getPrevVelY() > 0
                    && state.player.getHitboxY() + state.player.getHitboxH() - 4 < e.getY() + e.getHeight() / 2;
            if (stomp) {
                e.stomp();
                state.player.setVelY(-5);
                state.score += 200;
            } else if (state.starPower) {
                e.stomp();
                state.score += 200;
            } else {
                state.player.takeDamage();
            }
        }

        if (state.player.getX() >= state.level.getGoalX()) {
            state.levelComplete = true;
            state.score += 5000;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        BufferedImage frame = new BufferedImage(LOGIC_W, LOGIC_H, BufferedImage.TYPE_INT_ARGB);
        Graphics2D lg = frame.createGraphics();
        lg.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        int camX = camera.getX();
        int camY = camera.getY();
        lg.translate(-camX, -camY);

        state.level.drawBackground(lg, camX, camY, LOGIC_W, LOGIC_H);
        state.level.drawTiles(lg, camX, camY, LOGIC_W, LOGIC_H);

        for (Coin c : state.level.coins) {
            c.draw(lg);
        }
        for (PowerUp p : state.level.powerUps) {
            p.draw(lg);
        }
        for (Enemy e : state.level.enemies) {
            e.draw(lg);
        }
        state.player.draw(lg);
        lg.dispose();

        AffineTransform at = new AffineTransform();
        at.scale(SmwConstants.SCALE, SmwConstants.SCALE);
        g2d.drawImage(frame, at, null);

        state.hud.draw(g2d, SCREEN_W);

        if (state.levelComplete) {
            drawCentered(g2d, "COURSE CLEAR!", Color.WHITE);
        } else if (state.gameOver) {
            drawCentered(g2d, "GAME OVER", Color.RED);
        } else if (paused) {
            drawCentered(g2d, "PAUSED", Color.YELLOW);
        }

        g2d.setColor(Color.GRAY);
        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        g2d.drawString("Arrows: move | Z/Shift: run | Space: jump | P: pause | R: restart", 8, SCREEN_H + 28);
    }

    private void drawCentered(Graphics2D g, String text, Color c) {
        g.setFont(new Font("Arial", Font.BOLD, 28));
        g.setColor(c);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, (SCREEN_W - fm.stringWidth(text)) / 2, SCREEN_H / 2);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_P) {
            paused = !paused;
            return;
        }
        if (e.getKeyCode() == KeyEvent.VK_R) {
            resetLevel();
            return;
        }
        if (!state.gameOver && !state.levelComplete) {
            state.player.handleKeyPress(e.getKeyCode());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        state.player.handleKeyRelease(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
