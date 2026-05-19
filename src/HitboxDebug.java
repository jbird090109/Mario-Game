import java.awt.*;

/** Toggleable debug overlays for collision boxes (press H in-game). */
public final class HitboxDebug {
    public static boolean enabled = true;

    private HitboxDebug() {}

    public static void drawRect(Graphics2D g, int x, int y, int w, int h, Color fill, Color stroke) {
        if (w <= 0 || h <= 0) {
            return;
        }
        g.setColor(new Color(fill.getRed(), fill.getGreen(), fill.getBlue(), 90));
        g.fillRect(x, y, w, h);
        g.setColor(stroke);
        g.setStroke(new BasicStroke(1.5f));
        g.drawRect(x, y, w, h);
    }

    public static void drawLabel(Graphics2D g, int x, int y, String text, Color c) {
        g.setFont(new Font("Arial", Font.BOLD, 8));
        g.setColor(c);
        g.drawString(text, x, y - 2);
    }
}
