import java.awt.Image;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import javax.swing.JFrame;


public class Enemy {

   	private int x, y;
   	private int width, height;
			
   	private Image alienImage;

   	private Player player;
  	private SoundManager soundManager;

   	private BezierCurveMotion bezierCurveMotion;

   	private Point p0, p1, p2;		// points for Bezier curve motion

    private GameAnimation animation;

    private JFrame window;
    private TileMap tileMap;
    private BackgroundManager bgManager;
    

	private boolean isDead = false;

   	public Enemy (JFrame w, Player p, TileMap t, BackgroundManager b) {
      		window = w;
            tileMap = t;
            bgManager = b;

     		width = 60;
      		height = 50;

      		this.player = p;

            animation = AnimationManager.loadAnimation("SkeletonIdle");
    	  	soundManager = SoundManager.getInstance();
   	}


   	public void update() {

      		if (!window.isVisible () || !bezierCurveMotion.isActive()) {
				return;
			}
			
      		bezierCurveMotion.update();

      		boolean collision = collidesWithPlayer();

			
			if (collidesWithPlayer()) {
             // player.takeDamage();
        	}
      		
   	}


   	public Rectangle2D.Double getBoundingRectangle() {
      		return new Rectangle2D.Double (x, y, width, height);
   	}

   
   	public boolean collidesWithPlayer() {
      		Rectangle2D.Double myRect = getBoundingRectangle();
      		Rectangle2D.Double batRect = player.getBoundingRectangle();
      
      		return myRect.intersects(batRect); 
   	}


	public int getX() {
		return x;
	}


	public void setX (int x) {
		this.x = x;
	}


	public int getY() {
		return y;
	}


	public void setY (int y) {
		this.y = y;
	}

	public GameAnimation getAnimation() {
      return animation;
   }
	
	public boolean isActive () {
		return bezierCurveMotion.isActive();
	}


	public void activate () {
		bezierCurveMotion.activate();
	}

	
	public void setMovementPoints(Point p0, Point p1, Point p2) {
    	this.p0 = p0;
    	this.p1 = p1;
    	this.p2 = p2;
    	this.bezierCurveMotion = new BezierCurveMotion(window, this, p0, p1, p2);
	}

	public void die() {
    	isDead = true;
    	deActivate(); 
	}

	public boolean isDead() {
    	return isDead;
	}

	public void deActivate () {
		bezierCurveMotion.deActivate();
	}
}