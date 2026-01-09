package com.ususstudios.noway.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ususstudios.noway.Main;
import com.ususstudios.noway.main.UtilityTool;
import com.ususstudios.noway.entities.custom.Gatekeeper;
import com.ususstudios.noway.entities.custom.Player;
import com.ususstudios.noway.entities.custom.SoundTrigger;
import com.ususstudios.noway.rendering.Image;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Properties;

/** Extend this class to create an Entity.
 * Think of an Entity like a simple object in a map that is rendered separately from tiles.
 * This "object" can be modified however you want, and you can make it do whatever you want.
 * You can change its position, make it have different states, ect...
 * There are many extensions of this class such as mobs that, well, extend the possibilities of objects.
 * Mobs, for instance, are living beings that can walk around and talk.
 **/
public class Entity {
    // This is for storing the names of all the object types, so I don't have to reference the class path in map files
    private static final HashMap<String, Class<? extends Entity>> objectNames = new HashMap<>();
    public Properties properties = new Properties();

    // Positions
    public float x;
    public float y;
    public float colX = 0;
    public float colY = 0;               // The offset where the object's collision rect begins
    public float width = Main.tileSize;  // The size of the object's collision rect
    public float height = Main.tileSize;

    // Updating
    public boolean collision = true;
    /// Can the objects update while not being on the screen? If it's true, the {@code} onScreen} field will always be set to true inside the main update loop of the objects.
    public boolean updateOffScreen = false;
    /// Pretty self-explanatory. It's used to increase performance by not loading the objects while it's off-screen.
    public boolean onScreen = false;

    /// The image that is drawn at the objects's location to represent the objects.
	public Image currentImage = Image.loadImage("disabled");
	/// If there's a sprite sheet, this is the colum where the sprite would be pulled from. Set to -1 to disable it.
	public int spriteColumn = -1;
	/// If there's a sprite sheet, this is the row where the sprite would be pulled from. Set to -1 to disable it.
	public int spriteRow = -1;
    /// How much to scale the image before it's drawn?
    public int scaleX = 1;
    public int scaleY = 1;

	// Other
	public String name;

    public static Entity createGameObject(String name) {
        try {
            return objectNames.get(name).getConstructor().newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            Main.handleException(e);
            return new Entity("", 0, 0);
        }
    }
    public static void registerGameObjectTypes() {
        objectNames.put("Gatekeeper", Gatekeeper.class);
        objectNames.put("Player", Player.class);
        objectNames.put("SoundTrigger", SoundTrigger.class);
    }

	public Entity(String name, float spawnX, float spawnY) {
		this.name = name;
		x = spawnX;
		y = spawnY;
	}

	public Entity(String name, float spawnX, float spawnY, float width, float height) {
		this.name = name;
		x = spawnX;
		y = spawnY;
		this.width = width;
		this.height = height;
	}

    public void setup() {
        if (properties.containsKey("width")) width = (int) properties.get("width");
        if (properties.containsKey("height")) height = (int) properties.get("height");
    }

    public void setPosition(float setX, float setY) {
        x = Main.tileSize * setX;
        y = Main.tileSize * setY;
    }

    public void setPosition(UtilityTool.Tuple<Float, Float> pos) {
        setPosition(pos.x(), pos.y());
    }

	/** Called in the main draw method to draw the entities.
	 * The objects must be in a certain Hashmap in Main.game (depending on their type) to be drawn.
	 * For instance, a normal objects would have to be in the {@code} objects} array list to draw.
	 * Remove the objects from the array list if you stopped using it to stop drawing it.
	 * <p>
	 * For performance, everything is behind an if statement with {@code} onScreen} to only draw when the objects is on screen.
	 * Use {@code} updateOffScreen} if you want to disable this.
	 * <p>
	 **/
    public void draw() {
        if (!onScreen) return;

        float screenX = x - Main.player.cameraX + Main.screenWidth / 2f;
        float screenY = y - Main.player.cameraY + Main.screenHeight / 2f;

        if (spriteColumn == -1 && spriteRow == -1) {
            // Draw full image
            Main.batch.draw(currentImage.getTexture(),
                Math.round(screenX),
                Math.round(screenY));
        } else {
            // Draw a subregion from a sprite sheet
            Texture texture = currentImage.getTexture();
            int srcX = Main.tileSize * spriteColumn;
            int srcY = Main.tileSize * spriteRow;

            Main.batch.draw(texture,
                Math.round(screenX),                    // dest x
                Math.round(screenY),                    // dest y
                Main.tileSize * scaleX,                 // dest width
                Main.tileSize * scaleY,                 // dest height
                srcX,                                   // src x
                texture.getWidth() - srcY,              // src y  I don't know why it's width instead of height...
                                                                  // if it works don't touch it
                Main.tileSize,                          // src width
                Main.tileSize,                          // src height
                false,                                  // flipX
                false);                                 // flipY
        }

        if (Main.debugMode) {
            // Draw hitbox
            Main.batch.end();

            Main.shapes.begin(ShapeRenderer.ShapeType.Line);
            Main.shapes.setColor(Color.RED);
            screenX = x + colX - Main.player.cameraX + Main.screenWidth / 2f;
            screenY = y + colY - Main.player.cameraY + Main.screenHeight / 2f;
            Main.shapes.rect(screenX, Main.screenHeight - screenY - height, width, height);
            Main.shapes.end();

            Main.batch.begin();
        }
    }

    /** Called in the main update loop to update the objects.
     * The objects must be in a certain Hashmap in Main (depending on their type) to be updated.
     * For instance, a normal objects would have to be in the {@code} objects} array list to update.
     * Remove the objects from the array list if you stopped using it to stop updating it.
     * <p>
     * For performance, everything is behind an if statement with {@code} onScreen} to only update when the objects is on screen.
     * Use {@code} updateOffScreen} if you want to disable this.
     **/
    public void update() {
        // Check if the objects is on the screen using the player's camera position
        onScreen = x + width > Main.player.cameraX + Main.screenWidth / 2f &&
            x - width < Main.player.cameraX + Main.screenWidth / 2f &&
            y + height > Main.player.cameraY + Main.screenHeight / 2f &&
            y - height < Main.player.cameraY + Main.screenHeight / 2f || updateOffScreen;
    }
}
