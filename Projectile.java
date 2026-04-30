import java.awt.Rectangle;

public class Projectile {

 private int x, y;    
 private int speed;
 private boolean active = true;
 private boolean bossProjectile = false;

 public Projectile(int x, int y, int speed, boolean bossProjectile) {
     this.x = x;
     this.y = y;
     this.speed = speed;
     this.bossProjectile = bossProjectile;
 }

 public void update() {
     x += speed;
 }
 public Rectangle getBoundingRectangle() {
     return new Rectangle(x, y, 10, 10); 
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
}
