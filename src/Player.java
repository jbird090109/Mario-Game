import java.awt.*;
import java.awt.event.KeyEvent;

public class Player {
    private int x;
    private int y;
    private int width = 32;
    private int height = 48;
    private int velocityX = 0;
    private int velocityY = 0;
    private int speed = 5;
    private int jumpPower = -15;
    private int gravity = 1;
    private boolean jumping = false;
    private boolean onGround = false;

    private boolean leftPressed = false;
    private boolean rightPressed = false;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        if (leftPressed) {
            velocityX = -speed;
        } else if (rightPressed) {
            velocityX = speed;
        } else {
            velocityX = 0;
        }

        x += velocityX;

        if (!onGround) {
            velocityY += gravity;
        }

        y += velocityY;

        if (y >= 550) {
            y = 550;
            velocityY = 0;
            onGround = true;
            jumping = false;
        } else {
            onGround = false;
        }

        if (x < 0) x = 0;
        if (x + width > 800) x = 800 - width;
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, width, height);
    }

    public void handleKeyPress(int keyCode) {
        if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) {
            leftPressed = true;
        }
        if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) {
            rightPressed = true;
        }
        if ((keyCode == KeyEvent.VK_SPACE || keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP) && onGround) {
            velocityY = jumpPower;
            jumping = true;
            onGround = false;
        }
    }

    public void handleKeyRelease(int keyCode) {
        if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) {
            leftPressed = false;
        }
        if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) {
            rightPressed = false;
        }
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
}
