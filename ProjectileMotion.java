import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JFrame;

public class ProjectileMotion {

    private static final int XSIZE = 64;
    private static final int YSIZE = 64;

    private JFrame window;
    private Player player;
    private Boss boss;
    private int x = 0;
    private int y = 200;
    private int xPos, yPos;
    private int initialVelocityX = 25;
    private int initialVelocityY = 45;
    private int direction; // 1 for Left, 2 for Right

    private Dimension dimension;
    double timeElapsed;
    boolean active;

    private TileMap tileMap;
    private GameAnimation animation;

    public ProjectileMotion(JFrame w) {
        window = w;
        active = false;
        timeElapsed = 0;
        dimension = window.getSize();
        animation = AnimationManager.loadAnimation("Fireball");
    }

    
    public ProjectileMotion(JFrame w, Player p, TileMap tileMap) {
        this(w);
        this.player = p;
        this.tileMap = tileMap;
    }

   
    public ProjectileMotion(JFrame w, Boss b, int speed) {
        this(w);
        this.boss = b;
        this.initialVelocityX = Math.abs(speed); 
    }

    public void activate() {
        if (player != null) {
            xPos = player.getX();
            yPos = player.getY() + player.getHeight() / 2;
            direction = player.getDirection();
        } else if (boss != null) {
            xPos = boss.getX();
            
            yPos = boss.getY() + 96; 
            direction = 1; // Boss shoots Left
        }
        active = true;
        timeElapsed = 0;
        x = 0;
        y = yPos;
    }

    public void update() {
    if (!active || window == null) return;

    timeElapsed += 0.5; 
    x = (int) (initialVelocityX * timeElapsed);

    if (boss != null) {
        // BOSS FIREBALL LOGIC 
        y = (int) (yPos + (0.25 * timeElapsed * timeElapsed));

        if (boss.getPlayer() != null) {
            if (getBoundingRectangle().intersects(boss.getPlayer().getBoundingRectangle())) {
                boss.getPlayer().takeDamage();
                active = false;
            }
        }
    } else if (player != null) {
        // PLAYER FIREBALL LOGIC 
        int arcY = (int) (initialVelocityY * timeElapsed - 1 * timeElapsed * timeElapsed);
        y = yPos - arcY;

        if (tileMap != null) {
            
            if (tileMap.getBossList() != null) {
                for (Boss b : tileMap.getBossList()) {
                    if (!b.isDead() && getBoundingRectangle().intersects(b.getBoundingRectangle())) {
                        b.takeDamage();
                        active = false;
                        return; 
                    }
                }
            }

            if (tileMap.getSprites() != null) {
                for (Object s : tileMap.getSprites()) {
                    if (s instanceof Enemy e && e.isActive()) {
                        if (getBoundingRectangle().intersects(e.getBoundingRectangle())) {
                            e.die();
                            active = false;
                            return;
                        }
                    }
                }
            }
        }
    }

    // Screen Boundary Deactivation
    if (x > 3000 || x < -3000 || y > 2000) active = false;
}

    public Rectangle2D getBoundingRectangle() {
        int drawX;
        if (boss != null || direction == 1) {
            drawX = xPos - x;
        } else {
            drawX = xPos + x;
        }
        return new Rectangle2D.Double(drawX, y, XSIZE, YSIZE);
    }

    public void draw(Graphics2D g2, int tileOffsetX) {
        if (!active || animation == null) return;

        Rectangle2D rect = getBoundingRectangle();

        animation.draw(g2, (int)rect.getX() + tileOffsetX, (int)rect.getY(), XSIZE, YSIZE);
    }

    public boolean isActive() { return active; }
    public void deActivate() { active = false; }
    public Rectangle2D getBoundingRect() { return getBoundingRectangle(); }
}