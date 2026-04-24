import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JWindow;


public class Enemy {

   	private int x, y;
   	private int width, height;
			
   	private Image alienImage;

   	private Player player;
  	private SoundManager soundManager;

   	private BezierCurveMotion bezierCurveMotion;

   	private Point p0, p1, p2;		// points for Bezier curve motion

    private GameAnimation animation, enemyIdle;

    private JFrame window;
    private TileMap tileMap;
    private BackgroundManager bgManager;
    private AnimationManager animManager;

   	public Enemy (JFrame w, Player p, TileMap t, BackgroundManager b, AnimationManager a) {
      		window = w;
            tileMap = t;
            bgManager = b;
            animManager = a;

     		width = 60;
      		height = 50;

      		this.p0 = new Point(200, 200);
      		this.p1 = new Point(200, 200);
      		this.p2 = new Point(200, 200);

      		this.player = p;
            enemyIdle = animManager.loadAnimation("SkeletonIdle.png");
            animation = enemyIdle;

      		soundManager = SoundManager.getInstance();

      		bezierCurveMotion = new BezierCurveMotion (window, this, p0, p1, p2);
   	}


   	public void update() {

      		if (!window.isVisible () || !bezierCurveMotion.isActive()) 
			return;

      		bezierCurveMotion.update();

      		boolean collision = collidesWithPlayer();
      
      		// if (collision)
	  		// soundManager.playClip("hit", false);
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

	
	public boolean isActive () {
		return bezierCurveMotion.isActive();
	}


	public void activate () {
		bezierCurveMotion.activate();
	}


	public void deActivate () {
		bezierCurveMotion.deActivate();
	}
}