import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
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

        int offsetX = screenWidth / 2 - Math.round(player.getX()) - TILE_SIZE;
        
        
        offsetX = Math.min(offsetX, 0);
        offsetX = Math.max(offsetX, screenWidth - mapWidthPixels);

       
        g2.setColor(Color.black);
        g2.fillRect(0, 0, screenWidth, screenHeight);
        
        
        bgManager.draw(g2, 1);

        
        int firstTileX = pixelsToTiles(-offsetX);
        int lastTileX = firstTileX + pixelsToTiles(screenWidth) + 1;
        
        for (int y = 0; y < mapHeight; y++) {
            for (int x = firstTileX; x <= lastTileX; x++) {
                Image image = getTile(x, y);
                if (image != null) {
                    g2.drawImage(image,
                        tilesToPixels(x) + offsetX,
                        tilesToPixels(y) + offsetY,
                        64,
                        64,
                        null);
                }
            }
        }

       player.getAnimation().draw(g2, Math.round(player.getX()) + offsetX, Math.round(player.getY()));
       c.getAnimation().draw(g2, Math.round(c.getX()) + offsetX, Math.round(c.getY()));

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
        player.update();

        if (c.collidesWithPlayer()) {
            window.endLevel();
            return;
        }

        if (c.collidesWithPlayer()) {
            window.endLevel();
        }
    }
    public int getOffsetY() {
    return offsetY;
    }
}