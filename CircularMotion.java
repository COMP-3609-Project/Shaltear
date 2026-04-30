public class CircularMotion {
    
   private double angle;
   private double speed;
   private int radius;
   private int centerX, centerY;

   private int x, y;

   public CircularMotion(int centerX, int centerY, int radius, double speed) {
       this.centerX = centerX;
       this.centerY = centerY;
       this.radius = radius;
       this.speed = speed;
       this.angle = 0; 
   }
   public void update() {
       angle += speed;
       if (angle >= 360) {
           angle -= 360; 
       }
       x = centerX + (int)(radius * Math.cos(Math.toRadians(angle)));
       y = centerY + (int)(radius * Math.sin(Math.toRadians(angle)));
   }

   public int getX() {
       return x;
   }
   public int getY() {
         return y;
   }
   public void setCenter(int centerX, int centerY) {
        this.centerX = centerX;
        this.centerY = centerY;
   }
}