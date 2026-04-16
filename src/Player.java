import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.util.ArrayList;
import java.util.List;

public class Player {
    private int x;
    private int y;
    private int width = 60;
    private int height = 60;
    private int velocityX = 0;
    private int velocityY = 0;
    private int speed = 5;
    private int jumpPower = -15;
    private int gravity = 1;
    private boolean jumping = false;
    private boolean onGround = false;

    private boolean leftPressed = false;
    private boolean rightPressed = false;

    // Sprite images
    private BufferedImage idleRight;
    private BufferedImage idleLeft;
    private List<BufferedImage> runningRightFrames = new ArrayList<>();
    private List<BufferedImage> runningLeftFrames = new ArrayList<>();
    
    private int currentRunningFrame = 0;

    // Direction tracking
    private enum Direction {
        LEFT, RIGHT
    }
    private Direction facingDirection = Direction.RIGHT;

    // Animation tracking
    private int frameCount = 0;
    private int frameDelay = 5; // Frames per animation frame (lower = faster)

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        loadSprites();
    }

    private void loadSprites() {
        try {
            // Try multiple path options to handle different working directories
            File assetsDir = new File("assets");
            if (!assetsDir.exists()) {
                assetsDir = new File("./assets");
            }
            if (!assetsDir.exists()) {
                // Try from project root when running from IDE
                assetsDir = new File(System.getProperty("user.dir") + "/assets");
            }
            
            idleRight = ImageIO.read(new File(assetsDir, "Right.png"));
            idleLeft = ImageIO.read(new File(assetsDir, "Left.png"));
            
            // Load GIF animations with all frames
            loadGifFrames(new File(assetsDir, "Running Right.gif"), runningRightFrames);
            loadGifFrames(new File(assetsDir, "Running Left.gif"), runningLeftFrames);
            
            if (idleRight != null) {
                System.out.println("Sprites loaded successfully from: " + assetsDir.getAbsolutePath());
                System.out.println("Idle Right: " + (idleRight != null));
                System.out.println("Idle Left: " + (idleLeft != null));
                System.out.println("Running Right frames: " + runningRightFrames.size());
                System.out.println("Running Left frames: " + runningLeftFrames.size());
            }
        } catch (Exception e) {
            System.err.println("Error loading sprites: " + e.getMessage());
            e.printStackTrace();
        }
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
                
                // Scale all frames to a consistent size (155x155 like idle sprites)
                BufferedImage scaledFrame = new BufferedImage(155, 155, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = scaledFrame.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2d.drawImage(frame, 0, 0, 155, 155, null);
                g2d.dispose();
                frameList.add(scaledFrame);
            }
            reader.dispose();
            iis.close();
        } catch (Exception e) {
            System.err.println("Error loading GIF frames from " + gifFile.getName() + ": " + e.getMessage());
        }
    }

    public void update() {
        if (leftPressed) {
            velocityX = -speed;
            facingDirection = Direction.LEFT;
        } else if (rightPressed) {
            velocityX = speed;
            facingDirection = Direction.RIGHT;
        } else {
            velocityX = 0;
        }

        // Update animation frame counter for running animations
        frameCount++;
        if (frameCount > 255) {
            frameCount = 0; // Reset to keep it manageable
        }
        
        // Advance to next frame every frameDelay updates
        if (frameCount % frameDelay == 0) {
            currentRunningFrame++;
        }

        x += velocityX;

        if (!onGround) {
            velocityY += gravity;
        }

        y += velocityY;

        if (y >= 700) {
            y = 700;
            velocityY = 0;
            onGround = true;
            jumping = false;
        } else {
            onGround = false;
        }

        if (x < 0) x = 0;
        if (x + width > 1200) x = 1200 - width;
    }

    public void draw(Graphics2D g) {
        BufferedImage currentSprite = null;

        // Choose sprite based on direction and movement
        if (velocityX != 0) {
            // Running - cycle through animation frames
            if (facingDirection == Direction.RIGHT) {
                if (!runningRightFrames.isEmpty()) {
                    int frameIndex = currentRunningFrame % runningRightFrames.size();
                    currentSprite = runningRightFrames.get(frameIndex);
                }
            } else {
                if (!runningLeftFrames.isEmpty()) {
                    int frameIndex = currentRunningFrame % runningLeftFrames.size();
                    currentSprite = runningLeftFrames.get(frameIndex);
                }
            }
        } else {
            // Idle
            if (facingDirection == Direction.RIGHT) {
                currentSprite = idleRight;
            } else {
                currentSprite = idleLeft;
            }
        }

        // Draw sprite if loaded, otherwise draw placeholder
        if (currentSprite != null) {
            g.drawImage(currentSprite, x, y, width, height, null);
        } else {
            g.setColor(Color.RED);
            g.fillRect(x, y, width, height);
        }
    }

    public void handleKeyPress(int keyCode) {
        if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) {
            leftPressed = true;
        }
        if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) {
            rightPressed = true;
        }
        if ((keyCode == KeyEvent.VK_SPACE || keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP) && onGround) {
            velocityY = jumpPower;
            jumping = true;
            onGround = false;
        }
    }

    public void handleKeyRelease(int keyCode) {
        if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) {
            leftPressed = false;
        }
        if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) {
            rightPressed = false;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
