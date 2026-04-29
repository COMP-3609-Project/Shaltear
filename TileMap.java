import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.util.LinkedList;

public class TileMap {

    private static final int TILE_SIZE = 64;
    private Image[][] tiles;
    private int screenWidth, screenHeight;
    private int mapWidth, mapHeight;
    private int offsetY;

    private LinkedList sprites;
    private Player player;
    private Collectible c;

    public BackgroundManager bgManager;
    private GameWindow window;
    private Dimension dimension;

    private int aliveCount;

    public TileMap(GameWindow window, int width, int height) {
        this.window = window;
        dimension = window.getSize();

        screenWidth = dimension.width;
        screenHeight = dimension.height;

        mapWidth = width;
        mapHeight = height;

        
        offsetY = screenHeight - tilesToPixels(mapHeight);

        
        bgManager = new BackgroundManager(window, 12);
        
        tiles = new Image[mapWidth][mapHeight];
        sprites = new LinkedList();
 
    }

 
    public void setPlayer(Player player) {
        this.player = player;
        c = new Collectible(window, player);
    }

    public void draw(Graphics2D g2) {
        if (player == null) {
            return;
        }

        int mapWidthPixels = tilesToPixels(mapWidth);

        int tileOffsetX = screenWidth / 2 - Math.round(player.getX()) - TILE_SIZE;
        int tileOffsetY = screenHeight / 2 - Math.round(player.getY()) - TILE_SIZE;

        tileOffsetY = Math.min(0, tileOffsetY);
        tileOffsetY = Math.max(tileOffsetY, screenHeight - tilesToPixels(mapHeight));
        offsetY = tileOffsetY;
        
        tileOffsetX = Math.min(tileOffsetX, 0);
        tileOffsetX = Math.max(tileOffsetX, screenWidth - mapWidthPixels);

       
        g2.setColor(Color.black);
        g2.fillRect(0, 0, screenWidth, screenHeight);
        
        
        bgManager.draw(g2, Player.getLevel());

        
        int firstTileX = pixelsToTiles(-tileOffsetX);
        int lastTileX = firstTileX + pixelsToTiles(screenWidth) + 1;

        int firstTileY = pixelsToTiles(-tileOffsetY);
        int lastTileY = firstTileY + pixelsToTiles(screenHeight) + 1;
        
        for (int y = firstTileY; y <= lastTileY; y++) {
            for (int x = firstTileX; x <= lastTileX; x++) {
                Image image = getTile(x, y);
                if (image != null) {
                    g2.drawImage(image,
                        tilesToPixels(x) + tileOffsetX,
                        tilesToPixels(y) + tileOffsetY,
                        TILE_SIZE,
                        TILE_SIZE,
                        null);
                }
            }
        }
        for (Object s : sprites) {
            
        if (s instanceof Enemy) {
        Enemy e = (Enemy) s;
        if (e.isActive()) { 
            GameAnimation anim = e.getAnimation();
            
            if (anim != null) {
                anim.draw(g2, e.getX() + tileOffsetX, e.getY(), TILE_SIZE, TILE_SIZE);
            }
           
        } /*else{
                g2.setColor(Color.RED);
                g2.fillRect(e.getX() + tileOffsetX, e.getY(), 64, 64);
        } */
    }
}
       g2.setColor(Color.WHITE);
       g2.drawString("Skeletons Remaining: " + aliveCount, 20, 20);
       player.getAnimation().draw(g2, Math.round(player.getX()) + tileOffsetX, Math.round(player.getY()), TILE_SIZE, TILE_SIZE);
       c.getAnimation().draw(g2, Math.round(c.getX()) + tileOffsetX, Math.round(c.getY()), TILE_SIZE/2, TILE_SIZE/2);

    }

    public void addEnemy(int x, int y, int patrolWidth) {
    Enemy enemy = new Enemy(window, player, this, bgManager);
    
    Point p0 = new Point(x, y);
    Point p1 = new Point(x + (patrolWidth / 2), y);
    Point p2 = new Point(x + patrolWidth, y);
    
    enemy.setMovementPoints(p0, p1, p2);
    enemy.activate();
    sprites.add(enemy);
}


    public void spawnEnemies() {
    addEnemy(200, 950, 100);  
    addEnemy(800, 950, 200);  
    addEnemy(1500, 700, 300);
    addEnemy(1300, 950, 500);
    addEnemy(2100, 950, 450);
    addEnemy(2300, 700, 350);
    addEnemy(2800, 950, 400);
    }

    public LinkedList getSprites() {
        return sprites;
    }

    public Image getTile(int x, int y) {
        if (x < 0 || x >= mapWidth || y < 0 || y >= mapHeight) return null;
        return tiles[x][y];
    }

    public void setTile(int x, int y, Image tile) {
        tiles[x][y] = tile;
    }

    public static int pixelsToTiles(float pixels) {
        return (int)Math.floor(pixels / TILE_SIZE);
    }

    public static int tilesToPixels(int numTiles) {
        return numTiles * TILE_SIZE;
    }

    public int getWidth() { return mapWidth; }
    public int getHeight() { return mapHeight; }
    public int getWidthPixels() { return tilesToPixels(mapWidth); }

   
    public void moveLeft() {}
    public void moveRight() {}
    public void jump() {}
    public void update() {
        // player.update();
        aliveCount = 0;

        for (Object s : sprites) {
            if (s instanceof Enemy) {
                Enemy e = (Enemy) s;
                e.update();
            
                if (!e.isDead()) {
                aliveCount++;
                }
        }
    }

    if (aliveCount == 0) {
        if (c.collidesWithPlayer()) {
            window.endLevel();
        }
    }
}

    public int getOffsetY() {
    return offsetY;
    }
}