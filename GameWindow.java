import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.*;    

public class GameWindow extends JFrame implements
                Runnable,
                KeyListener,
                MouseListener,
                MouseMotionListener
{
    private static final int NUM_BUFFERS = 2;    

    private int pWidth, pHeight;             
    private Thread gameThread = null;                
    private volatile boolean isRunning = false;        

    private ImageEffect imageEffect;        
    private Player player; 

    private boolean levelChange;
    private int level;
    private boolean gameOver;

    private BufferedImage image;            
    private Image quit1Image;            
    private Image quit2Image;            

    private boolean finishedOff = false;        
    private volatile boolean isOverQuitButton = false;
    private Rectangle quitButtonArea;        

    private volatile boolean isOverPauseButton = false;
    private Rectangle pauseButtonArea;        
    private volatile boolean isPaused = false;

    private volatile boolean isOverStopButton = false;
    private Rectangle stopButtonArea;        
    private volatile boolean isStopped = false;
   
    private GraphicsDevice device;             
    private Graphics gScr;
    private BufferStrategy bufferStrategy;

    private SoundManager soundManager;
    TileMapManager tileManager;
    TileMap    tileMap;
	private AnimationManager animManager;

    public GameWindow() {
        super("Tiled Bat and Ball Game: Full Screen Exclusive Mode");

        initFullScreen();

        quit1Image = ImageManager.loadImage("images/Quit1.png");
        quit2Image = ImageManager.loadImage("images/Quit2.png");

        setButtonAreas();

        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

        animManager = new AnimationManager(this);
        soundManager = SoundManager.getInstance();
        image = new BufferedImage (pWidth, pHeight, BufferedImage.TYPE_INT_RGB);

        startGame();
    }

    @Override
    public void run () {
        try {
            isRunning = true;
            while (isRunning) {
                if (!isPaused && !gameOver) {
                    gameUpdate();
                }
                screenUpdate();
                Thread.sleep (30);
            }
        }
        catch(InterruptedException e) {}
        finishOff();
    }

    public void gameUpdate () {
        if (tileMap != null) {
            tileMap.update();
        }

        
        if (player != null && !isPaused) {
            player.handleMovement(); 
            player.update();         
        }

        if (!isPaused)
            animManager.updateAnimations();
        
        if (player.getLives() <= 0) {
            gameOver = true;
            isRunning = false; // Or trigger a specific Game Over screen
        }
        
        imageEffect.update();
        
        if (levelChange) {
            levelChange = false;
            tileManager = new TileMapManager (this);

            try {
                String filename = "maps/map" + level + ".txt";
                SoundManager.getInstance().stopSound("background" + (level - 1));
                SoundManager.getInstance().playSound("background" + level, true);
                tileMap = tileManager.loadMap(filename) ;
                
                // Re-initialize player for new level if needed
                player = new Player(this, tileMap, new BackgroundManager(this, 8));
                tileMap.setPlayer(player);
            } catch (IOException e) {
                gameOver = true;
            }
        }
    }

    private void screenUpdate() { 
        do {
            do {
                gScr = bufferStrategy.getDrawGraphics();
                gameRender(gScr);
                gScr.dispose();
            } while (bufferStrategy.contentsRestored());
            bufferStrategy.show();
        } while (bufferStrategy.contentsLost());
        Toolkit.getDefaultToolkit().sync();
    }

    public void gameRender(Graphics gScr) {
        Graphics2D g2 = (Graphics2D) gScr;
        
        if (tileMap != null) {
            tileMap.draw(g2);
        }

		if (gameOver) {
			Color darken = new Color (0, 0, 0, 125);
			g2.setColor (darken);
			g2.fill (new Rectangle2D.Double (0, 0, this.getWidth(), this.getHeight()));
		}


        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 24));
        g2.drawString("LIVES: " + player.getLives(), 50, 50);
        
        imageEffect.draw(g2);
        drawButtons(g2);
    }

    private void startGame() {
        level = 1;
        levelChange = false;

        if (gameThread == null) {
            tileManager = new TileMapManager (this);

            try {
                tileMap = tileManager.loadMap("maps/map1.txt");
                SoundManager.getInstance().playSound("background1", true);
                player = new Player(this, tileMap, tileMap.bgManager);
                tileMap.setPlayer(player);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            }

            // tileMap.spawnEnemies();

            imageEffect = new ImageEffect (this);
            gameThread = new Thread(this);
            gameThread.start();
            animManager.startAnimations();
        }
    }

    public void endLevel() {
		level = level + 1;
		levelChange = true;
	}

    // --- INPUT HANDLING ---

    @Override
    public void keyPressed (KeyEvent e) {
        if (isPaused || player == null) return;

        int keyCode = e.getKeyCode();

        if ((keyCode == KeyEvent.VK_ESCAPE) || (keyCode == KeyEvent.VK_Q)) {
            isRunning = false;
            return;
        }

        
        if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) {
            player.setKey(1, true);
        }
        if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) {
            player.setKey(2, true);
        }
        if (keyCode == KeyEvent.VK_SPACE || keyCode == KeyEvent.VK_W) {
            player.setKey(3, true);
        }
    }

    @Override
    public void keyReleased (KeyEvent e) {
        if (player == null) return;
        
        int keyCode = e.getKeyCode();

        
        if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) {
            player.setKey(1, false);
        }
        if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) {
            player.setKey(2, false);
        }
        if (keyCode == KeyEvent.VK_SPACE || keyCode == KeyEvent.VK_W) {
            player.setKey(3, false);
        }
        if (keyCode == KeyEvent.VK_F) { // Press F to attack
            player.attack();
        }
    }

    @Override
    public void keyTyped (KeyEvent e) {}

   
    private void setButtonAreas() {
        int leftOffset = (pWidth - (5 * 150) - (4 * 20)) / 2;
        pauseButtonArea = new Rectangle(leftOffset, 60, 150, 40);
        leftOffset += 170;
        stopButtonArea = new Rectangle(leftOffset, 60, 150, 40);
        leftOffset += 170;
        quitButtonArea = new Rectangle(leftOffset, 55, 180, 50);
    }

    private void drawButtons (Graphics g) {
        Font oldFont = g.getFont();
        g.setFont(new Font ("TimesRoman", Font.ITALIC + Font.BOLD, 18));
        g.setColor(Color.BLACK);
        
        // Draw Pause Button
        g.drawOval(pauseButtonArea.x, pauseButtonArea.y, pauseButtonArea.width, pauseButtonArea.height);
        g.setColor(isOverPauseButton && !isStopped ? Color.WHITE : Color.RED);
        g.drawString(isPaused && !isStopped ? "Paused" : "Pause", pauseButtonArea.x+45, pauseButtonArea.y+25);

        // Draw Quit Button
        if (isOverQuitButton)
           g.drawImage(quit1Image, quitButtonArea.x, quitButtonArea.y, 180, 50, null);
        else
           g.drawImage(quit2Image, quitButtonArea.x, quitButtonArea.y, 180, 50, null);

        g.setFont(oldFont);
    }

    private void testMousePress(int x, int y) {
        if (isStopped && !isOverQuitButton) return;
        if (isOverStopButton) { isStopped = true; isPaused = false; }
        else if (isOverPauseButton) { isPaused = !isPaused; }
        else if (isOverQuitButton) { isRunning = false; }
    }

    private void testMouseMove(int x, int y) {
        if (isRunning) {
            isOverPauseButton = pauseButtonArea.contains(x,y);
            isOverStopButton = stopButtonArea.contains(x,y);
            isOverQuitButton = quitButtonArea.contains(x,y);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) { testMousePress(e.getX(), e.getY()); }
    @Override
    public void mouseMoved(MouseEvent e) { testMouseMove(e.getX(), e.getY()); }
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseDragged(MouseEvent e) {}

    private void finishOff() { 
        if (!finishedOff) {
            finishedOff = true;
            restoreScreen();
            System.exit(0);
        }
    }

    private void restoreScreen() { 
        Window w = device.getFullScreenWindow();
        if (w != null) w.dispose();
        device.setFullScreenWindow(null);
    }

    private void initFullScreen() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        device = ge.getDefaultScreenDevice();
        setUndecorated(true);
        setIgnoreRepaint(true);
        setResizable(false);
        device.setFullScreenWindow(this);
        pWidth = getBounds().width;
        pHeight = getBounds().height;
        setVisible(true);
        createBufferStrategy(NUM_BUFFERS);
        bufferStrategy = getBufferStrategy();
    }
}