import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/** Procedural Super Mario World style sprites and tiles. */
public final class SmwAssets {
    private SmwAssets() {}

    public static BufferedImage skyGradient;
    public static BufferedImage[] hillsFar;
    public static BufferedImage[] hillsNear;
    public static BufferedImage bush;
    public static BufferedImage cloud;

    public static BufferedImage tileGround;
    public static BufferedImage tileBlock;
    public static BufferedImage tileBrick;
    public static BufferedImage tileQuestion;
    public static BufferedImage tileQuestionHit;
    public static BufferedImage tileUsed;
    public static BufferedImage tilePipeTopL;
    public static BufferedImage tilePipeTopR;
    public static BufferedImage tilePipeBody;
    public static BufferedImage tileSemisolid;
    public static BufferedImage tileGoal;

    public static BufferedImage[] marioIdleR;
    public static BufferedImage[] marioRunR;
    public static BufferedImage[] marioJumpR;
    public static BufferedImage[] marioSkidR;
    public static BufferedImage marioDead;

    public static BufferedImage[] goombaWalk;
    public static BufferedImage[] koopaWalk;
    public static BufferedImage koopaShell;
    public static BufferedImage coinSpin;
    public static BufferedImage mushroom;
    public static BufferedImage flower;
    public static BufferedImage star;

    public static void init() {
        skyGradient = makeSky();
        hillsFar = new BufferedImage[]{makeHill(80, new Color(0, 168, 0)), makeHill(60, new Color(0, 148, 0))};
        hillsNear = new BufferedImage[]{makeHill(50, new Color(0, 200, 0)), makeHill(40, new Color(0, 180, 0))};
        bush = makeBush();
        cloud = makeCloud();

        tileGround = makeGroundTile();
        tileBlock = makeBlockTile(new Color(200, 120, 60), new Color(140, 70, 30));
        tileBrick = makeBrickTile();
        tileQuestion = makeQuestionTile(false);
        tileQuestionHit = makeQuestionTile(true);
        tileUsed = makeUsedBlockTile();
        tilePipeTopL = makePipeTop(true);
        tilePipeTopR = makePipeTop(false);
        tilePipeBody = makePipeBody();
        tileSemisolid = makeSemisolidTile();
        tileGoal = makeGoalTile();

        marioIdleR = new BufferedImage[]{makeMarioFrame(0, false)};
        marioRunR = new BufferedImage[]{
            makeMarioFrame(1, false), makeMarioFrame(2, false),
            makeMarioFrame(3, false), makeMarioFrame(2, false)
        };
        marioJumpR = new BufferedImage[]{makeMarioFrame(4, false)};
        marioSkidR = new BufferedImage[]{makeMarioFrame(5, false)};
        marioDead = makeMarioDead();

        goombaWalk = new BufferedImage[]{makeGoomba(0), makeGoomba(1)};
        koopaWalk = new BufferedImage[]{makeKoopa(0, false), makeKoopa(1, false)};
        koopaShell = makeKoopa(0, true);
        coinSpin = makeCoin();
        mushroom = makeMushroom();
        flower = makeFlower();
        star = makeStar();
    }

    public static BufferedImage flip(BufferedImage src) {
        BufferedImage out = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.drawImage(src, src.getWidth(), 0, -src.getWidth(), src.getHeight(), null);
        g.dispose();
        return out;
    }

    public static List<BufferedImage> mirrorFrames(BufferedImage[] frames) {
        List<BufferedImage> list = new ArrayList<>();
        for (BufferedImage f : frames) {
            list.add(flip(f));
        }
        return list;
    }

    private static BufferedImage img(int w, int h) {
        return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    }

    private static void px(Graphics2D g, int x, int y, int w, int h, Color c) {
        g.setColor(c);
        g.fillRect(x, y, w, h);
    }

    private static BufferedImage makeSky() {
        BufferedImage img = img(256, 224);
        Graphics2D g = img.createGraphics();
        GradientPaint gp = new GradientPaint(0, 0, new Color(92, 148, 252), 0, 224, new Color(60, 120, 240));
        g.setPaint(gp);
        g.fillRect(0, 0, 256, 224);
        g.dispose();
        return img;
    }

    private static BufferedImage makeHill(int size, Color c) {
        BufferedImage img = img(size * 2, size);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(c);
        g.fillOval(0, size / 3, size * 2, size);
        g.setColor(c.darker());
        g.fillOval(size / 4, size / 2, size, size / 2);
        g.dispose();
        return img;
    }

    private static BufferedImage makeBush() {
        BufferedImage img = img(48, 24);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(0, 168, 0));
        g.fillOval(0, 8, 20, 16);
        g.fillOval(14, 4, 20, 20);
        g.fillOval(28, 8, 20, 16);
        g.dispose();
        return img;
    }

    private static BufferedImage makeCloud() {
        BufferedImage img = img(64, 32);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.WHITE);
        g.fillOval(8, 12, 24, 18);
        g.fillOval(24, 8, 28, 22);
        g.fillOval(40, 12, 20, 16);
        g.dispose();
        return img;
    }

    private static BufferedImage makeGroundTile() {
        BufferedImage img = img(16, 16);
        Graphics2D g = img.createGraphics();
        px(g, 0, 0, 16, 16, new Color(192, 112, 56));
        px(g, 0, 0, 16, 4, new Color(0, 168, 0));
        for (int y = 4; y < 16; y += 4) {
            for (int x = 0; x < 16; x += 4) {
                px(g, x, y, 3, 3, new Color(160, 90, 40));
            }
        }
        g.dispose();
        return img;
    }

    private static BufferedImage makeBlockTile(Color top, Color side) {
        BufferedImage img = img(16, 16);
        Graphics2D g = img.createGraphics();
        px(g, 0, 0, 16, 16, side);
        px(g, 1, 1, 14, 14, top);
        px(g, 2, 2, 12, 2, top.brighter());
        g.dispose();
        return img;
    }

    private static BufferedImage makeBrickTile() {
        BufferedImage img = img(16, 16);
        Graphics2D g = img.createGraphics();
        px(g, 0, 0, 16, 16, new Color(180, 80, 40));
        g.setColor(new Color(120, 50, 20));
        g.drawLine(0, 7, 16, 7);
        g.drawLine(8, 0, 8, 7);
        g.drawLine(4, 8, 4, 16);
        g.drawLine(12, 8, 12, 16);
        g.dispose();
        return img;
    }

    private static BufferedImage makeQuestionTile(boolean hit) {
        BufferedImage img = img(16, 16);
        Graphics2D g = img.createGraphics();
        Color gold = hit ? new Color(200, 160, 60) : new Color(255, 200, 0);
        px(g, 0, 0, 16, 16, new Color(180, 100, 0));
        px(g, 1, 1, 14, 14, gold);
        g.setColor(new Color(80, 40, 0));
        g.setFont(new Font("Monospaced", Font.BOLD, 10));
        g.drawString("?", 5, 12);
        g.dispose();
        return img;
    }

    private static BufferedImage makeUsedBlockTile() {
        BufferedImage img = img(16, 16);
        Graphics2D g = img.createGraphics();
        px(g, 0, 0, 16, 16, new Color(140, 90, 50));
        px(g, 2, 2, 12, 12, new Color(100, 60, 30));
        g.dispose();
        return img;
    }

    private static BufferedImage makePipeTop(boolean left) {
        BufferedImage img = img(16, 16);
        Graphics2D g = img.createGraphics();
        px(g, 0, 0, 16, 16, new Color(0, 140, 0));
        px(g, 0, 0, 16, 6, new Color(0, 200, 0));
        if (left) {
            px(g, 0, 6, 4, 10, new Color(0, 160, 0));
        } else {
            px(g, 12, 6, 4, 10, new Color(0, 160, 0));
        }
        g.dispose();
        return img;
    }

    private static BufferedImage makePipeBody() {
        BufferedImage img = img(16, 16);
        Graphics2D g = img.createGraphics();
        px(g, 0, 0, 16, 16, new Color(0, 140, 0));
        px(g, 2, 0, 12, 16, new Color(0, 200, 0));
        g.dispose();
        return img;
    }

    private static BufferedImage makeSemisolidTile() {
        BufferedImage img = img(16, 16);
        Graphics2D g = img.createGraphics();
        px(g, 0, 12, 16, 4, new Color(0, 168, 0));
        for (int x = 0; x < 16; x += 4) {
            px(g, x, 12, 3, 4, new Color(0, 200, 80));
        }
        g.dispose();
        return img;
    }

    private static BufferedImage makeGoalTile() {
        BufferedImage img = img(16, 16);
        Graphics2D g = img.createGraphics();
        px(g, 6, 0, 4, 16, new Color(80, 80, 80));
        px(g, 4, 2, 8, 4, new Color(255, 0, 0));
        g.dispose();
        return img;
    }

    private static BufferedImage makeMarioFrame(int pose, boolean big) {
        int h = big ? 28 : 16;
        BufferedImage img = img(16, h);
        Graphics2D g = img.createGraphics();
        Color hat = new Color(220, 0, 0);
        Color skin = new Color(255, 200, 150);
        Color overall = new Color(0, 0, 220);
        Color shirt = new Color(220, 0, 0);
        int yOff = big ? 12 : 0;

        px(g, 4, yOff, 8, 5, hat);
        px(g, 5, yOff + 5, 6, 4, skin);
        px(g, 3, yOff + 9, 10, 4, shirt);

        int legY = yOff + 13;
        if (pose == 1) {
            px(g, 2, legY, 4, 3, overall);
            px(g, 10, legY, 4, 3, overall);
        } else if (pose == 2 || pose == 3) {
            px(g, 3, legY, 4, 3, overall);
            px(g, 9, legY - (pose == 2 ? 1 : 0), 4, 3, overall);
        } else if (pose == 4) {
            px(g, 4, yOff + 8, 8, 5, overall);
            px(g, 2, legY, 5, 3, overall);
        } else if (pose == 5) {
            px(g, 6, legY, 6, 3, overall);
            px(g, 1, yOff + 10, 4, 3, overall);
        } else {
            px(g, 4, legY, 3, 3, overall);
            px(g, 9, legY, 3, 3, overall);
        }
        g.dispose();
        return img;
    }

    private static BufferedImage makeMarioDead() {
        BufferedImage img = img(16, 16);
        Graphics2D g = img.createGraphics();
        px(g, 2, 6, 12, 6, new Color(220, 0, 0));
        g.dispose();
        return img;
    }

    private static BufferedImage makeGoomba(int frame) {
        BufferedImage img = img(16, 16);
        Graphics2D g = img.createGraphics();
        px(g, 2, 4, 12, 8, new Color(160, 80, 40));
        px(g, 4, 2, 8, 4, new Color(160, 80, 40));
        px(g, 5, 6, 2, 2, Color.WHITE);
        px(g, 9, 6, 2, 2, Color.WHITE);
        px(g, 5, 7, 2, 2, Color.BLACK);
        px(g, 9, 7, 2, 2, Color.BLACK);
        int foot = frame == 0 ? 3 : 5;
        px(g, 3, 12, 4, 3, new Color(80, 40, 20));
        px(g, 9, 12, 4, 3, new Color(80, 40, 20));
        px(g, foot, 11, 3, 2, new Color(80, 40, 20));
        g.dispose();
        return img;
    }

    private static BufferedImage makeKoopa(int frame, boolean shell) {
        BufferedImage img = img(16, 16);
        Graphics2D g = img.createGraphics();
        if (shell) {
            px(g, 2, 6, 12, 10, new Color(0, 180, 0));
            px(g, 4, 4, 8, 4, new Color(0, 140, 0));
        } else {
            px(g, 4, 2, 8, 6, new Color(0, 200, 0));
            px(g, 3, 8, 10, 6, new Color(0, 160, 0));
            px(g, 4 + frame, 12, 3, 3, new Color(255, 200, 150));
            px(g, 9 - frame, 12, 3, 3, new Color(255, 200, 150));
        }
        g.dispose();
        return img;
    }

    private static BufferedImage makeCoin() {
        BufferedImage img = img(12, 16);
        Graphics2D g = img.createGraphics();
        px(g, 3, 2, 6, 12, new Color(255, 220, 0));
        px(g, 5, 2, 2, 12, new Color(255, 180, 0));
        g.dispose();
        return img;
    }

    private static BufferedImage makeMushroom() {
        BufferedImage img = img(16, 16);
        Graphics2D g = img.createGraphics();
        px(g, 2, 2, 12, 8, new Color(220, 0, 0));
        px(g, 2, 10, 12, 6, new Color(255, 200, 150));
        px(g, 4, 4, 3, 3, Color.WHITE);
        px(g, 9, 4, 3, 3, Color.WHITE);
        g.dispose();
        return img;
    }

    private static BufferedImage makeFlower() {
        BufferedImage img = img(16, 16);
        Graphics2D g = img.createGraphics();
        px(g, 6, 10, 4, 6, new Color(0, 160, 0));
        px(g, 2, 4, 5, 5, new Color(255, 0, 0));
        px(g, 9, 4, 5, 5, new Color(255, 0, 0));
        px(g, 5, 2, 6, 5, new Color(255, 0, 0));
        px(g, 6, 6, 4, 4, new Color(255, 220, 0));
        g.dispose();
        return img;
    }

    private static BufferedImage makeStar() {
        BufferedImage img = img(16, 16);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(255, 220, 0));
        int[] x = {8, 10, 14, 11, 12, 8, 4, 5, 2, 6};
        int[] y = {2, 6, 6, 9, 14, 11, 14, 9, 6, 6};
        g.fillPolygon(x, y, 10);
        g.dispose();
        return img;
    }

    public static BufferedImage tileImage(TileType type, int sub) {
        switch (type) {
            case GROUND: return tileGround;
            case BLOCK: return tileBlock;
            case BRICK: return tileBrick;
            case QUESTION: return sub > 0 ? tileQuestionHit : tileQuestion;
            case USED: return tileUsed;
            case PIPE_TOP: return sub == 0 ? tilePipeTopL : tilePipeTopR;
            case PIPE_BODY: return tilePipeBody;
            case SEMI_SOLID: return tileSemisolid;
            case GOAL: return tileGoal;
            default: return null;
        }
    }
}
