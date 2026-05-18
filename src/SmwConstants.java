/** Super Mario World SNES constants (scaled for 60fps Java port). */
public final class SmwConstants {
    private SmwConstants() {}

    public static final int SNES_WIDTH = 256;
    public static final int SNES_HEIGHT = 224;
    public static final int SCALE = 3;
    public static final int SCREEN_W = SNES_WIDTH * SCALE;
    public static final int SCREEN_H = SNES_HEIGHT * SCALE;

    public static final int TILE = 16;
    public static final int FPS = 60;

    public static final double WALK_MAX = 2.25;
    public static final double RUN_MAX = 2.75;
    public static final double SPRINT_MAX = 3.06;
    public static final double WALK_ACCEL = 0.14;
    public static final double RUN_ACCEL = 0.18;
    public static final double FRICTION_GROUND = 0.85;
    public static final double FRICTION_AIR = 0.92;

    public static final double JUMP_VEL = -9.5;
    public static final double JUMP_HOLD_GRAVITY = 0.28;
    public static final double GRAVITY_ASCENT = 0.42;
    public static final double GRAVITY_DESCENT = 0.55;
    public static final int JUMP_HOLD_FRAMES = 18;

    public static final int P_METER_MAX = 112;
    public static final int P_METER_RUN = 2;
    public static final int P_METER_SPRINT = 4;

    public static final int MARIO_W = 14;
    public static final int MARIO_H_SMALL = 16;
    public static final int MARIO_H_BIG = 28;

    public static final int[] SPRINT_SPEEDS = {48, 47, 48, 47, 49};
    public static final int[] RUN_SPEEDS = {36, 35, 36, 35, 37};
}
