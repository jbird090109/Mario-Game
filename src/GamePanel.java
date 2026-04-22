import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements KeyListener, Runnable {
    private static final int WIDTH = 1200;
    private static final int HEIGHT = 800;
    private static final int LEVEL_WIDTH = 4000;  // Extended level width
    private static final int LEVEL_HEIGHT = 1000; // Extended level height
    private static final int FPS = 60;

    private Player player;
    private HUD hud;
    private Camera camera;
    private Thread gameThread;
    private boolean running = false;
    
    private BufferedImage backgroundImage;
    private List<Platform> platforms;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(135, 206, 235));
        setFocusable(true);
        addKeyListener(this);
        
        platforms = new ArrayList<>();
        loadBackgroundAndPlatforms();

        player = new Player(100, 650, platforms, LEVEL_WIDTH, LEVEL_HEIGHT);
        camera = new Camera(WIDTH, HEIGHT, LEVEL_WIDTH, LEVEL_HEIGHT, player);
        hud = new HUD();
    }
    
    private void loadBackgroundAndPlatforms() {
        try {
            File assetsDir = new File("assets");
            if (!assetsDir.exists()) {
                assetsDir = new File("./assets");
            }
            if (!assetsDir.exists()) {
                assetsDir = new File(System.getProperty("user.dir") + "/assets");
            }
            
            File bgFile = new File(assetsDir, "SNES - Super Mario World - Donut Plains Stages - Donut Plains 4.png");
            if (bgFile.exists()) {
                backgroundImage = ImageIO.read(bgFile);
                System.out.println("Background image loaded: " + bgFile.getAbsolutePath());
                System.out.println("Background size: " + backgroundImage.getWidth() + "x" + backgroundImage.getHeight());
            } else {
                System.err.println("Background image not found at: " + bgFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Error loading background: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Create expanded terrain for a 4000px wide level
        // Repeating and extending the terrain pattern
        
        // SECTION 1: Start area (0-500px)
        platforms.add(new Platform(0, 675, 100, 125));
        platforms.add(new Platform(100, 680, 150, 120));
        platforms.add(new Platform(250, 660, 100, 140));
        platforms.add(new Platform(350, 640, 170, 160));
        platforms.add(new Platform(520, 635, 130, 165));
        
        // SECTION 2: Early challenge (500-1000px)
        platforms.add(new Platform(500, 650, 190, 150));
        platforms.add(new Platform(690, 665, 150, 135));
        platforms.add(new Platform(840, 655, 220, 145));
        platforms.add(new Platform(1060, 670, 130, 130));
        
        // Ground filler section 1
        platforms.add(new Platform(300, 695, 80, 105));
        platforms.add(new Platform(700, 695, 90, 105));
        
        // SECTION 3: Mid-level (1000-1500px)
        platforms.add(new Platform(1000, 640, 200, 160));
        platforms.add(new Platform(1200, 660, 150, 140));
        platforms.add(new Platform(1350, 630, 180, 170));
        platforms.add(new Platform(1530, 665, 170, 135));
        
        // Ground filler section 2
        platforms.add(new Platform(1100, 695, 100, 105));
        
        // SECTION 4: Challenge area (1500-2000px)
        platforms.add(new Platform(1500, 600, 120, 200));
        platforms.add(new Platform(1620, 650, 100, 150));
        platforms.add(new Platform(1720, 620, 110, 180));
        platforms.add(new Platform(1830, 660, 130, 140));
        platforms.add(new Platform(1960, 640, 140, 160));
        
        // Ground filler section 3
        platforms.add(new Platform(1550, 695, 85, 105));
        platforms.add(new Platform(1950, 695, 95, 105));
        
        // SECTION 5: Mid-game expansion (2000-2500px)
        platforms.add(new Platform(2000, 670, 180, 130));
        platforms.add(new Platform(2180, 645, 160, 155));
        platforms.add(new Platform(2340, 665, 150, 135));
        platforms.add(new Platform(2490, 640, 170, 160));
        
        // Ground filler section 4
        platforms.add(new Platform(2100, 695, 100, 105));
        platforms.add(new Platform(2400, 695, 90, 105));
        
        // SECTION 6: Upper challenge (2500-3000px)
        platforms.add(new Platform(2500, 600, 150, 200));
        platforms.add(new Platform(2650, 630, 140, 170));
        platforms.add(new Platform(2790, 655, 130, 145));
        platforms.add(new Platform(2920, 620, 160, 180));
        
        // Ground filler section 5
        platforms.add(new Platform(2550, 695, 100, 105));
        platforms.add(new Platform(2850, 695, 95, 105));
        
        // SECTION 7: Late game (3000-3500px)
        platforms.add(new Platform(3000, 665, 170, 135));
        platforms.add(new Platform(3170, 645, 150, 155));
        platforms.add(new Platform(3320, 670, 160, 130));
        platforms.add(new Platform(3480, 640, 140, 160));
        
        // Ground filler section 6
        platforms.add(new Platform(3050, 695, 100, 105));
        platforms.add(new Platform(3300, 695, 85, 105));
        
        // SECTION 8: Final stretch (3500-4000px)
        platforms.add(new Platform(3500, 620, 180, 180));
        platforms.add(new Platform(3680, 660, 150, 140));
        platforms.add(new Platform(3830, 630, 170, 170));
        platforms.add(new Platform(4000, 675, 100, 125));
        
        // Ground filler section 7
        platforms.add(new Platform(3550, 695, 100, 105));
        platforms.add(new Platform(3900, 695, 90, 105));
        
        System.out.println("Total platforms created: " + platforms.size());
    }

    public void start() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountTicks = 60.0;
        double ns = 1000000000 / amountTicks;
        double delta = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    private void update() {
        player.update();
        camera.update();
        hud.update();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Save the current graphics state
        AffineTransform originalTransform = g2d.getTransform();
        
        // Apply camera translation
        g2d.translate(-camera.getX(), -camera.getY());
        
        // Draw background image tiled across the level
        if (backgroundImage != null) {
            int bgWidth = backgroundImage.getWidth();
            int bgHeight = backgroundImage.getHeight();
            
            // Calculate which tiles to draw based on camera position
            int startX = (camera.getX() / bgWidth) * bgWidth;
            int startY = (camera.getY() / bgHeight) * bgHeight;
            int endX = startX + WIDTH + bgWidth;
            int endY = startY + HEIGHT + bgHeight;
            
            for (int x = startX; x < endX; x += bgWidth) {
                for (int y = startY; y < endY; y += bgHeight) {
                    g2d.drawImage(backgroundImage, x, y, bgWidth, bgHeight, null);
                }
            }
        } else {
            // Fallback: draw solid background
            g2d.setColor(new Color(135, 206, 235));
            g2d.fillRect(camera.getX(), camera.getY(), WIDTH, HEIGHT);
        }
        
        // Debug: Uncomment to visualize platform hitboxes
        // for (Platform p : platforms) {
        //     g2d.setColor(new Color(0, 255, 0, 50));
        //     g2d.fillRect(p.getX(), p.getY(), p.getWidth(), p.getHeight());
        //     g2d.setColor(Color.GREEN);
        //     g2d.setStroke(new BasicStroke(2));
        //     g2d.drawRect(p.getX(), p.getY(), p.getWidth(), p.getHeight());
        // }

        player.draw(g2d);
        
        // Restore graphics state before drawing HUD
        g2d.setTransform(originalTransform);
        
        hud.draw(g2d, WIDTH, HEIGHT);

        g2d.dispose();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        player.handleKeyPress(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        player.handleKeyRelease(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
