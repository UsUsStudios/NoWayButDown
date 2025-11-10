package com.ususstudios.noway.entity;

import com.ususstudios.noway.main.Game;
import com.ususstudios.noway.rendering.Image;

import java.awt.*;

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
	public float width = 16;
	public float height = 16;
	
	// Updating
	/// Can the entity update while not being on the screen? If it's true, the {@code} onScreen} field will always be set to true inside the main update loop of the entity.
	public boolean updateOffScreen = false;
	/// Pretty self-explanatory. It's used to increase performance by not loading the entity while it's off-screen.
	public boolean onScreen = false;
	
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
	 * @param graphics What graphics will the entity be drawn with?
	 * I don't want to explain what graphics are, look at the Java class if you want to know - it has good documentation.
	 **/
	public void draw(Graphics graphics) {
		if (onScreen) {
            /* If spriteSheets are disabled (spriteColumn = 100, spriteRow = 100), draw the currentImage normally.
             If not, use spriteColumn and spriteRow to figure out what part to draw. */
			if (spriteColumn == 100 && spriteRow == 100) {
				graphics.drawImage(currentImage.getImage(), Math.round(x - Game.player.cameraX + Game.screenWidth / 2f),
						Math.round(y - Game.player.cameraY + Game.screenHeight / 2f), null);
			} else {
				graphics.drawImage(currentImage.getImage(),
						// Destination rectangle (on screen)
						Math.round(x - Game.player.cameraX + Game.screenWidth / 2f),
						Math.round(y - Game.player.cameraY + Game.screenHeight / 2f),
						Math.round(x - Game.player.cameraX + Game.screenWidth / 2f + Game.tileSize),
						Math.round(y - Game.player.cameraY + Game.screenHeight / 2f + Game.tileSize),
						
						// Source rectangle (from a sprite sheet)
						Game.tileSize * spriteColumn,
						Game.tileSize * spriteRow,
						Game.tileSize * spriteColumn + Game.tileSize,
						Game.tileSize * spriteRow + Game.tileSize,
						null);
			}
		}
	}
	
	/** Called in the main update loop to update the entity.
	 * The entity must be in a certain Hashmap in Main.game (depending on their type) to be updated.
	 * For instance, a normal entity would have to be in the {@code} entities} array list to update.
	 * Remove the entity from the array list if you stopped using it to stop updating it.
	 * <p>
	 * For performance, everything is behind an if statement with {@code} onScreen} to only update when the entity is on screen.
	 * Use {@code} updateOffScreen} if you want to disable this.
	 **/
	public void update() {
		// Check if the entity is on the screen using the player's camera position
		onScreen = x + Game.tileSize > Game.player.cameraX + Game.screenWidth / 2f &&
				x - Game.tileSize < Game.player.cameraX + Game.screenWidth / 2f &&
				y + Game.tileSize > Game.player.cameraY + Game.screenHeight / 2f &&
				y - Game.tileSize < Game.player.cameraY + Game.screenHeight / 2f || updateOffScreen;
	}
}
