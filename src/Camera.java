public class Camera {
    private int x;
    private int y;
    private int width;
    private int height;
    private int levelWidth;
    private int levelHeight;
    
    // Player reference for smooth following
    private Player player;
    private float smoothSpeed = 0.15f; // Lower = smoother camera follow (0-1)

    public Camera(int width, int height, int levelWidth, int levelHeight, Player player) {
        this.width = width;
        this.height = height;
        this.levelWidth = levelWidth;
        this.levelHeight = levelHeight;
        this.player = player;
        this.x = 0;
        this.y = 0;
    }

    public void update() {
        // Calculate target position to keep player centered
        int playerCenterX = player.getX() + player.getWidth() / 2;
        int playerCenterY = player.getY() + player.getHeight() / 2;
        
        // Target camera position (centered on player)
        int targetX = playerCenterX - width / 2;
        int targetY = playerCenterY - height / 2;
        
        // Smooth camera follow
        x = (int) (x + (targetX - x) * smoothSpeed);
        y = (int) (y + (targetY - y) * smoothSpeed);
        
        // Clamp camera to level bounds
        if (x < 0) x = 0;
        if (x + width > levelWidth) x = levelWidth - width;
        
        if (y < 0) y = 0;
        if (y + height > levelHeight) y = levelHeight - height;
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

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
