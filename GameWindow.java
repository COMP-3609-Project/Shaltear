import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
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

    private Player player; 

    private boolean levelChange;
    private int level;
    private boolean gameOver;           

    private boolean finishedOff = false;        
    private volatile boolean isOverQuitButton = false;
    private Rectangle quitButtonArea;        

    private volatile boolean isOverPauseButton = false;
    private Rectangle pauseButtonArea;        
    private volatile boolean isPaused = false;

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
        setButtonAreas();

        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

        animManager = new AnimationManager(this);
        soundManager = SoundManager.getInstance();

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
            isRunning = false; 
        }

        if (levelChange) {
            levelChange = false;
            tileManager = new TileMapManager (this);

            try {
                String filename = "maps/map" + level + ".txt";
                SoundManager.getInstance().stopSound("background" + (level - 1));
                SoundManager.getInstance().playSound("background" + level, true);
                tileMap = tileManager.loadMap(filename) ;
                
                player = new Player(this, tileMap, new BackgroundManager(this, 8));
                tileMap.setPlayer(player);
            } catch (IOException e) {
                SoundManager.getInstance().stopSound("background3");
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
			g2.fill (new Rectangle2D.Double (0, 0, this.getWidth()/4, this.getHeight()/4));
            g2.drawString("GAME OVER", getWidth()/4, getHeight()/4);
		}

        drawButtons(g2);
    }

    private void startGame() {
        level = 1;
        levelChange = false;

        if (gameThread == null) {
            tileManager = new TileMapManager (this);

            try {
                tileMap = tileManager.loadMap("maps/map3.txt");
                level = 3;
                SoundManager.getInstance().playSound("background1", true);
                player = new Player(this, tileMap, tileMap.bgManager);
                tileMap.setPlayer(player);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            }

            gameThread = new Thread(this);
            gameThread.start();
            animManager.startAnimations();
        }
    }

    public void endLevel() {
        if(level==3){
            gameOver = true;
            return;
        }
		level = level + 1;
		levelChange = true;
	}

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
        if(keyCode == KeyEvent.VK_F){
            player.attack();
        }
        if(keyCode == KeyEvent.VK_M){
            player.projecTileActivate();
        }
    }

    @Override
    public void keyReleased (KeyEvent e) { //Controls
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
        if (keyCode == KeyEvent.VK_F) { 
            player.attack();
        }
        if(keyCode == KeyEvent.VK_M){
            player.projecTileActivate();
        }
    }

    @Override
    public void keyTyped (KeyEvent e) {}

   
    private void setButtonAreas() {
        int btnW = 160;
        int btnH = 45;
        int spacing = 20;
        
        int startX = pWidth - (btnW * 3) - (spacing * 3); 
        int y = 30;

        pauseButtonArea = new Rectangle(startX, y, btnW, btnH);
        quitButtonArea = new Rectangle(startX + (btnW + spacing) * 2, y, btnW, btnH);
    
    }

    private void drawButtons (Graphics2D g2) {
        drawProfessionalButton(g2, pauseButtonArea, isPaused ? "RESUME" : "PAUSE", isOverPauseButton, new Color(52, 70, 129));
        drawProfessionalButton(g2, quitButtonArea, "QUIT", isOverQuitButton, new Color(192, 57, 43)); 
    }

private void drawProfessionalButton(Graphics2D g2, Rectangle r, String text, boolean isHovered, Color baseColor) {

    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
    if (isHovered) {
        g2.setColor(new Color(255, 255, 255, 80));
        g2.fillRoundRect(r.x - 3, r.y - 3, r.width + 6, r.height + 6, 15, 15);
        g2.setColor(baseColor.brighter());
    } else {
        g2.setColor(baseColor);
    }

    g2.fillRoundRect(r.x, r.y, r.width, r.height, 15, 15);
    
    g2.setColor(Color.WHITE);
    g2.setStroke(new BasicStroke(2));
    g2.drawRoundRect(r.x, r.y, r.width, r.height, 15, 15);

    
    g2.setFont(new Font("SansSerif", Font.BOLD, 18));
    FontMetrics metrics = g2.getFontMetrics();
    int tx = r.x + (r.width - metrics.stringWidth(text)) / 2;
    int ty = r.y + ((r.height - metrics.getHeight()) / 2) + metrics.getAscent();
    
    g2.setColor(Color.WHITE);
    g2.drawString(text, tx, ty);

    }

    private void testMousePress(int x, int y) {
    
        if (isOverPauseButton) { isPaused = !isPaused; }
        else if (isOverQuitButton) { isRunning = false; }
    
    }

    private void testMouseMove(int x, int y) {
        if (isRunning) {
            isOverPauseButton = pauseButtonArea.contains(x,y);
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

    public int getLevel(){return level;}
}