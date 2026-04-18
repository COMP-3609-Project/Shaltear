import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.LinkedList;
import javax.swing.JFrame;

public class TileMap {

    private static final int TILE_SIZE = 64;
    private Image[][] tiles;
    private int screenWidth, screenHeight;
    private int mapWidth, mapHeight;
    private int offsetY;

    private LinkedList sprites;
    private Player player; 

    public BackgroundManager bgManager;
    private JFrame window;
    private Dimension dimension;

    public TileMap(JFrame window, int width, int height) {
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
        
        
        bgManager.draw(g2);

        
        int firstTileX = pixelsToTiles(-offsetX);
        int lastTileX = firstTileX + pixelsToTiles(screenWidth) + 1;
        
        for (int y = 0; y < mapHeight; y++) {
            for (int x = firstTileX; x <= lastTileX; x++) {
                Image image = getTile(x, y);
                if (image != null) {
                    g2.drawImage(image,
                        tilesToPixels(x) + offsetX,
                        tilesToPixels(y) + offsetY,
                        null);
                }
            }
        }

        /*
        g2.drawImage(player.getAnimation(),
            Math.round(player.getX()) + offsetX,
            Math.round(player.getY()),
            null);
        */
       player.getAnimation().draw(g2, Math.round(player.getX()) + offsetX, Math.round(player.getY()));
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
       
    }
    public int getOffsetY() {
    return offsetY;
    }
}