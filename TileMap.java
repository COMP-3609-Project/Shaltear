import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class TileMap {

    private static final int TILE_SIZE = 64;
    private Image[][] tiles;
    private int screenWidth, screenHeight;
    private int mapWidth, mapHeight;
    private int offsetY;

    private LinkedList<Enemy> sprites;
    private LinkedList<Boss> bosslist;
    private Player player;
    private Collectible c;

    public BackgroundManager bgManager;
    private GameWindow window;
    private Dimension dimension;

    private int aliveCount;
    private int collected = 0;

    private ArrayList<Collectible> collectibles;

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
        sprites = new LinkedList<>();
        bosslist = new LinkedList<>();
        collectibles = new ArrayList<>();
        
    }

    // Add enemy at specific tile position with patrol width
    public void addEnemyAt(int tileX, int tileY, int patrolWidth) {
        int pixelX = tilesToPixels(tileX);
        int pixelY = tilesToPixels(tileY) + offsetY;
        Enemy enemy = new Enemy(window, player, this, bgManager);
        enemy.setMovementPoints(
            new Point(pixelX, pixelY),
            new Point(pixelX + patrolWidth / 2, pixelY),
            new Point(pixelX + patrolWidth, pixelY)
        );
        enemy.activate();
        sprites.add(enemy);
    }

    public void addCollectibleAt(int tileX, int tileY) {
        int pixelX = tilesToPixels(tileX);
        int pixelY = tilesToPixels(tileY) + offsetY;
        Collectible collectible = new Collectible(window, player, pixelX, pixelY);
        collectibles.add(collectible);
    }

    public void addBossAt(int tileX, int tileY) {
        int pixelX = tilesToPixels(tileX);
        int pixelY = tilesToPixels(tileY) + offsetY;
        Boss boss = new Boss(window, player, this, bgManager);
        boss.setX(pixelX);
        boss.setY(pixelY);
        bosslist.add(boss);
    }

    public Collectible getCurrentCollectible() {
        if (!collectibles.isEmpty()) {
            return c;
        }
        return null;
    }
 
    public void setPlayer(Player player) {
        this.player = player;
        for(Collectible collectible : collectibles) {
            collectible.setPlayer(player);
        }
        for(Enemy enemy : sprites) {
            enemy.setPlayer(player);
        }
        for(Boss boss : bosslist) {
            boss.setPlayer(player);
        }
        if(window.getLevel()==2){ c = collectibles.get(new Random().nextInt((14-0) + 1) + 0);}
        
    }

    public void draw(Graphics2D g2) {
        if (player == null) {
            return;
        }

        int mapWidthPixels = tilesToPixels(mapWidth);

        int tileOffsetX = screenWidth / 2 - Math.round(player.getX()) - TILE_SIZE;
        
        tileOffsetX = Math.min(tileOffsetX, 0);
        tileOffsetX = Math.max(tileOffsetX, screenWidth - mapWidthPixels);

       
        g2.setColor(Color.black);
        g2.fillRect(0, 0, screenWidth, screenHeight);
        
        if(window.getLevel()<=3){
            bgManager.draw(g2, window.getLevel());
        }

        
        int firstTileX = pixelsToTiles(-tileOffsetX);
        int lastTileX = firstTileX + pixelsToTiles(screenWidth) + 1;

        int firstTileY = pixelsToTiles(-offsetY);
        int lastTileY = firstTileY + pixelsToTiles(screenHeight) + 1;
        
        for (int y = firstTileY; y <= lastTileY; y++) {
            for (int x = firstTileX; x <= lastTileX; x++) {
                Image image = getTile(x, y);
                if (image != null) {
                    g2.drawImage(image,
                        tilesToPixels(x) + tileOffsetX,
                        tilesToPixels(y) + offsetY,
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
                        anim.draw(g2, Math.round(e.getX()) + tileOffsetX, Math.round(e.getY()), TILE_SIZE, TILE_SIZE);
                    }
                
                }
            }
        }

        for(Boss b : bosslist) {
            if (b.isDead() == false) { 
                GameAnimation anim = b.getAnimation();
                if (anim != null) {
                    anim.draw(g2, Math.round(b.getX()) + tileOffsetX, Math.round(b.getY()), 192, 192);
                    b.drawProjectiles(g2, tileOffsetX);
                }
            }
        }

       g2.setColor(Color.WHITE);
       player.getAnimation().draw(g2, Math.round(player.getX()) + tileOffsetX, Math.round(player.getY()), TILE_SIZE, TILE_SIZE);
       player.drawProjectiles(g2, tileOffsetX);
       
        switch (window.getLevel()) {
            case 1 -> {
                g2.setColor(Color.RED);
                g2.setFont(new Font("Arial", Font.BOLD, 20));
                g2.drawString("Skeletons Remaining: " + aliveCount, 20, 20);
                g2.setColor(Color.BLUE);
                g2.setFont(new Font("Arial", Font.BOLD, 30));
                g2.drawString("LIVES: " + player.getLives(), 50, 50);
            }
            case 2 -> {
                g2.setColor(Color.BLUE);
                g2.setFont(new Font("Arial", Font.BOLD, 30));
                g2.drawString("Bats Collected: " + collected + "/15", 50, 50);
                Collectible collectible = getCurrentCollectible();
                if (collectible != null) {
                    collectible.getAnimation().draw(g2, Math.round(collectible.getX()) + tileOffsetX, Math.round(collectible.getY()), 48, 48);
                }
            }
            case 3 -> {
                g2.setColor(Color.RED);
                g2.setFont(new Font("Arial", Font.BOLD, 20));
                g2.drawString("Boss Health: " + bosslist.get(0).getHealth(), 20, 20);
                g2.setColor(Color.BLUE);
                g2.setFont(new Font("Arial", Font.BOLD, 30));
                g2.drawString("LIVES: " + player.getLives(), 50, 50);
            }
        }
       
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

    for(Boss b : bosslist) {
        b.update();
    }

    if (aliveCount == 0 && window.getLevel() == 1) {
        window.endLevel();
    }

    if(getCurrentCollectible() != null && getCurrentCollectible().collidesWithPlayer()){
        SoundManager.getInstance().playSound("bat", false);
        collected++;
        if(collected<15){
            c = collectibles.get(new Random().nextInt((14-0) + 1) + 0);
        } else {
            window.endLevel();
        }
    }
}
    public boolean isBossDefeated() {
    if (bosslist == null || bosslist.isEmpty()) return false;
    
    for (Boss b : bosslist) {
      
        if (!b.isDead()) {
            return false;
        }
    }
    return true;
    }

    public int getOffsetY() {
    return offsetY;
    }

    public LinkedList<Boss> getBossList() {
        return bosslist;
    }
}