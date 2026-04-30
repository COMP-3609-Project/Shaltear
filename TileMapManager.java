import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;


/**
    The ResourceManager class loads and manages tile Images and
    "host" Sprites used in the game. Game Sprites are cloned from
    "host" Sprites.
*/
public class TileMapManager {

    private ArrayList<Image> tiles1, tiles2, tiles3;
    private int currentMap = 0;

    private GameWindow window;
    private ImageEffect effect;

/*
    private GraphicsConfiguration gc;

    // host sprites used for cloning
    private Sprite playerSprite;
    private Sprite musicSprite;
    private Sprite coinSprite;
    private Sprite goalSprite;
    private Sprite grubSprite;
    private Sprite flySprite;
*/

    public TileMapManager(GameWindow window) {
	    this.window = window;
        effect = new ImageEffect(window);

        loadTileImages();
        //loadCreatureSprites();
        //loadPowerUpSprites();
    }


     public TileMap loadMap(String filename)
        throws IOException
    {
        ArrayList<String> lines = new ArrayList<>();
        int mapWidth = 0;
        int mapHeight = 0;

        // read every line in the text file into the list

        BufferedReader reader = new BufferedReader(
            new FileReader(filename));
        while (true) {
            String line = reader.readLine();
            // no more lines to read
            if (line == null) {
                reader.close();
                break;
            }

            // add every line except for comments
            if (!line.startsWith("#")) {
                lines.add(line);
                mapWidth = Math.max(mapWidth, line.length());
            }
        }

        // parse the lines to create a TileMap
        mapHeight = lines.size();

        TileMap newMap = new TileMap(window, mapWidth, mapHeight);
        for (int y=0; y<mapHeight; y++) {
            String line = lines.get(y);
            for (int x=0; x<line.length(); x++) {
                char ch = line.charAt(x);

                // Check for special symbols first (enemies and collectibles)
                switch (ch) {
                    case '!' -> {
                        newMap.addEnemyAt(x, y, 100);
                    }
                    case '*' -> {
                        newMap.addCollectibleAt(x, y);
                    }
                    default -> {
                        // check if the char represents tile A, B, C etc.
                        int tile = ch - 'A';
                        switch (window.getLevel()) {
                            case 1 -> {
                                if (tile >= 0 && tile < tiles1.size()) {
                                    newMap.setTile(x, y, tiles1.get(tile));
                                }
                            }
                            case 2 -> {
                                if (tile >= 0 && tile < tiles2.size()) {
                                    newMap.setTile(x, y, tiles2.get(tile));
                                }
                            }
                            case 3 -> {
                                if (tile >= 0 && tile < tiles3.size()) {
                                    newMap.setTile(x, y, tiles3.get(tile));
                                }
                            }
                            default -> {
                            }
                        }
                    }
                }
            }
        }

        return newMap;
    }


/*
    private void addSprite(TileMap map,
        Sprite hostSprite, int tileX, int tileY)
    {
        if (hostSprite != null) {
            // clone the sprite from the "host"
            Sprite sprite = (Sprite)hostSprite.clone();

            // center the sprite
            sprite.setX(
                TileMapRenderer.tilesToPixels(tileX) +
                (TileMapRenderer.tilesToPixels(1) -
                sprite.getWidth()) / 2);

            // bottom-justify the sprite
            sprite.setY(
                TileMapRenderer.tilesToPixels(tileY + 1) -
                sprite.getHeight());

            // add it to the map
            map.addSprite(sprite);
        }
    }

*/

    // -----------------------------------------------------------
    // code for loading sprites and images
    // -----------------------------------------------------------


    public void loadTileImages() {
        // keep looking for tile A,B,C, etc. this makes it
        // easy to drop new tiles in the images/ folder

        File file;
        tiles1 = new ArrayList<>();
        tiles2 = new ArrayList<>();
        tiles3 = new ArrayList<>();

        char ch = 'A';
        while (true) {
            String filename = "images/tilemaps/tile_" + ch + ".png";
            file = new File(filename);
            if (!file.exists()) {
                break;
            }else{
                Image tileImage = new ImageIcon(filename).getImage();
                tiles1.add(tileImage);
                tiles2.add(effect.convertImage(filename, 1));
                tiles3.add(effect.convertImage(filename, 2));
                ch++;
            }
        }
    }

/*
    private void addSprite(TileMap map,
        Sprite hostSprite, int tileX, int tileY)
    {
        if (hostSprite != null) {
            // clone the sprite from the "host"
            Sprite sprite = (Sprite)hostSprite.clone();

            // center the sprite
            sprite.setX(
                TileMapRenderer.tilesToPixels(tileX) +
                (TileMapRenderer.tilesToPixels(1) -
                sprite.getWidth()) / 2);

            // bottom-justify the sprite
            sprite.setY(
                TileMapRenderer.tilesToPixels(tileY + 1) -
                sprite.getHeight());

            // add it to the map
            map.addSprite(sprite);
        }
    }

/*
    public void loadCreatureSprites() {

        Image[][] images = new Image[4][];

        // load left-facing images
        images[0] = new Image[] {
            loadImage("player1.png"),
            loadImage("player2.png"),
            loadImage("player3.png"),
            loadImage("fly1.png"),
            loadImage("fly2.png"),
            loadImage("fly3.png"),
            loadImage("grub1.png"),
            loadImage("grub2.png"),
        };

        images[1] = new Image[images[0].length];
        images[2] = new Image[images[0].length];
        images[3] = new Image[images[0].length];
        for (int i=0; i<images[0].length; i++) {
            // right-facing images
            images[1][i] = getMirrorImage(images[0][i]);
            // left-facing "dead" images
            images[2][i] = getFlippedImage(images[0][i]);
            // right-facing "dead" images
            images[3][i] = getFlippedImage(images[1][i]);
        }

        // create creature animations
        Animation[] playerAnim = new Animation[4];
        Animation[] flyAnim = new Animation[4];
        Animation[] grubAnim = new Animation[4];
        for (int i=0; i<4; i++) {
            playerAnim[i] = createPlayerAnim(
                images[i][0], images[i][1], images[i][2]);
            flyAnim[i] = createFlyAnim(
                images[i][3], images[i][4], images[i][5]);
            grubAnim[i] = createGrubAnim(
                images[i][6], images[i][7]);
        }

        // create creature sprites
        playerSprite = new Player(playerAnim[0], playerAnim[1],
            playerAnim[2], playerAnim[3]);
        flySprite = new Fly(flyAnim[0], flyAnim[1],
            flyAnim[2], flyAnim[3]);
        grubSprite = new Grub(grubAnim[0], grubAnim[1],
            grubAnim[2], grubAnim[3]);
System.out.println("loadCreatureSprites successfully executed.");

    }
*/

}
