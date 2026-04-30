import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File; 
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements KeyListener, Runnable {
    private static final int WIDTH = 1200;
    private static final int HEIGHT = 800;
    private static final int LEVEL_WIDTH = 15000;  // Extended to 5x longer
    private static final int LEVEL_HEIGHT = 800;  // Match viewport height to prevent tiling

    private Player player;
    private HUD hud;
    private Camera camera;
    private Thread gameThread;
    private boolean running = false;
    
    private BufferedImage floorImage;
    private BufferedImage highgroundImage;
    private List<BufferedImage> luckyBlockFrames;
    private List<Floor> floors;
    private List<Platform> platforms;
    private List<HighGround> highgrounds;
    private List<LuckyBlock> luckyBlocks;
    private java.util.Random random;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(135, 206, 235));
        setFocusable(true);
        addKeyListener(this);
        
        floors = new ArrayList<>();
        platforms = new ArrayList<>();
        highgrounds = new ArrayList<>();
        luckyBlocks = new ArrayList<>();
        luckyBlockFrames = new ArrayList<>();
        random = new java.util.Random();
        loadFloorsAndPlatforms();

        player = new Player(50, 620, platforms, highgrounds, luckyBlocks, LEVEL_WIDTH, LEVEL_HEIGHT);
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
        
        // Create a repeating pattern of floor sections across entire map
        int renderWidth = floorImage != null ? floorImage.getWidth() : 100;
        int hitboxWidth = renderWidth;
        int renderHeight = floorImage != null ? floorImage.getHeight() : 50;
        int floorY = 650;
        
        // Fill the entire extended map with floor sections
        int x = 0;
        while (x < LEVEL_WIDTH) {
            addFloorSection(x, floorY, renderWidth, hitboxWidth, renderHeight);
            x += renderWidth + 180; // Floor + gap pattern
        }
        
        System.out.println("Total floor sections created: " + floors.size());
        System.out.println("Total collision platforms created: " + platforms.size());
        
        // Load and create HighGround obstacles
        loadHighGroundObstacles();
        
        // Load and create LuckyBlock obstacles (after highgrounds)
        loadLuckyBlockObstacles();
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
                
                int hgWidth = (int)(highgroundImage.getWidth() * 2.55);
                int hgHeight = (int)(highgroundImage.getHeight() * 2.55);
                
                // Generate highgrounds at random intervals throughout the extended map
                for (int xPos = 600; xPos < LEVEL_WIDTH; xPos += 500 + random.nextInt(400)) {
                    int yPos = 300 + random.nextInt(150); // Random Y in range
                    addHighGround(xPos, yPos, hgWidth, hgHeight);
                }
                
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
    
    private void loadGifFrames(File gifFile, List<BufferedImage> frameList) {
        try {
            ImageInputStream iis = ImageIO.createImageInputStream(gifFile);
            ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
            reader.setInput(iis, false);
            
            int frameCount = reader.getNumImages(true);
            System.out.println("Loading " + gifFile.getName() + " with " + frameCount + " frames");
            
            for (int i = 0; i < frameCount; i++) {
                BufferedImage frame = reader.read(i);
                System.out.println("  Frame " + i + ": " + frame.getWidth() + "x" + frame.getHeight());
                frameList.add(frame);
            }
            reader.dispose();
            iis.close();
        } catch (Exception e) {
            System.err.println("Error loading GIF frames from " + gifFile.getName() + ": " + e.getMessage());
        }
    }
    
    private void loadLuckyBlockObstacles() {
        try {
            File assetsDir = new File("assets");
            if (!assetsDir.exists()) {
                assetsDir = new File("./assets");
            }
            if (!assetsDir.exists()) {
                assetsDir = new File(System.getProperty("user.dir") + "/assets");
            }
            
            File luckyBlockFile = new File(assetsDir, "QuestionBlock.gif");
            if (luckyBlockFile.exists()) {
                loadGifFrames(luckyBlockFile, luckyBlockFrames);
                System.out.println("LuckyBlock animation frames loaded: " + luckyBlockFrames.size() + " frames");
                
                int blockSize = 50;
                
                // Generate lucky blocks at random intervals with ~5% frequency
                for (int xPos = 800; xPos < LEVEL_WIDTH; xPos += 2000 + random.nextInt(1500)) {
                    int yPos = 250 + random.nextInt(150);
                    addLuckyBlockIfNoHighgroundIntersection(xPos, yPos, blockSize, blockSize);
                }
                
                System.out.println("Total LuckyBlock obstacles created: " + luckyBlocks.size());
            } else {
                System.out.println("LuckyBlock image not found at: " + luckyBlockFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Error loading LuckyBlock image: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void addLuckyBlockIfNoHighgroundIntersection(int x, int y, int width, int height) {
        // Check if this lucky block would intersect with any highground's hitbox
        for (HighGround hg : highgrounds) {
            if (checkIntersection(x, y, width, height, hg.getX(), hg.getY(), hg.getWidth(), hg.getHeight())) {
                // Intersection found, skip this lucky block
                return;
            }
        }
        // No intersection, add the lucky block
        luckyBlocks.add(new LuckyBlock(x, y, width, height, luckyBlockFrames));
    }
    
    private boolean checkIntersection(int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2) {
        return x1 < x2 + w2 && x1 + w1 > x2 && y1 < y2 + h2 && y1 + h1 > y2;
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
        for (LuckyBlock lb : luckyBlocks) {
            lb.update();
        }
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
        
        // Draw LuckyBlock obstacles
        for (LuckyBlock lb : luckyBlocks) {
            lb.draw(g2d);
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
        
        // Draw LuckyBlock hitboxes for debugging
        for (LuckyBlock lb : luckyBlocks) {
            g2d.setColor(new Color(255, 215, 0, 100)); // Semi-transparent gold
            g2d.fillRect(lb.getX(), lb.getY(), lb.getWidth(), lb.getHeight());
            g2d.setColor(new Color(255, 215, 0));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(lb.getX(), lb.getY(), lb.getWidth(), lb.getHeight());
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
