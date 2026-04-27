import java.awt.Point;
import java.awt.geom.Rectangle2D;
import javax.swing.JFrame;


public class Enemy {

   	private int x, y;
   	private int width, height;

   	private Player player;
  	private SoundManager soundManager;

   	private BezierCurveMotion bezierCurveMotion;

   	private Point p0, p1, p2;		// points for Bezier curve motion

    private GameAnimation animation;

    private JFrame window;
    private TileMap tileMap;
    private BackgroundManager bgManager;

    

	private int playerInsideTimer = 0;
	private static final int ATTACK_DELAY = 30;
	private int attackAnimTimer = 0;
	private boolean isAttacking = false;
	private boolean isDead = false;

   	public Enemy (JFrame w, Player p, TileMap t, BackgroundManager b) {
      		window = w;
            tileMap = t;
            bgManager = b;

     		width = 60;
      		height = 50;

      		this.player = p;

            animation = AnimationManager.loadAnimation("SkeletonWalk");
    	  	soundManager = SoundManager.getInstance();
   	}


   	public void update() {

      		if (!window.isVisible () || !bezierCurveMotion.isActive()) {
				return;
			}
			if(!isAttacking){
      		bezierCurveMotion.update();
			}
			if (isAttacking) {
        	attackAnimTimer--;
        	if (attackAnimTimer <= 0) {
            isAttacking = false; 
        		}
    		}
			if(!isAttacking){
			if (bezierCurveMotion.isMovingForward()) {
        
        	if (!animation.getName().equals("SkeletonWalk")) {
            animation = AnimationManager.loadAnimation("SkeletonWalk");
        	}
    		} else {
        	if (!animation.getName().equals("SkeletonWalkFlip")) {
            animation = AnimationManager.loadAnimation("SkeletonWalkFlip");
        		}
    			}
			}

      		boolean collision = collidesWithPlayer();

			
			if (collidesWithPlayer()) {
        	playerInsideTimer++;
        
        	// Change to an "Aggressive" animation if you have one
        	if (playerInsideTimer >= ATTACK_DELAY) {
            	performAttack();
            	playerInsideTimer = 0; // Reset timer so they don't hit every frame
       		 }
    		} else {
        	playerInsideTimer = 0; // Reset if the player jumps away
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

	private void performAttack() {
		isAttacking = true;
		attackAnimTimer = ATTACK_DELAY; 
    	animation = AnimationManager.loadAnimation("SkeletonAttack");
   
    	player.takeDamage();
	}

	public void deActivate () {
		bezierCurveMotion.deActivate();
	}
}