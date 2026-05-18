public class Camera {
    private int x;
    private int y;
    private final int screenW;
    private final int screenH;
    private final int levelW;
    private final int levelH;

    public Camera(int screenW, int screenH, int levelW, int levelH) {
        this.screenW = screenW;
        this.screenH = screenH;
        this.levelW = levelW;
        this.levelH = levelH;
    }

    public void update(Player player) {
        int targetX = player.getX() + player.getWidth() / 2 - screenW / 2;
        // SMW camera leads slightly when moving right
        if (player.isFacingRight()) {
            targetX += 32;
        }
        x += (targetX - x) * 0.12;
        if (x < 0) {
            x = 0;
        }
        if (x + screenW > levelW) {
            x = levelW - screenW;
        }

        // mostly fixed vertical (show ground area)
        y = Math.max(0, levelH - screenH);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
