public class GameState {
    public Player player;
    public final Level level;
    public final HUD hud;

    public int score;
    public boolean levelComplete;
    public boolean gameOver;
    public int invincibleFrames;
    public boolean starPower;

    public GameState(Player player, Level level, HUD hud) {
        this.player = player;
        this.level = level;
        this.hud = hud;
    }
}
