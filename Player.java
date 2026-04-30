import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import javax.swing.JFrame;

public class Player {			

   private static final int DX = 15;	// amount of X pixels to move in one keystroke
   private static final int DY = 32;	// amount of Y pixels to move in one keystroke

   private static final int TILE_SIZE = 64;

   private JFrame window;		// reference to the JFrame on which player is drawn
   private TileMap tileMap;
   private BackgroundManager bgManager;

   private int x;			// x-position of player's sprite
   private int y;			// y-position of player's sprite

   Graphics2D g2;
   private Dimension dimension;

   private GameAnimation animation;

   private int lives = 3;

   private boolean jumping;
   private int timeElapsed;
   private int startY;

   private boolean goingUp;
   private boolean goingDown;

   private boolean leftKey, rightKey, jumpKey;

   private boolean inAir;
   private int initialVelocity;
   private int startAir;

   private boolean isAttacking = false;
   private int attackTimer = 0;
   private int invincibleTimer = 0;
   private static final int INVINCIBILITY_DURATION = 180;


   public Player (JFrame window, TileMap t, BackgroundManager b) {
      this.window = window;

      tileMap = t;			// tile map on which the player's sprite is displayed
      bgManager = b;			// instance of BackgroundManager

      goingUp = goingDown = false;
      inAir = false;
   
      animation = AnimationManager.loadAnimation("PlayerIdle");


      x = 300;
      y = 736;


   }

   public Point collidesWithTile(int newX, int newY) {

      	  int playerWidth = animation.getWidth();
      	  int offsetY = tileMap.getOffsetY();
	  int xTile = tileMap.pixelsToTiles(newX);
	  int yTile = tileMap.pixelsToTiles(newY - offsetY);

	  if (tileMap.getTile(xTile, yTile) != null) {
	        Point tilePos = new Point (xTile, yTile);
	  	return tilePos;
	  }
	  else {
		return null;
	  }
   }


   public Point collidesWithTileDown (int newX, int newY) {

	  int playerWidth = animation.getWidth();
     int playerHeight = animation.getHeight();
     int offsetY = tileMap.getOffsetY();
	  int xTile = tileMap.pixelsToTiles(newX);
	  int yTileFrom = tileMap.pixelsToTiles(y - offsetY);
	  int yTileTo = tileMap.pixelsToTiles(newY - offsetY + playerHeight);

	  for (int yTile=yTileFrom; yTile<=yTileTo; yTile++) {
		if (tileMap.getTile(xTile, yTile) != null) {
	        	Point tilePos = new Point (xTile, yTile);
	  		return tilePos;
	  	}
		else {
			if (tileMap.getTile(xTile+1, yTile) != null) {
				int leftSide = (xTile + 1) * TILE_SIZE;
				if (newX + playerWidth > leftSide) {
				    Point tilePos = new Point (xTile+1, yTile);
				    return tilePos;
			        }
			}
		}
	  }

	  return null;
   }


   public Point collidesWithTileUp (int newX, int newY) {

	  int playerWidth = animation.getWidth();

      	  int offsetY = tileMap.getOffsetY();
	  int xTile = tileMap.pixelsToTiles(newX);

	  int yTileFrom = tileMap.pixelsToTiles(y - offsetY);
	  int yTileTo = tileMap.pixelsToTiles(newY - offsetY);
	 
	  for (int yTile=yTileFrom; yTile>=yTileTo; yTile--) {
		if (tileMap.getTile(xTile, yTile) != null) {
	        	Point tilePos = new Point (xTile, yTile);
	  		return tilePos;
		}
		else {
			if (tileMap.getTile(xTile+1, yTile) != null) {
				int leftSide = (xTile + 1) * TILE_SIZE;
				if (newX + playerWidth > leftSide) {
				    Point tilePos = new Point (xTile+1, yTile);
				    return tilePos;
			        }
			}
		}
				    
	  }

	  return null;
   }

 public void handleMovement() {
   if(!jumping && !inAir && !leftKey && !rightKey){
      if(animation.getName().equals("PlayerMove")){
         animation = AnimationManager.loadAnimation("PlayerIdle");
      }else if(animation.getName().equals("PlayerMoveFlip")){
         animation = AnimationManager.loadAnimation("PlayerIdleFlip");
      }

   }

   // 1. Handle Jumping (Upward trigger)
   if (jumpKey && !jumping && !inAir) {
      // animation = playerJump;
      jump();
   }

   // 2. Handle Left Movement
   if (leftKey) {
      if(!jumping && !inAir){
         animation = AnimationManager.loadAnimation("PlayerMoveFlip");
      }
      int newX = x - DX;
      if (newX >= 0) {
         Point tilePos = collidesWithTile(newX, y);
         if (tilePos == null) {
            x = newX;
            bgManager.moveLeft();
         } else {
            x = ((int) tilePos.getX() + 1) * TILE_SIZE; // Flush with tile
         }
      }
   }

   // 3. Handle Right Movement
   if (rightKey) {
      if(!jumping && !inAir){
         animation = AnimationManager.loadAnimation("PlayerMove");
      }

      int playerWidth = animation.getWidth();
      int newX = x + DX;
      int tileMapWidth = tileMap.getWidthPixels();

      if (newX + playerWidth < tileMapWidth) {
         Point tilePos = collidesWithTile(newX + playerWidth, y);
         if (tilePos == null) {
            x = newX;
            bgManager.moveRight();
         } else {
            x = ((int) tilePos.getX()) * TILE_SIZE - playerWidth; // Flush with tile
         }
      }
}

// 4. Handle falling if we walked off a ledge while moving
   if ((leftKey || rightKey) && isInAir()) {
      // animation = playerJump;
      fall();
   }
}

   public boolean isInAir() {

      int playerHeight;
      Point tilePos;

      if (!jumping && !inAir) {   
	  playerHeight = animation.getHeight();
	  tilePos = collidesWithTile(x, y + playerHeight + 1); 	// check below player to see if there is a tile
	
  	  if (tilePos == null)				   	// there is no tile below player, so player is in the air
		return true;
	  else							// there is a tile below player, so the player is on a tile
		return false;
      }

      return false;
   }


   private void fall() {

      jumping = false;
      inAir = true;
      timeElapsed = 0;

      goingUp = false;
      goingDown = true;

      startY = y;
      initialVelocity = 0;
   }


   public void jump () {  

      if (!window.isVisible ()) return;

      jumping = true;
      timeElapsed = 0;

      goingUp = true;
      goingDown = false;

      startY = y;
      initialVelocity = 70;
   }


   public void update() {
      int distance = 0;
      int newY = 0;

      timeElapsed++;

      if (invincibleTimer > 0) {
        invincibleTimer--;
    }

      if (jumping || inAir) {
         distance = (int) (initialVelocity * timeElapsed - 4.9 * timeElapsed * timeElapsed);
         newY = startY - distance;

         if (newY > y && goingUp) {
            goingUp = false;
            goingDown = true;
         }

         if (goingUp) {
            Point tilePos = collidesWithTileUp (x, newY);	
            if (tilePos != null) {				// hits a tile going up

               int offsetY = tileMap.getOffsetY();
               int topTileY = ((int) tilePos.getY()) * TILE_SIZE + offsetY;
               int bottomTileY = topTileY + TILE_SIZE;
               y = bottomTileY;
               fall();
         }
            else {
            y = newY;
            }
               }
         else if (goingDown) {			
            Point tilePos = collidesWithTileDown (x, newY);	
            if (tilePos != null) {				// hits a tile going up
               int playerHeight = animation.getHeight();
               goingDown = false;

               int offsetY = tileMap.getOffsetY();
               int topTileY = ((int) tilePos.getY()) * TILE_SIZE + offsetY;

               y = topTileY - playerHeight;
               jumping = false;
               inAir = false;
            }
            else {
               y = newY;
            }
         }
      }

      if (isAttacking) {
        attackTimer--;
        if (attackTimer <= 0) {
            isAttacking = false;
            // Return to idle based on direction
            animation = animation.getName().contains("Flip") ? 
                        AnimationManager.loadAnimation("PlayerIdleFlip") : 
                        AnimationManager.loadAnimation("PlayerIdle");
        }
      }
   }

   public void attack() {
      if (isAttacking) return;

      
      isAttacking = true;
      attackTimer = 15; 

      if (animation.getName().contains("Flip")) {
         animation = AnimationManager.loadAnimation("PlayerAttackFlip");
      } else {
         animation = AnimationManager.loadAnimation("PlayerAttack");
      }

      int range = 40; 
      Rectangle2D.Double attackHitbox;

      if (animation.getName().contains("Flip")) {
         // Facing Left: Hitbox starts 'range' pixels to the left of the player's X
         attackHitbox = new Rectangle2D.Double(x - range, y, range, animation.getHeight());
      } else {
         // Facing Right: Hitbox starts at the player's right edge (x + width)
         attackHitbox = new Rectangle2D.Double(x + animation.getWidth(), y, range, animation.getHeight());
      }

    LinkedList allSprites = tileMap.getSprites();
    
    for (Object s : allSprites) {
        if (s instanceof Enemy) {
            Enemy e = (Enemy) s;
            
            if (e.isActive() && attackHitbox.intersects(e.getBoundingRectangle())) {
                e.die(); // Kill the enemy
                
                System.out.println("Enemyaa defeated!");
            }
        }
    }
}
   public void takeDamage() {
      if (invincibleTimer > 0) {
         return;
      }
      lives--;
      invincibleTimer = INVINCIBILITY_DURATION;
      System.out.println("Lives remaining: " + lives);
}

   public void setKey(int direction, boolean isPressed) {
    if (direction == 1) leftKey = isPressed;
    if (direction == 2) rightKey = isPressed;
    if (direction == 3) jumpKey = isPressed;
	}

   public void moveUp () {

      if (!window.isVisible ()) return;

      y = y - DY;
   }

   public int getLives() { 
      return lives;
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

   public boolean isInvincible() {
        return invincibleTimer > 0;
    }

   public Rectangle2D.Double getBoundingRectangle() {
         return new Rectangle2D.Double (x, y, animation.getWidth(), animation.getHeight());
   }

/*

   public Point collidesWithTile(int newX, int newY) {

	 int playerWidth = playerImage.getWidth(null);
	 int playerHeight = playerImage.getHeight(null);

      	 int fromX = Math.min (x, newX);
	 int fromY = Math.min (y, newY);
	 int toX = Math.max (x, newX);
	 int toY = Math.max (y, newY);

	 int fromTileX = tileMap.pixelsToTiles (fromX);
	 int fromTileY = tileMap.pixelsToTiles (fromY);
	 int toTileX = tileMap.pixelsToTiles (toX + playerWidth - 1);
	 int toTileY = tileMap.pixelsToTiles (toY + playerHeight - 1);

	 for (int x=fromTileX; x<=toTileX; x++) {
		for (int y=fromTileY; y<=toTileY; y++) {
			if (tileMap.getTile(x, y) != null) {
				Point tilePos = new Point (x, y);
				return tilePos;
			}
		}
	 }
	
	 return null;
   }
*/


   /*public synchronized void move (int direction) {

      int newX = x;
      Point tilePos = null;

      if (!window.isVisible ()) return;
      
      if (direction == 1) {		// move left
	  playerImage = playerLeftImage;
          newX = x - DX;
	  if (newX < 0) {
		x = 0;
		return;
	  }
		
	  tilePos = collidesWithTile(newX, y);
      }	
      else				
      if (direction == 2) {		// move right
	  playerImage = playerRightImage;
      	  int playerWidth = playerImage.getWidth(null);
          newX = x + DX;

      	  int tileMapWidth = tileMap.getWidthPixels();

	  if (newX + playerImage.getWidth(null) >= tileMapWidth) {
	      x = tileMapWidth - playerImage.getWidth(null);
	      return;
	  }

	  tilePos = collidesWithTile(newX+playerWidth, y);			
      }
      else				// jump
      if (direction == 3 && !jumping) {	
          jump();
	  return;
      }
    
      if (tilePos != null) {  
         if (direction == 1) {
	     System.out.println (": Collision going left");
             x = ((int) tilePos.getX() + 1) * TILE_SIZE;	   // keep flush with right side of tile
	 }
         else
         if (direction == 2) {
	     System.out.println (": Collision going right");
      	     int playerWidth = playerImage.getWidth(null);
             x = ((int) tilePos.getX()) * TILE_SIZE - playerWidth; // keep flush with left side of tile
	 }
      }
      else {
          if (direction == 1) {
	      x = newX;
	      bgManager.moveLeft();
          }
	  else
	  if (direction == 2) {
	      x = newX;
	      bgManager.moveRight();
   	  }

          if (isInAir()) {
	      System.out.println("In the air. Starting to fall.");
	      if (direction == 1) {				// make adjustment for falling on left side of tile
      	          int playerWidth = playerImage.getWidth(null);
		  x = x - playerWidth + DX;
	      }
	      fall();
          }
      }
   }
	  */

}