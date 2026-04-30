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
   	private int dx = 2;
   	private int dy = 2;
	private int xPos, yPos;			// location from which to generate projectile
   	private int initialVelocityX = 25;
   	private int initialVelocityY = 25;
	private int direction;

   	private Dimension dimension;

   	double timeElapsed;
	boolean active;

	// New fields for unified projectile system
	private int projectileSpeed;       // horizontal speed
	private TileMap tileMap;            // reference to tileMap for enemy collision
	private GameAnimation animation;

	public ProjectileMotion (JFrame w) {
      	window = w;
		active = false;
		timeElapsed = 0;
      	dimension = window.getSize();
		xPos = 25;
		yPos = 200 - YSIZE;
		animation = AnimationManager.loadAnimation("Fireball");
	}

	public ProjectileMotion(JFrame w, Player p, TileMap tileMap) {
		this(w);
		this.tileMap = tileMap;
		this.projectileSpeed = 15;
		this.player = p;
	}

	public ProjectileMotion(JFrame w, Boss b, int speed) {
		this(w);
		this.projectileSpeed = speed;
		this.boss = b;
	}


	public boolean isActive() {
		return active;
	}

	public void activate() {
		if(player!=null) {
			xPos = player.getX();
			yPos = player.getY() + player.getHeight() / 2;
			if(player.getDirection() == 2) {
				direction = 2;
			}else{
				direction = 1;
			}
		} else {
			xPos = boss.getX();
			yPos = boss.getY() + boss.getAnimation().getHeight() / 2;
			direction = 1;
		}
		active = true;
		timeElapsed = 0;
	}

	public void deActivate() {
		active = false;
	}


   	public void update () {  

		if (!window.isVisible () || !active) return;

		if (boss!=null) {
			timeElapsed = timeElapsed + 0.5;

			x = (int) (initialVelocityX * timeElapsed);
			y = (int) (initialVelocityX * timeElapsed);
			// y = (int) (initialVelocityY * timeElapsed - 1 * timeElapsed * timeElapsed);
			if (getBoundingRect().intersects(boss.getPlayer().getBoundingRectangle())) {
				boss.getPlayer().takeDamage();
				active = false;
				return;
			}

			if (xPos < -XSIZE || xPos > dimension.width + XSIZE) {
				active = false;
			}
		} else {
		 
			timeElapsed = timeElapsed + 0.5;

			x = (int) (initialVelocityX * timeElapsed);
			y = (int) (initialVelocityY * timeElapsed - 1 * timeElapsed * timeElapsed);

			if (y > 0)
				y = yPos - y;			// yPos is the height at which ball is thrown
			else
				y = yPos + y * -1;

			// Calculate actual position
			int drawX;
			if (player.getDirection() == 2) {
				drawX = xPos + x;
			} else {
				drawX = xPos - x;
			}

			// Check collision with enemies
			if (tileMap != null) {
				java.util.LinkedList allSprites = tileMap.getSprites();
				for (Object s : allSprites) {
					if (s instanceof Enemy e) {
						Rectangle2D projRect = new Rectangle2D.Double(drawX, y, XSIZE, YSIZE);
						if (e.isActive() && projRect.intersects(e.getBoundingRectangle())) {
							e.die();
							active = false;
							return;
						}
					}
				}
			}
		}
	}
	
	// Get bounding rectangle for collision detection
	public Rectangle2D getBoundingRectangle() {
		int drawX;
		if (player.getDirection() == 2) {
			drawX = xPos + x;
		} else {
			drawX = xPos - x;
		}
		return new Rectangle2D.Double(drawX, y, XSIZE, YSIZE);
	}
	
	// Get simple bounding rect for boss projectile
	public Rectangle2D getBoundingRect() {
		return new Rectangle2D.Double(xPos, yPos, XSIZE, YSIZE);
	}
	
	public int getX() {
		return xPos;
	}
	
	public int getY() {
		return yPos;
	}

	public int getXSIZE(){
		return XSIZE;
	}

	public int getYSIZE(){
		return YSIZE;
	}

	public GameAnimation getAnimation() {
		return animation;
	}

	public void draw(Graphics2D g2, int tileOffsetX) {
		if (animation != null && active) {
			if(boss != null) {
				animation.draw(g2, xPos - x + tileOffsetX, y, XSIZE, YSIZE);
			}else{
				if(direction==2){
					animation.draw(g2, xPos + x + tileOffsetX, y, XSIZE, YSIZE);
				}else{
					animation.draw(g2, xPos - x + tileOffsetX, y, XSIZE, YSIZE);
				}
			}
		}
	}

}