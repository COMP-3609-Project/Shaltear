import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class Background {
  	private Image bgImage;
  	private int bgImageWidth;      		// width of the background (>= panel Width)

	private Dimension dimension;

	private int backgroundX;
	private int backgroundX2;
	private int bgDX;			// size of the background move (in pixels)


	public Background(JFrame window, String imageFile, int bgDX) {

    		this.bgImage = loadImage(imageFile);
    		bgImageWidth = bgImage.getWidth(null);	// get width of the background

		System.out.println ("bgImageWidth = " + bgImageWidth);

		dimension = window.getSize();

		if (bgImageWidth < dimension.width)
      			System.out.println("Background width < panel width");

    		this.bgDX = bgDX;

		backgroundX = 0;
		backgroundX2 = bgImageWidth;
  	}


  	public void moveRight() {

		backgroundX = backgroundX - bgDX;
		backgroundX2 = backgroundX2 - bgDX;

		if (backgroundX <= -bgImageWidth) {
			backgroundX = backgroundX2 + bgImageWidth;
		}

		if (backgroundX2 <= -bgImageWidth) {
			backgroundX2 = backgroundX + bgImageWidth;
		}
  	}


  	public void moveLeft() {
	
		backgroundX = backgroundX + bgDX;	
		backgroundX2 = backgroundX2 + bgDX;

		if (backgroundX >= bgImageWidth) {
			backgroundX = backgroundX2 - bgImageWidth;
		}

		if (backgroundX2 >= bgImageWidth) {
			backgroundX2 = backgroundX - bgImageWidth;
		}
   	}
 

  	public void draw (Graphics2D g2) {
		// Draw the two main background copies
		g2.drawImage(bgImage, backgroundX, 0, dimension.width, dimension.height, null);
		g2.drawImage(bgImage, backgroundX2, 0, dimension.width, dimension.height, null);
  	}


  	public Image loadImage (String fileName) {
		return new ImageIcon(fileName).getImage();
  	}

}
