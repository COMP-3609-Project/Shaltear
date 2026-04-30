import java.awt.Dimension;
import java.awt.geom.Rectangle2D;
import javax.swing.JFrame;


public class Collectible {

	private static final int XSIZE = 50;		// width of the image
	private static final int YSIZE = 50;		// height of the image
	//private static final int DX = 2;		// amount of pixels to move in one update
	private static final int YPOS = 150;		// vertical position of the image

	private JFrame panel;				// JPanel on which image will be drawn
	private Dimension dimension;
	private int x;
	private int y;
	private int dx;

	private Player player;

	private GameAnimation animation;

	//Graphics2D g2;

	int time, timeChange;				// to control when the image is grayed
	boolean originalImage, grayImage;


	public Collectible (JFrame panel, Player player, int x, int y) {
		this.panel = panel;
		//Graphics g = window.getGraphics ();
		//g2 = (Graphics2D) g;

		dimension = panel.getSize();

		this.player = player;
		this.x = x;
		this.y = y;

		time = 0;				// range is 0 to 10
		timeChange = 1;				// set to 1
		originalImage = true;
		grayImage = false;

		animation = AnimationManager.loadAnimation("Bat");

	}


	public boolean collidesWithPlayer () {
		Rectangle2D.Double myRect = getBoundingRectangle();
		Rectangle2D.Double playerRect = player.getBoundingRectangle();
		
            return myRect.intersects(playerRect);
	}


	public Rectangle2D.Double getBoundingRectangle() {
		return new Rectangle2D.Double (x, y, XSIZE, YSIZE);
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

	public GameAnimation getAnimation(){return this.animation;}

}