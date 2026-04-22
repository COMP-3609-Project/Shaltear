import java.io.File;
import java.util.ArrayList;
import javax.swing.JFrame;


public class AnimationManager {

	String pathName = "images/animations";

	private String animNames[] = new String[50];

    ArrayList<PlayerAnimation> animations;

  	private JFrame window;			// JFrame on which backgrounds are drawn

  	public AnimationManager(JFrame window) {
    		this.window = window;

			File folder = new File(pathName);
			File[] files = folder.listFiles();
            animations = new ArrayList<>();
			for (File file : files) {
                animations.add(new PlayerAnimation(file.getName()));
			}
  	}

    public void startAnimations(){
        for(PlayerAnimation animation : animations){
            animation.start();
        }
    }

    public void updateAnimations(){
        for(PlayerAnimation animation : animations){
            animation.update();
        }
    }

    public PlayerAnimation loadAnimation(String animName){
        for(PlayerAnimation animation : animations){
            if(animation.getName().equals(animName)){
                return animation;
            }
        }
        return null;
    }
}

