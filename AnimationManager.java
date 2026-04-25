import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JFrame;


public class AnimationManager {

	private String pathName = "images/animations";

    private static ArrayList<GameAnimation> animations;

  	private JFrame window;			// JFrame on which backgrounds are drawn

  	public AnimationManager(JFrame window) {
    		this.window = window;

			File folder = new File(pathName);
			File[] files = folder.listFiles();
            animations = new ArrayList<>();

			for (File file : files) {
                Image stripImage = ImageManager.loadImage("images/animations/" + file.getName());
                BufferedImage src = new BufferedImage(stripImage.getWidth(null), stripImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = src.createGraphics();
                g2d.drawImage(stripImage, 0, 0, null);
                g2d.dispose();
                
                Image flipImage = ImageManager.hFlipImage(src);
                String fileName = file.getName().substring(0, file.getName().length() - 4);

                animations.add(new GameAnimation(stripImage, fileName));
                animations.add(new GameAnimation(flipImage, fileName + "Flip"));
			}
  	}

    public void startAnimations(){
        for(GameAnimation animation : animations){
            animation.start();
        }
    }

    public void updateAnimations(){
        for(GameAnimation animation : animations){
            animation.update();
        }
    }

    public static GameAnimation loadAnimation(String animName){
        for(GameAnimation animation : animations){
            if(animation.getName().equals(animName)){
                return animation;
            }
        }
        return null;
    }
}

