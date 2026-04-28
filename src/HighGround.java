import java.awt.image.BufferedImage;

public class HighGround extends Obstacles {
    // Only top 1/5th of the image has a hitbox
    private static final double HITBOX_HEIGHT_RATIO = 0.2; // Top 1/5th
    
    public HighGround(int x, int y, int width, int height, BufferedImage image) {
        super(x, y, width, height, image);
    }

    /**
     * Check intersection with velocity awareness.
     * When player is jumping (velocityY < 0), the hitbox is solid (collides).
     * Otherwise (falling or stationary), the hitbox is permeable.
     */
    public boolean intersectsWithVelocity(int px, int py, int pw, int ph, double velocityY) {
        // If player is not jumping (velocity >= 0), this obstacle is permeable
        if (velocityY < 0) {
            return false;
        }
        
        // Only check collision with the top 1/5th of the obstacle
        int hitboxHeight = (int) (height * HITBOX_HEIGHT_RATIO);
        int hitboxY = y; // Hitbox is at the very top
        
        return px < x + width &&
               px + pw > x &&
               py < hitboxY + hitboxHeight &&
               py + ph > hitboxY;
    }
    
    /**
     * Standard intersects method for compatibility (only checks top 1/5th).
     */
    @Override
    public boolean intersects(int px, int py, int pw, int ph) {
        // Only check collision with the top 1/5th of the obstacle
        int hitboxHeight = (int) (height * HITBOX_HEIGHT_RATIO);
        int hitboxY = y; // Hitbox is at the very top
        
        return px < x + width &&
               px + pw > x &&
               py < hitboxY + hitboxHeight &&
               py + ph > hitboxY;
    }
    
    public int getHitboxHeight() {
        return (int) (height * HITBOX_HEIGHT_RATIO);
    }
}
