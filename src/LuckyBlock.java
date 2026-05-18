import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class LuckyBlock extends Obstacles {
    private final List<BufferedImage> animationFrames;
    private int currentFrameIndex;
    private int frameCount;
    private final int frameDelay = 10;

    public LuckyBlock(int x, int y, int width, int height, List<BufferedImage> frames) {
        super(x, y, width, height, frames.isEmpty() ? null : frames.get(0));
        this.animationFrames = frames;
    }

    public void update() {
        if (animationFrames.isEmpty()) {
            return;
        }
        frameCount++;
        if (frameCount % frameDelay == 0) {
            currentFrameIndex = (currentFrameIndex + 1) % animationFrames.size();
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (!animationFrames.isEmpty()) {
            g.drawImage(animationFrames.get(currentFrameIndex), x, y, width, height, null);
        }
    }

    @Override
    public boolean intersects(int px, int py, int pw, int ph) {
        return px < x + width && px + pw > x && py < y + height && py + ph > y;
    }
}
