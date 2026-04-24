import java.io.File;
import java.util.ArrayList;
import javax.swing.JFrame;


public class AnimationManager {

	String pathName = "images/animations";

	private String animNames[] = new String[50];

    ArrayList<GameAnimation> animations;

  	private JFrame window;			// JFrame on which backgrounds are drawn

  	public AnimationManager(JFrame window) {
    		this.window = window;

			File folder = new File(pathName);
			File[] files = folder.listFiles();
            animations = new ArrayList<>();
			for (File file : files) {
                animations.add(new GameAnimation(file.getName()));
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

    public GameAnimation loadAnimation(String animName){
        for(GameAnimation animation : animations){
            if(animation.getName().equals(animName)){
                return animation;
            }
        }
        return null;
    }
}

