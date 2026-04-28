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
    private static final int LEVEL_WIDTH = 3000;  // Single horizontal level
    private static final int LEVEL_HEIGHT = 800;  // Match viewport height to prevent tiling

    private Player player;
    private HUD hud;
    private Camera camera;
    private Thread gameThread;
    private boolean running = false;
    
    private BufferedImage floorImage;
    private BufferedImage highgroundImage;
    private List<Floor> floors;
    private List<Platform> platforms;
    private List<HighGround> highgrounds;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(135, 206, 235));
        setFocusable(true);
        addKeyListener(this);
        
        floors = new ArrayList<>();
        platforms = new ArrayList<>();
        highgrounds = new ArrayList<>();
        loadFloorsAndPlatforms();

        player = new Player(50, 620, platforms, highgrounds, LEVEL_WIDTH, LEVEL_HEIGHT);
        camera = new Camera(WIDTH, HEIGHT, LEVEL_WIDTH, LEVEL_HEIGHT, player);
        hud = new HUD();
    }
    
    private void loadFloorsAndPlatforms() {
        // Load floor image
        try {
            File assetsDir = new File("assets");
            if (!assetsDir.exists()) {
                assetsDir = new File("./assets");
            }
            if (!assetsDir.exists()) {
                assetsDir = new File(System.getProperty("user.dir") + "/assets");
            }
            
            File floorFile = new File(assetsDir, "floor.png");
            if (floorFile.exists()) {
                floorImage = ImageIO.read(floorFile);
                System.out.println("Floor image loaded: " + floorFile.getAbsolutePath());
                System.out.println("Floor image size: " + floorImage.getWidth() + "x" + floorImage.getHeight());
            } else {
                System.err.println("Floor image not found at: " + floorFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Error loading floor image: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Create a line of floor sections with gaps in between
        // Floor dimensions - render width is the full image, but hitbox width is smaller to create gaps
        int renderWidth = floorImage != null ? floorImage.getWidth() : 100;
        int hitboxWidth = (int)(renderWidth); // Reduce hitbox to 70% of render width
        int renderHeight = floorImage != null ? floorImage.getHeight() : 50;
        int floorY = 650; // Floor Y position
        
        // Section 1: Tutorial area with gaps
        addFloorSection(0, floorY, renderWidth, hitboxWidth, renderHeight);
        addFloorSection(renderWidth + 180, floorY, renderWidth, hitboxWidth, renderHeight); // Gap of 180px
        addFloorSection((renderWidth + 180) * 2 + 70, floorY, renderWidth, hitboxWidth, renderHeight); // Larger gap
        
        // Section 2: Mid-level with varied gaps
        addFloorSection((renderWidth + 180) * 3 + 240, floorY, renderWidth, hitboxWidth, renderHeight); // Even bigger gap
        addFloorSection((renderWidth + 180) * 4, floorY, renderWidth, hitboxWidth, renderHeight); // Normal gap
        addFloorSection((renderWidth + 180) * 5 + 320, floorY, renderWidth, hitboxWidth, renderHeight); // Very large gap
        
        // Section 3: Final sections
        addFloorSection((renderWidth + 180) * 6 + 140, floorY, renderWidth, hitboxWidth, renderHeight);
        addFloorSection((renderWidth + 180) * 7 + 260, floorY, renderWidth, hitboxWidth, renderHeight);
        
        System.out.println("Total floor sections created: " + floors.size());
        System.out.println("Total collision platforms created: " + platforms.size());
        
        // Load and create HighGround obstacles
        loadHighGroundObstacles();
    }
    
    private void loadHighGroundObstacles() {
        try {
            File assetsDir = new File("assets");
            if (!assetsDir.exists()) {
                assetsDir = new File("./assets");
            }
            if (!assetsDir.exists()) {
                assetsDir = new File(System.getProperty("user.dir") + "/assets");
            }
            
            File highgroundFile = new File(assetsDir, "highground.png");
            if (highgroundFile.exists()) {
                highgroundImage = ImageIO.read(highgroundFile);
                System.out.println("HighGround image loaded: " + highgroundFile.getAbsolutePath());
                System.out.println("HighGround image size: " + highgroundImage.getWidth() + "x" + highgroundImage.getHeight());
                
                // Create HighGround obstacles at various positions (moved down by 50)
                addHighGround(600, 370, (int)(highgroundImage.getWidth() * 2.55), (int)(highgroundImage.getHeight() * 2.55));
                addHighGround(1200, 370, (int)(highgroundImage.getWidth() * 2.55), (int)(highgroundImage.getHeight() * 2.55));
                addHighGround(1800, 370, (int)(highgroundImage.getWidth() * 2.55), (int)(highgroundImage.getHeight() * 2.55));
                addHighGround(2400, 370, (int)(highgroundImage.getWidth() * 2.55), (int)(highgroundImage.getHeight() * 2.55));
                
                System.out.println("Total HighGround obstacles created: " + highgrounds.size());
            } else {
                System.out.println("HighGround image not found at: " + highgroundFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Error loading HighGround image: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void addHighGround(int x, int y, int width, int height) {
        highgrounds.add(new HighGround(x, y, width, height, highgroundImage));
    }
    
    private void addFloorSection(int x, int y, int renderWidth, int hitboxWidth, int height) {
        floors.add(new Floor(x, y, renderWidth, height, floorImage));
        platforms.add(new Platform(x, y, hitboxWidth, height));
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
        
        // Draw floor objects
        for (Floor floor : floors) {
            floor.draw(g2d);
        }
        
        // Draw HighGround obstacles
        for (HighGround hg : highgrounds) {
            hg.draw(g2d);
        }
        
        // Draw floor hitboxes for debugging
        for (Platform p : platforms) {
            g2d.setColor(new Color(0, 255, 0, 100)); // Semi-transparent green
            g2d.fillRect(p.getX(), p.getY(), p.getWidth(), p.getHeight());
            g2d.setColor(Color.GREEN);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(p.getX(), p.getY(), p.getWidth(), p.getHeight());
        }
        
        // Draw HighGround hitboxes for debugging
        for (HighGround hg : highgrounds) {
            int hitboxHeight = hg.getHitboxHeight();
            g2d.setColor(new Color(255, 165, 0, 100)); // Semi-transparent orange
            g2d.fillRect(hg.getX(), hg.getY(), hg.getWidth(), hitboxHeight);
            g2d.setColor(new Color(255, 165, 0));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(hg.getX(), hg.getY(), hg.getWidth(), hitboxHeight);
        }

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
