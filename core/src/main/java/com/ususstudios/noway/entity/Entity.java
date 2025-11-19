package com.ususstudios.noway.entity;

import com.badlogic.gdx.graphics.Texture;
import com.ususstudios.noway.Main;
import com.ususstudios.noway.rendering.Image;
import java.awt.*;
import java.util.Properties;

/** Extend this class to create an Entity.
 * Think of an Entity like a simple object in a map that is rendered separately from tiles.
 * This "object" can be modified however you want, and you can make it do whatever you want.
 * You can change its position, make it have different states, ect...
 * There are many extensions of this class such as mobs that, well, extend the possibilities of entities.
 * Mobs, for instance, are living beings that can walk around and talk.
 **/
public class Entity {
	/// The image that is drawn at the entity's location to represent the entity.
	public Image currentImage = Image.loadImage("disabled");
	/// If there's a sprite sheet, this is the colum where the sprite would be pulled from. Set to -1 to disable it.
	public int spriteColumn = -1;
	/// If there's a sprite sheet, this is the row where the sprite would be pulled from. Set to -1 to disable it.
	public int spriteRow = -1;

	// Positions
	public float x;
	public float y;
	public float width = Main.tileSize;
	public float height = Main.tileSize;

	// Updating
	/// Can the entity update while not being on the screen? If it's true, the {@code} onScreen} field will always be set to true inside the main update loop of the entity.
	public boolean updateOffScreen = false;
	/// Pretty self-explanatory. It's used to increase performance by not loading the entity while it's off-screen.
	public boolean onScreen = false;
	public boolean collision = true;

	public Properties properties = new Properties();

	// Other
	public String name;

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

	/** Called in the main draw method to draw the entity.
	 * The entity must be in a certain Hashmap in Main.game (depending on their type) to be drawn.
	 * For instance, a normal entity would have to be in the {@code} entities} array list to draw.
	 * Remove the entity from the array list if you stopped using it to stop drawing it.
	 * <p>
	 * For performance, everything is behind an if statement with {@code} onScreen} to only draw when the entity is on screen.
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
                Math.round(screenX),                   // dest x
                Math.round(screenY),                   // dest y
                Main.tileSize,                          // dest width
                Main.tileSize,                          // dest height
                srcX,                                   // src x
                srcY,                                   // src y
                Main.tileSize,                          // src width
                Main.tileSize,                          // src height
                false,                                  // flipX
                false);                                 // flipY
        }
    }


	/** Called in the main update loop to update the entity.
	 * The entity must be in a certain Hashmap in Main (depending on their type) to be updated.
	 * For instance, a normal entity would have to be in the {@code} entities} array list to update.
	 * Remove the entity from the array list if you stopped using it to stop updating it.
	 * <p>
	 * For performance, everything is behind an if statement with {@code} onScreen} to only update when the entity is on screen.
	 * Use {@code} updateOffScreen} if you want to disable this.
	 **/
	public void update() {
		// Check if the entity is on the screen using the player's camera position
		onScreen = x + Main.tileSize > Main.player.cameraX + Main.screenWidth / 2f &&
				x - Main.tileSize < Main.player.cameraX + Main.screenWidth / 2f &&
				y + Main.tileSize > Main.player.cameraY + Main.screenHeight / 2f &&
				y - Main.tileSize < Main.player.cameraY + Main.screenHeight / 2f || updateOffScreen;
	}
}
