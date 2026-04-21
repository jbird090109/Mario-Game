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
    private double velocityX = 0;
    private double velocityY = 0;
    private double maxSpeed = 12.0;
    private double acceleration = 1.5;
    private double friction = 0.85; // Slide/deceleration factor (0-1)
    private int jumpPower = -15;
    private double gravityAscent = 1.50; // Gravity while ascending
    private double gravityDescent = 1.8; // Gravity while descending - faster fall
    private boolean jumping = false;
    private boolean onGround = false;

    private boolean leftPressed = false;
    private boolean rightPressed = false;

    // Sprite images
    private BufferedImage idleRight;
    private BufferedImage idleLeft;
    private List<BufferedImage> runningRightFrames = new ArrayList<>();
    private List<BufferedImage> runningLeftFrames = new ArrayList<>();
    
    // Jump sprites
    private BufferedImage jumpUpRight;
    private BufferedImage jumpUpLeft;
    private BufferedImage jumpDownRight;
    private BufferedImage jumpDownLeft;
    
    private int currentRunningFrame = 0;
    
    // Jump variables (Super Mario World style)
    private boolean jumpKeyPressed = false;
    private int jumpKeyHeldFrames = 0;
    private int maxJumpKeyFrames = 14; // Duration to hold button for max height
    private double jumpForceAccumulator = 0;
    private int hangTimeCounter = 0; // For apex hang time (SMW-like feel)
    private int hangTimeFrames = 2; // Frames of reduced gravity at apex

    // Direction tracking
    private enum Direction {
        LEFT, RIGHT
    }
    private Direction facingDirection = Direction.RIGHT;

    // Animation tracking
    private int frameCount = 0;
    private int frameDelay = 2; // Frames per animation frame (lower = faster)

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
            
            // Load jump sprites
            jumpUpRight = ImageIO.read(new File(assetsDir, "jumpingupright.png"));
            jumpUpLeft = ImageIO.read(new File(assetsDir, "jumpingupleft.png"));
            jumpDownRight = ImageIO.read(new File(assetsDir, "jumpingrightdown.png"));
            jumpDownLeft = ImageIO.read(new File(assetsDir, "jumpingleftdown.png"));
            
            if (idleRight != null) {
                System.out.println("Sprites loaded successfully from: " + assetsDir.getAbsolutePath());
                System.out.println("Idle Right: " + (idleRight != null));
                System.out.println("Idle Left: " + (idleLeft != null));
                System.out.println("Running Right frames: " + runningRightFrames.size());
                System.out.println("Running Left frames: " + runningLeftFrames.size());
                System.out.println("Jump Up Right: " + (jumpUpRight != null));
                System.out.println("Jump Up Left: " + (jumpUpLeft != null));
                System.out.println("Jump Down Right: " + (jumpDownRight != null));
                System.out.println("Jump Down Left: " + (jumpDownLeft != null));
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
        // Momentum-based movement with acceleration and friction
        // Reduce control when in the air (air strafing is slower)
        double currentAcceleration = onGround ? acceleration : acceleration * 0.5;
        double currentMaxSpeed = onGround ? maxSpeed : maxSpeed * 0.9;
        
        if (leftPressed) {
            velocityX -= currentAcceleration;
            if (velocityX < -currentMaxSpeed) velocityX = -currentMaxSpeed;
            facingDirection = Direction.LEFT;
        } else if (rightPressed) {
            velocityX += currentAcceleration;
            if (velocityX > currentMaxSpeed) velocityX = currentMaxSpeed;
            facingDirection = Direction.RIGHT;
        } else {
            // Apply friction when no key is pressed (sliding effect)
            velocityX *= friction;
            // Stop completely if very slow
            if (Math.abs(velocityX) < 0.1) {
                velocityX = 0;
            }
        }

        // Track max jump hold time (no longer needed for accumulation, just for feedback)
        if (jumpKeyPressed && jumpKeyHeldFrames < maxJumpKeyFrames) {
            jumpKeyHeldFrames++;
        }

        // Apply variable gravity based on jump phase (classic Mario physics)
        if (!onGround) {
            if (jumpKeyPressed && velocityY < 0) {
                // While holding jump button AND ascending - reduced gravity for variable height
                // This allows jumping higher by holding, and lower if released early
                velocityY += gravityAscent * 0.5; // Reduced gravity extends hang time
            } else if (velocityY < 0) {
                // Ascending without holding jump button - normal ascent gravity
                velocityY += gravityAscent;
            } else {
                // Descending - normal gravity
                velocityY += gravityDescent;
            }
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

        x += (int) velocityX;

        y += velocityY;

        if (y >= 700) {
            y = 700;
            velocityY = 0;
            onGround = true;
            jumping = false;
            jumpKeyPressed = false;
            jumpKeyHeldFrames = 0;
            jumpForceAccumulator = 0;
            hangTimeCounter = 0;
        } else {
            onGround = false;
        }

        if (x < 0) x = 0;
        if (x + width > 1200) x = 1200 - width;
    }

    public void draw(Graphics2D g) {
        BufferedImage currentSprite = null;
        int drawWidth = width;
        int drawHeight = height;

        // Check if currently jumping
        if (jumping || (velocityY != 0 && !onGround)) {
            // Choose jump sprite based on direction and velocity
            drawWidth = 45; // Smaller width for jump sprites
            drawHeight = 60;
            if (velocityY < 0) {
                // Going up
                if (facingDirection == Direction.RIGHT) {
                    currentSprite = jumpUpRight;
                } else {
                    currentSprite = jumpUpLeft;
                }
            } else {
                // Coming down
                if (facingDirection == Direction.RIGHT) {
                    currentSprite = jumpDownRight;
                } else {
                    currentSprite = jumpDownLeft;
                }
            }
        } else if (Math.abs(velocityX) > 1.0) {
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
            g.drawImage(currentSprite, x, y, drawWidth, drawHeight, null);
        } else {
            g.setColor(Color.RED);
            g.fillRect(x, y, drawWidth, drawHeight);
        }
    }

    public void handleKeyPress(int keyCode) {
        if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) {
            leftPressed = true;
        }
        if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) {
            rightPressed = true;
        }
        if ((keyCode == KeyEvent.VK_SPACE || keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP) && onGround && !jumpKeyPressed) {
            // Apply instant jump burst for that classic Mario leap feel
            jumpKeyPressed = true;
            jumpKeyHeldFrames = 0;
            velocityY = -22; // Strong initial burst velocity - higher jump!
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
        if (keyCode == KeyEvent.VK_SPACE || keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP) {
            // Release jump key - gravity takes over naturally
            // By releasing the key, you lose the reduced gravity bonus and fall faster
            if (jumpKeyPressed) {
                jumpKeyPressed = false;
                // Let normal gravity handle the descent - no need to set velocity
            }
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
