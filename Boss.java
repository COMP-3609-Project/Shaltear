import java.util.LinkedList;
import javax.swing.JFrame;

public class Boss {

    private int x, y;
   	private int width, height;
	private int health = 10;

   	private Player player;
  	private SoundManager soundManager;
    private GameAnimation animation;
    private JFrame window;
    private TileMap tileMap;
    private BackgroundManager bgManager;

	private CircularMotion circularMotion;
	private LinkedList<Projectile> projectiles;
	private int shootTimer = 0;
	private static final int SHOOT_DELAY = 60;

	private boolean isDead = false;

    public Boss (JFrame w, Player p, TileMap t, BackgroundManager b) {
      		this.window = w;
            this.tileMap = t;
            this.bgManager = b;
			this.player = p;

     		this.width = 128;
      		this.height = 128;

      		this.circularMotion = new CircularMotion(2000, 500, 200, 0.02);
			this.projectiles = new LinkedList<>();

            this.animation = AnimationManager.loadAnimation("BossIdle");
    	  	this.soundManager = SoundManager.getInstance();
   	}

	public void update() {
		if(isDead) {
			return;
		}
		circularMotion.update();
		this.x = circularMotion.getX();
		this.y = circularMotion.getY();
	}
	public void takeDamage() {
      health--;
      System.out.println("Boss Health remaining: " + health);
}
	public int getHealth() { 
      return health;
    }
	public int getX() {
      return x;
   }
   public void setX(int x) {
      this.x = x;
   }
   public int getY() {
      return y;
   }
   public void setY(int y) {
      this.y = y;
   }
   public GameAnimation getAnimation() {
      return animation;
   }
   public LinkedList<Projectile> getProjectiles() {
	  return projectiles;
   }
   public boolean isDead() {
	  return isDead;
   }
   public void die() {
	  isDead = true;
   }
}