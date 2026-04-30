import java.awt.Point;
import javax.swing.JFrame;

public class BezierCurveMotion {

   	private static double START = 0;
   	private static double END = 1.0;
   	private static double INCR = 0.02;

   	private JFrame window;
	private Enemy enemy;

	private boolean active;		// to activate or deactivate motion

	private Point p0;		// first point for drawing curve;
	private Point p1;		// second point for drawing curve;
	private Point p2;		// third point for drawing curve;

	private double t;		// loop curve for t =[0.0, 1.0]
 	private double incr;		// increment for looping t in [0.0, 1.0]
	private boolean forward = true;

	public BezierCurveMotion (JFrame w, Enemy enemy, 
				  Point p0, Point p1, Point p2) {

		this.window = w;
		this.enemy = enemy;
		this.p0 = p0;
		this.p1 = p1;
		this.p2 = p2;

		active = false;		// behaviour is inactive by default

		t = START;
		incr = INCR;
	}

	public boolean isActive() {
		return active;
	}

	public void activate() {
		active = true;
	}
	

	public void deActivate() {
		active = false;
	}

   	public void update () {  

		if (!active || !window.isVisible ()) 
			return;

      		t = t + incr;

      		if (t > END) {
			t = END;
         		incr = INCR * -1.0;
				forward = false;
		}
		else
		if (t < START) {
			t = START;
         		incr = INCR;
				forward = true;
		}

		int x = (int)((1 - t) * (1 - t) * p0.x + 2 * (1 - t) * t * p1.x + t * t * p2.x);

		int y = (int)	((1 - t) * (1 - t) * p0.y +
				 2 * (1 - t) * t * p1.y +
				 t * t * p2.y);

		enemy.setX(x);
		enemy.setY(y);
   }

   public boolean isMovingForward() { return forward; }

}