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
import java.util.ArrayList;
import javax.swing.JFrame;


public class BackgroundManager {

	String pathName = "images/backgrounds/Background";

	private String bgImages[] = new String[50];

  	private ArrayList<ArrayList<Background>> backgrounds;

  	private JFrame window;			// JFrame on which backgrounds are drawn

  	public BackgroundManager(JFrame window, int moveSize) {
						// ignore moveSize
		this.window = window;
		backgrounds = new ArrayList<>();

		for(int i=1;i<=2;i++){
			File folder = new File(pathName + i);
			File[] files = folder.listFiles();
			ArrayList<Background> bg = new ArrayList<>();

			for (int j=0; j < files.length; j++) {
				bg.add(new Background(window, pathName + i + "/" + files[files.length - 1 - j].getName(), j));
			}
			backgrounds.add(bg);
		}
  	} 


  	public void moveRight() { 
		for (ArrayList<Background> backgroundList : backgrounds) {
			for (Background background : backgroundList) {
				background.moveRight();
			}
		}
  	}


  	public void moveLeft() {
		for (ArrayList<Background> backgroundList : backgrounds) {
			for (Background background : backgroundList) {
				background.moveLeft();
			}
		}
  	}


  	// The draw method draws the backgrounds on the screen. The
  	// backgrounds are drawn from the back to the front.

  	public void draw (Graphics2D g2, int bgNum) {
		for(Background background : backgrounds.get(bgNum-1)){
			background.draw(g2);
		}
  	}

}

