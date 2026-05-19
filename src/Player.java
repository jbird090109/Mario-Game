import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

public class Player {
    private final GameState state;

    private double x = 48;
    private double y = 0;
    private double velX;
    private double velY;
    private double prevVelY;
    private double velYBeforeResolve;
    private boolean onGround;
    private boolean jumping;
    private boolean jumpHeld;
    private int jumpHoldFrames;

    private boolean left;
    private boolean right;
    private boolean runKey;
    private int jumpBuffer;

    private boolean facingRight = true;
    private boolean big;
    private boolean fire;
    private boolean dead;
    private int animFrame;
    private int speedOscIndex;
    private int pMeter;

    private static final int HITBOX_W = SmwConstants.MARIO_W;
    private static final int HITBOX_OFFSET = 1;
    private static final int SPRITE_W = 16;

    public Player(GameState state, int startY) {
        this.state = state;
        this.y = startY;
    }

    public GameState getState() {
        return state;
    }

    public void update() {
        if (dead) {
            velY += SmwConstants.GRAVITY_DESCENT;
            y += velY;
            return;
        }

        double maxSpeed = SmwConstants.WALK_MAX;
        double accel = SmwConstants.WALK_ACCEL;
        boolean sprinting = runKey && pMeter >= SmwConstants.P_METER_MAX;

        if (runKey && onGround) {
            pMeter = Math.min(SmwConstants.P_METER_MAX, pMeter + SmwConstants.P_METER_RUN);
        } else if (!onGround && runKey) {
            pMeter = Math.min(SmwConstants.P_METER_MAX, pMeter + 1);
        } else if (onGround) {
            pMeter = Math.max(0, pMeter - 2);
        }

        if (sprinting) {
            maxSpeed = SmwConstants.SPRINT_SPEEDS[speedOscIndex % 5] / 16.0;
            accel = SmwConstants.RUN_ACCEL;
            if (onGround && (left || right)) {
                speedOscIndex++;
            }
        } else if (runKey) {
            maxSpeed = SmwConstants.RUN_MAX;
            accel = SmwConstants.RUN_ACCEL;
        }

        if (left) {
            velX -= accel;
            facingRight = false;
        } else if (right) {
            velX += accel;
            facingRight = true;
        } else if (onGround) {
            velX *= SmwConstants.FRICTION_GROUND;
        } else {
            velX *= SmwConstants.FRICTION_AIR;
        }

        if (velX > maxSpeed) {
            velX = maxSpeed;
        }
        if (velX < -maxSpeed) {
            velX = -maxSpeed;
        }
        if (Math.abs(velX) < 0.05) {
            velX = 0;
        }

        if (jumpHeld && jumpHoldFrames < SmwConstants.JUMP_HOLD_FRAMES && velY < 0) {
            velY += SmwConstants.JUMP_HOLD_GRAVITY;
            jumpHoldFrames++;
        } else if (velY < 0) {
            velY += SmwConstants.GRAVITY_ASCENT;
        } else if (!onGround) {
            velY += SmwConstants.GRAVITY_DESCENT;
        }

        velYBeforeResolve = velY;
        onGround = TileCollision.resolve(this, state.level);

        if (onGround) {
            jumping = false;
        }
        if (jumpBuffer > 0 && onGround && !dead) {
            velY = SmwConstants.JUMP_VEL;
            jumping = true;
            jumpHeld = true;
            jumpHoldFrames = 0;
            onGround = false;
            jumpBuffer = 0;
        }
        if (jumpBuffer > 0) {
            jumpBuffer--;
        }

        if (y > state.level.getPixelHeight() + 32) {
            die();
        }

        prevVelY = velY;
        animFrame++;
    }

    public double getPrevVelY() {
        return prevVelY;
    }

    /** Vertical velocity immediately before tile collision (used for stomp detection). */
    public double getVelYBeforeResolve() {
        return velYBeforeResolve;
    }

    public void die() {
        if (!dead) {
            dead = true;
            velY = -6;
            state.hud.loseLife();
        }
    }

    public void powerUp(PowerUp.Type type) {
        state.invincibleFrames = Math.max(state.invincibleFrames, 90);
        if (type == PowerUp.Type.MUSHROOM) {
            if (!big) {
                big = true;
                y -= 12;
            }
            state.score += 1000;
        } else if (type == PowerUp.Type.FLOWER) {
            big = true;
            fire = true;
            state.score += 1000;
        } else if (type == PowerUp.Type.STAR) {
            state.starPower = true;
            state.invincibleFrames = 600;
        }
    }

    public void takeDamage() {
        if (state.starPower || state.invincibleFrames > 0) {
            return;
        }
        if (big) {
            big = false;
            fire = false;
            state.invincibleFrames = 120;
        } else {
            die();
        }
    }

    public void draw(Graphics2D g) {
        if (dead) {
            g.drawImage(SmwAssets.marioDead, (int) x, (int) y + 8, 16, 8, null);
            return;
        }

        BufferedImage sprite;
        BufferedImage[] frames;
        if (!onGround || jumping) {
            frames = SmwAssets.marioJumpR;
            sprite = frames[0];
        } else if ((left && velX > 0.5) || (right && velX < -0.5)) {
            frames = SmwAssets.marioSkidR;
            sprite = frames[0];
        } else if (Math.abs(velX) > 0.3) {
            frames = SmwAssets.marioRunR;
            sprite = frames[(animFrame / 4) % frames.length];
        } else {
            frames = SmwAssets.marioIdleR;
            sprite = frames[0];
        }

        if (!facingRight) {
            sprite = SmwAssets.flip(sprite);
        }

        int h = big ? SmwConstants.MARIO_H_BIG : SmwConstants.MARIO_H_SMALL;
        int drawY = (int) y + (SmwConstants.MARIO_H_SMALL - h);
        g.drawImage(sprite, (int) x, drawY, SPRITE_W, h, null);
    }

    public void handleKeyPress(int code) {
        if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
            left = true;
        }
        if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
            right = true;
        }
        if (code == KeyEvent.VK_Z || code == KeyEvent.VK_X || code == KeyEvent.VK_SHIFT) {
            runKey = true;
        }
        if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_S || code == KeyEvent.VK_UP) {
            jumpBuffer = 8;
            jumpHeld = true;
        }
    }

    public void handleKeyRelease(int code) {
        if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
            left = false;
        }
        if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
            right = false;
        }
        if (code == KeyEvent.VK_Z || code == KeyEvent.VK_X || code == KeyEvent.VK_SHIFT) {
            runKey = false;
        }
        if (code == KeyEvent.VK_SPACE || code == KeyEvent.VK_S || code == KeyEvent.VK_UP) {
            jumpHeld = false;
        }
    }

    public int getX() {
        return (int) x;
    }

    public int getY() {
        return (int) y;
    }

    public void setPosition(int nx, int ny) {
        x = nx;
        y = ny;
    }

    public double getVelX() {
        return velX;
    }

    public double getVelY() {
        return velY;
    }

    public void setVelX(double v) {
        velX = v;
    }

    public void setVelY(double v) {
        velY = v;
    }

    public int getHitboxX() {
        return (int) x + HITBOX_OFFSET;
    }

    public int getHitboxY() {
        return (int) y + (big ? 12 : 0);
    }

    public int getHitboxW() {
        return HITBOX_W;
    }

    public int getHitboxH() {
        return big ? SmwConstants.MARIO_H_BIG - 12 : SmwConstants.MARIO_H_SMALL;
    }

    public int getHitboxOffsetX() {
        return HITBOX_OFFSET;
    }

    public int getSpriteTopOffset() {
        return big ? 12 : 0;
    }

    public int getWidth() {
        return SPRITE_W;
    }

    public int getHeight() {
        return big ? SmwConstants.MARIO_H_BIG : SmwConstants.MARIO_H_SMALL;
    }

    public boolean isBig() {
        return big;
    }

    public boolean isDead() {
        return dead;
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    public int getPMeter() {
        return pMeter;
    }
}
