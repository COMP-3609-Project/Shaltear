import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;


/**
    The StripAnimation class creates an animation from a strip file.
*/
public class PlayerAnimation {
	
	Animation animation;

	private int x;		// x position of animation
	private int y;		// y position of animation

	private int width;
	private int height;

	private int dx;		// increment to move along x-axis
	private int dy;		// increment to move along y-axis

	private String name;

	public PlayerAnimation(String file) {

		animation = new Animation(true);	// run animation once

		dx = 0;		// increment to move along x-axis
		dy = 0;	// increment to move along y-axis

		width = 50;
		height = 80;

		// load images from strip file

		this.name = file;

		Image stripImage = ImageManager.loadImage("images/animations/" + file);

		int imageWidth = (int) stripImage.getWidth(null) / 5;
		int imageHeight = stripImage.getHeight(null);

		for(int i=0;i<5;i++){
			BufferedImage frameImage = new BufferedImage (imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) frameImage.getGraphics();
     
			g.drawImage(stripImage, 
					0, 0, imageWidth, imageHeight,
					i*imageWidth, 0, (i*imageWidth)+imageWidth, imageHeight,
					null);

			animation.addFrame(frameImage, 100);
		}
	
	}


	public void start() {
		x = 100;
        y = 200;
		animation.start();
	}

	
	public void update() {
		if (!animation.isStillActive())
			return;

		animation.update();
		x = x + dx;
		y = y + dy;
	}


	public void draw(Graphics2D g2, int xPos, int yPos) {
		if (!animation.isStillActive())
			return;

		g2.drawImage(animation.getImage(), xPos, yPos, width, height, null);
	}

	public int getWidth(){return width;}
	public int getHeight(){return height;}
	public String getName(){return this.name;}


}
