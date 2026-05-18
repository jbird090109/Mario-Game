import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Level {
    public final int width;
    public final int height;
    public final TileType[][] tiles;
    public final int[][] sub;

    public final List<Enemy> enemies = new ArrayList<>();
    public final List<Coin> coins = new ArrayList<>();
    public final List<PowerUp> powerUps = new ArrayList<>();

    private final int goalTileX;

    public Level(String[] rows) {
        height = rows.length;
        int maxW = 0;
        for (String row : rows) {
            maxW = Math.max(maxW, row.length());
        }
        width = maxW;
        tiles = new TileType[height][width];
        sub = new int[height][width];
        goalTileX = parseRows(rows);
        spawnEntities();
    }

    private int parseRows(String[] rows) {
        int goalX = width - 3;
        for (int y = 0; y < height; y++) {
            String row = rows[y];
            for (int x = 0; x < width; x++) {
                char c = x < row.length() ? row.charAt(x) : '.';
                tiles[y][x] = charToTile(c);
                if (c == 'P') {
                    tiles[y][x] = TileType.PIPE_BODY;
                } else if (c == 'T') {
                    tiles[y][x] = TileType.PIPE_TOP;
                    sub[y][x] = (x > 0 && x - 1 < row.length() && row.charAt(x - 1) == 'T') ? 1 : 0;
                } else if (c == 'G') {
                    goalX = x;
                }
            }
        }
        return goalX;
    }

    private static TileType charToTile(char c) {
        switch (c) {
            case '#': return TileType.GROUND;
            case '=': return TileType.BLOCK;
            case 'B': return TileType.BRICK;
            case '?': return TileType.QUESTION;
            case 'U': return TileType.USED;
            case 'S': return TileType.SEMI_SOLID;
            case 'G': return TileType.GOAL;
            default: return TileType.EMPTY;
        }
    }

    private void spawnEntities() {
        placeGoomba(28, groundYAt(28) - 16);
        placeGoomba(45, groundYAt(45) - 16);
        placeGoomba(62, groundYAt(62) - 16);
        placeGoomba(78, 9 * 16);
        placeGoomba(95, groundYAt(95) - 16);
        placeGoomba(115, groundYAt(115) - 16);

        placeKoopa(52, groundYAt(52) - 16);
        placeKoopa(88, groundYAt(88) - 16);
        placeKoopa(108, groundYAt(108) - 16);

        for (int i = 0; i < 8; i++) {
            coins.add(new Coin((35 + i * 3) * 16, 10 * 16));
        }
        for (int i = 0; i < 5; i++) {
            coins.add(new Coin((70 + i * 2) * 16, 8 * 16));
        }
    }

    private int groundYAt(int tileX) {
        for (int y = 0; y < height; y++) {
            if (tiles[y][tileX].solid && tiles[y][tileX] != TileType.SEMI_SOLID) {
                return y * SmwConstants.TILE;
            }
        }
        return (height - 2) * SmwConstants.TILE;
    }

    private void placeGoomba(int tileX, int y) {
        enemies.add(new Goomba(tileX * SmwConstants.TILE, y));
    }

    private void placeKoopa(int tileX, int y) {
        enemies.add(new Koopa(tileX * SmwConstants.TILE, y));
    }

    public int getPixelWidth() {
        return width * SmwConstants.TILE;
    }

    public int getPixelHeight() {
        return height * SmwConstants.TILE;
    }

    public int getGoalX() {
        return goalTileX * SmwConstants.TILE;
    }

    public boolean isSolidAt(int tx, int ty, boolean fromAbove) {
        if (tx < 0 || ty < 0 || tx >= width || ty >= height) {
            return false;
        }
        TileType t = tiles[ty][tx];
        if (t == TileType.SEMI_SOLID) {
            return fromAbove;
        }
        return t.solid;
    }

    public TileType getTile(int tx, int ty) {
        if (tx < 0 || ty < 0 || tx >= width || ty >= height) {
            return TileType.EMPTY;
        }
        return tiles[ty][tx];
    }

    public void setTile(int tx, int ty, TileType type) {
        if (tx >= 0 && ty >= 0 && tx < width && ty < height) {
            tiles[ty][tx] = type;
        }
    }

    public void bumpBlock(int tx, int ty, GameState state) {
        TileType t = getTile(tx, ty);
        if (!t.bumpable) {
            return;
        }
        if (t == TileType.QUESTION) {
            sub[ty][tx] = 1;
            tiles[ty][tx] = TileType.USED;
            int px = tx * SmwConstants.TILE;
            int py = (ty - 1) * SmwConstants.TILE;
            if (!state.player.isBig()) {
                powerUps.add(new PowerUp(px, py, PowerUp.Type.MUSHROOM));
            } else {
                powerUps.add(new PowerUp(px, py, PowerUp.Type.FLOWER));
            }
            state.score += 1000;
        } else if (t == TileType.BRICK && state.player.isBig()) {
            tiles[ty][tx] = TileType.EMPTY;
            state.score += 50;
            coins.add(new Coin(tx * SmwConstants.TILE, ty * SmwConstants.TILE, true));
        }
    }

    public void drawBackground(Graphics2D g, int camX, int camY, int screenW, int screenH) {
        g.drawImage(SmwAssets.skyGradient, camX, camY, camX + screenW, camY + screenH, null);

        int parallaxFar = camX / 4;
        int parallaxNear = camX / 2;

        for (int i = -1; i < 4; i++) {
            int hx = camX + i * 200 - (parallaxFar % 200);
            g.drawImage(SmwAssets.hillsFar[0], hx, camY + screenH - 120, 160, 60, null);
            g.drawImage(SmwAssets.hillsFar[1], hx + 100, camY + screenH - 100, 120, 50, null);
        }
        for (int i = -1; i < 4; i++) {
            int hx = camX + i * 150 - (parallaxNear % 150);
            g.drawImage(SmwAssets.hillsNear[0], hx, camY + screenH - 80, 100, 40, null);
        }
        for (int i = 0; i < 3; i++) {
            g.drawImage(SmwAssets.cloud, camX + i * 90 - (camX / 3 % 90), camY + 30 + i * 20, 64, 32, null);
        }
        for (int i = 0; i < width; i += 8) {
            g.drawImage(SmwAssets.bush, camX + i * SmwConstants.TILE - parallaxNear / 2, camY + screenH - 50, 48, 24, null);
        }
    }

    public void drawTiles(Graphics2D g, int camX, int camY, int screenW, int screenH) {
        int startX = Math.max(0, camX / SmwConstants.TILE - 1);
        int endX = Math.min(width, (camX + screenW) / SmwConstants.TILE + 2);
        int startY = Math.max(0, camY / SmwConstants.TILE - 1);
        int endY = Math.min(height, (camY + screenH) / SmwConstants.TILE + 2);

        for (int ty = startY; ty < endY; ty++) {
            for (int tx = startX; tx < endX; tx++) {
                TileType t = tiles[ty][tx];
                if (t == TileType.EMPTY) {
                    continue;
                }
                BufferedImage img = SmwAssets.tileImage(t, sub[ty][tx]);
                if (img != null) {
                    g.drawImage(img, tx * SmwConstants.TILE, ty * SmwConstants.TILE, null);
                }
            }
        }
    }

    /** Yoshi's Island 1 inspired layout (World 1-1). */
    public static Level yoshisIsland1() {
        String[] rows = {
            "................................................................................................................................",
            "................................................................................................................................",
            "................................................................................................................................",
            "................................................................................................................................",
            "................................................................................................................................",
            "................................................................................................................................",
            "................................................................................................................................",
            "................................................................................................................................",
            "..........................................................????..................................................................",
            "..........................................................????..................................................................",
            "....................................................SSS.......................................................................",
            "..............................................???.............................................................................",
            "..............................................???.............................................................................",
            "................................................................................................................................",
            "...........................??.................................................................................................",
            "...........................??...BBBB..........................................................................................",
            "................................................................................................................................",
            "................................................................................................................................",
            "................................................................................................................................",
            "................................................................................................................................",
            "................................................................................................................................",
            "................................................................................................................................",
            "################################################################################################################################",
            "################################################################################################################################",
        };
        int w = 0;
        for (String row : rows) {
            w = Math.max(w, row.length());
        }
        char[][] grid = new char[rows.length][w];
        for (int y = 0; y < rows.length; y++) {
            char[] line = new char[w];
            java.util.Arrays.fill(line, '.');
            System.arraycopy(rows[y].toCharArray(), 0, line, 0, rows[y].length());
            grid[y] = line;
        }
        int ground = rows.length - 2;
        setChars(grid, 18, ground - 3, "TT");
        setChars(grid, 18, ground - 2, "PP");
        setChars(grid, 18, ground - 1, "PP");
        setChar(grid, 22, ground - 5, '?');
        setChar(grid, 23, ground - 5, '?');
        setChar(grid, 24, ground - 5, '?');
        setChar(grid, 25, ground - 5, '?');
        for (int i = 0; i < 4; i++) {
            setChar(grid, 40 + i, ground - 1 - i, '#');
        }
        for (int i = 0; i < 4; i++) {
            setChar(grid, 48 + i, ground - 4 + i, '#');
        }
        setChars(grid, 58, ground - 4, "TT");
        setChars(grid, 58, ground - 3, "PP");
        setChars(grid, 58, ground - 2, "PP");
        setChars(grid, 58, ground - 1, "PP");
        setChar(grid, grid[0].length - 4, ground - 5, 'G');
        setChar(grid, grid[0].length - 4, ground - 4, 'G');
        setChar(grid, grid[0].length - 4, ground - 3, 'G');
        setChar(grid, grid[0].length - 4, ground - 2, 'G');
        setChar(grid, grid[0].length - 4, ground - 1, 'G');

        String[] out = new String[rows.length];
        for (int y = 0; y < rows.length; y++) {
            out[y] = new String(grid[y]);
        }
        return new Level(out);
    }

    private static void setChar(char[][] g, int x, int y, char c) {
        if (y >= 0 && y < g.length && x >= 0 && x < g[0].length) {
            g[y][x] = c;
        }
    }

    private static void setChars(char[][] g, int x, int y, String s) {
        for (int i = 0; i < s.length(); i++) {
            setChar(g, x + i, y, s.charAt(i));
        }
    }
}
