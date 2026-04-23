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
import java.awt.RenderingHints;

public class GamePanel extends JPanel implements KeyListener, Runnable {
    private static final int WIDTH = 1200;
    private static final int HEIGHT = 800;
    private static final int LEVEL_WIDTH = 3000;  // Single horizontal level
    private static final int LEVEL_HEIGHT = 800;  // Match viewport height to prevent tiling

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

        player = new Player(50, 620, platforms, LEVEL_WIDTH, LEVEL_HEIGHT);
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
        
        // Create a clean, single-level platformer with proper spacing
        // Ground base throughout the level
        platforms.add(new Platform(0, 700, 3000, 100)); // Main ground
        
        // Section 1: Tutorial area (0-300px) - Easy platforming
        platforms.add(new Platform(150, 600, 150, 50));  // First platform
        platforms.add(new Platform(350, 550, 150, 50));  // Second platform (higher)
        
        // Section 2: Ascending challenge (300-600px)
        platforms.add(new Platform(450, 600, 120, 50));
        platforms.add(new Platform(600, 550, 120, 50));
        platforms.add(new Platform(750, 500, 120, 50));
        
        // Section 3: Peak and descent (600-900px)
        platforms.add(new Platform(900, 450, 100, 50));  // Peak platform
        platforms.add(new Platform(1050, 500, 120, 50));
        platforms.add(new Platform(1200, 550, 120, 50));
        
        // Section 4: Horizontal challenge (900-1200px) - Side-by-side platforms
        platforms.add(new Platform(1350, 600, 100, 50));
        platforms.add(new Platform(1500, 600, 100, 50));
        platforms.add(new Platform(1650, 600, 100, 50));
        
        // Section 5: Staircase up (1200-1500px)
        platforms.add(new Platform(1800, 550, 120, 50));
        platforms.add(new Platform(1950, 500, 120, 50));
        platforms.add(new Platform(2100, 450, 120, 50));
        
        // Section 6: Gap challenge (1500-1800px) - Wider spacing
        platforms.add(new Platform(2250, 500, 100, 50));
        platforms.add(new Platform(2400, 550, 100, 50));
        
        // Section 7: Final climb (1800-2200px)
        platforms.add(new Platform(2550, 600, 120, 50));
        platforms.add(new Platform(2700, 550, 120, 50));
        
        // Section 8: Home stretch (2200-3000px)
        platforms.add(new Platform(2850, 600, 150, 50));
        
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
        
        // Draw background image tiled horizontally only
        if (backgroundImage != null) {
            int bgWidth = backgroundImage.getWidth();
            
            // Calculate which tiles to draw based on camera position
            int startX = (camera.getX() / bgWidth) * bgWidth;
            int endX = startX + WIDTH + bgWidth;
            
            for (int x = startX; x < endX; x += bgWidth) {
                // Draw background stretched to fit level height, positioned at top
                g2d.drawImage(backgroundImage, x, 0, bgWidth, LEVEL_HEIGHT, null);
            }
        } else {
            // Fallback: draw solid background
            g2d.setColor(new Color(135, 206, 235));
            g2d.fillRect(0, 0, LEVEL_WIDTH, LEVEL_HEIGHT);
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
