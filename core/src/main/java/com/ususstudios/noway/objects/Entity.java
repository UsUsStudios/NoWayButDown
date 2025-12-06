package com.ususstudios.noway.objects;

import com.badlogic.gdx.graphics.Texture;
import com.ususstudios.noway.Main;
import com.ususstudios.noway.rendering.Image;

/** Extend this class to create an Entity.
 * Think of an Entity like a simple object in a map that is rendered separately from tiles.
 * This "object" can be modified however you want, and you can make it do whatever you want.
 * You can change its position, make it have different states, ect...
 * There are many extensions of this class such as mobs that, well, extend the possibilities of objects.
 * Mobs, for instance, are living beings that can walk around and talk.
 **/
public class Entity extends GameObject {
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

	/** Called in the main draw method to draw the objects.
	 * The objects must be in a certain Hashmap in Main.game (depending on their type) to be drawn.
	 * For instance, a normal objects would have to be in the {@code} objects} array list to draw.
	 * Remove the objects from the array list if you stopped using it to stop drawing it.
	 * <p>
	 * For performance, everything is behind an if statement with {@code} onScreen} to only draw when the objects is on screen.
	 * Use {@code} updateOffScreen} if you want to disable this.
	 * <p>
	 **/
    @Override
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
    }
}
