import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
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
	private LinkedList<ProjectileMotion> projectiles;
	private int shootTimer = 0;
	private static final int SHOOT_DELAY = 20;

	private boolean isDead = false;
   private boolean isDying = false;
   private int deathTimer = 0;
   private static final int DEATH_DURATION = 7;
   private GameAnimation projectileAnimation;

    public Boss (JFrame w, Player p, TileMap t, BackgroundManager b) {
      	this.window = w;
         this.tileMap = t;
         this.bgManager = b;
			this.player = p;

     		this.width = 192;
      	this.height = 192;

      	this.circularMotion = new CircularMotion(1000, 400, 200, 3);

			this.projectiles = new LinkedList<>();
         this.projectileAnimation = AnimationManager.loadAnimation("Fireball");

         this.animation = AnimationManager.loadAnimation("BossIdleFlip");
    	  	this.soundManager = SoundManager.getInstance();
   	}


	public void update() {

		if(isDead) {
			return;
		}

      if (isDying) {
        deathTimer++;
        if (deathTimer >= DEATH_DURATION) {
            isDead = true; 
        }
        return; 
    }
		circularMotion.update();
		this.x = circularMotion.getX();
		this.y = circularMotion.getY();

      shootTimer++;
      if (shootTimer >= SHOOT_DELAY) {
         fireProjectile();
         shootTimer = 0;
      }
      updateProjectiles();

	}
	public void takeDamage() {
       if (isDying || isDead) return; 
          health--;

       if (health <= 0) {
        startDeathSequence();
     }
   }
   
   public Rectangle2D.Double getBoundingRectangle() {
      return new Rectangle2D.Double(x, y, width, height);
   }

   public void drawProjectiles(Graphics2D g2, int tileOffsetX) {
       for (ProjectileMotion p : projectiles) {
         p.draw(g2, tileOffsetX);
      }
   }
   
   public void fireProjectile() {
     ProjectileMotion proj = new ProjectileMotion(window, this, -15);
     proj.activate();
     projectiles.add(proj);
   }
   private void updateProjectiles() {
     for (int i = 0; i < projectiles.size(); i++) {
         ProjectileMotion p = projectiles.get(i);
         p.update();

         if(p.getBoundingRect().intersects(player.getBoundingRectangle())) {
             player.takeDamage();
             p.deActivate();
         }

         if (!p.isActive()) {
             projectiles.remove(i);
             i--;
         }
     }

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
   public LinkedList<ProjectileMotion> getProjectiles() {
	  return projectiles;
   }
   public boolean isDead() {
	  return isDead;
   }
   private void startDeathSequence() {
    isDying = true;
    this.animation = AnimationManager.loadAnimation("BossDeath"); 
    this.soundManager.playSound("boss_death", false);
}
   public void die() {
	  isDead = true;
     animation = AnimationManager.loadAnimation("BossDeath");
   }
   public Player getPlayer(){return this.player;}
   public void setPlayer(Player p){
      this.player = p;
   }
}