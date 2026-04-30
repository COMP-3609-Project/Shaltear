import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Projectile {

 private int x, y;    
 private int speed;
 private boolean active = true;
 private boolean bossProjectile = false;
 private GameAnimation animation;

 public Projectile(int x, int y, int speed, boolean bossProjectile, GameAnimation animation) {
     this.x = x;
     this.y = y;
     this.speed = speed;
     this.bossProjectile = bossProjectile;
     this.animation = animation;
 }

 public void update() {
     x += speed;

     if(animation != null) {
         animation.update();
     }
 }
 public Rectangle getBoundingRectangle() {
     return new Rectangle(x, y, 10, 10); 
 }
public void draw(Graphics2D g2, int offsetX) {
     if(animation != null) {
         animation.draw(g2, x + offsetX, y, 10, 10);
     }
 }
 public boolean isActive() {
     return active;
 }

 public void deactivate() {
     active = false;
 }
 public boolean isBossProjectile() {
        return bossProjectile;
 }
 public int getX() {
     return x;
 }
public int getY() {
    return y;
}
public GameAnimation getAnimation() {
    return animation;
}
}
