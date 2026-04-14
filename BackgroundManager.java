/* BackgroundManager manages many backgrounds (wraparound images 
   used for the game's background). 

   Backgrounds 'further back' move slower than ones nearer the
   foreground of the game, creating a parallax distance effect.

   When a sprite is instructed to move left or right, the sprite
   doesn't actually move, instead the backgrounds move in the 
   opposite direction (right or left).

*/

import java.awt.Graphics2D;
import java.io.File;
import javax.swing.JFrame;


public class BackgroundManager {

	String pathName = "images/backgrounds";

	private String bgImages[] = new String[50];

  	private Background[] backgrounds;
  	private int numBackgrounds;

  	private JFrame window;			// JFrame on which backgrounds are drawn

  	public BackgroundManager(JFrame window, int moveSize) {
						// ignore moveSize
    		this.window = window;

			File folder = new File(pathName);
			File[] files = folder.listFiles();
			for (int i=0; i < files.length; i++) {
				bgImages[i] = pathName + "/" + files[files.length - 1 - i].getName();
			}

			numBackgrounds = files.length;
    		backgrounds = new Background[numBackgrounds];

    		for (int i = 0; i < numBackgrounds; i++) {
       			backgrounds[i] = new Background(window, bgImages[i], i/2);
    		}
  	} 


  	public void moveRight() { 
		for (int i=0; i < numBackgrounds; i++)
      			backgrounds[i].moveRight();
  	}


  	public void moveLeft() {
		for (int i=0; i < numBackgrounds; i++)
      			backgrounds[i].moveLeft();
  	}


  	// The draw method draws the backgrounds on the screen. The
  	// backgrounds are drawn from the back to the front.

  	public void draw (Graphics2D g2) { 
		for (int i=0; i < numBackgrounds; i++)
      			backgrounds[i].draw(g2);
  	}

}

