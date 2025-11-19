package com.ususstudios.noway.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.ususstudios.noway.Main;
import com.ususstudios.noway.main.States;
import com.ususstudios.noway.rendering.Image;
import com.ususstudios.noway.rendering.Map;
import com.ususstudios.noway.rendering.MapTileHandler;

public class Player extends Mob {
	public Image eyesSheet;
	public int eyesColumn = 0;
	public int eyesRow = 0;
	private boolean blinking = false;

	public float cameraX, cameraY;

	public Player() {
		super("Player", 0f, 0f, 36f, 48f);

		// Ajust spriteSheet properties
		spriteSheet = Image.loadImage("entity/player/torgray_sheet");
		spriteRow = 0;
		spriteColumn = 0;
		spriteSheet.scaleImage(Main.tileSize * 3, Main.tileSize * 4);
		currentImage = spriteSheet;

		// Load the eye sheet
		eyesSheet = Image.loadImage("entity/eyes_sheet");
		eyesSheet.scaleImage(Main.tileSize * 5, Main.tileSize * 2);

		// Add counters
		counters.put("eyes_idle", 0);
		counters.put("eyes_blink", 0);

		// Set some properties
		speed = 4;
		updateOffScreen = true;
        properties.put("light_radius", 125f);
        properties.put("light_intensity", 0.8f);

        /* Set onScreen to true, so the player can be drawn
        Since the super class's update method isn't called, and the player is always on Screen, it doesn't update to false*/
		onScreen = true;

		Main.LOGGER.info("Player loaded");
	}

	public void setPosition(float setX, float setY) {
		x = Main.tileSize * setX;
		y = Main.tileSize * setY;
		cameraX = x;
		cameraY = y;
	}

	@Override
	public void update() {
		// if (Main.random.nextFloat() > 0.5) properties.put("light_intensity", 0.8f * ((Main.Main.random.nextFloat() - 0.5f) / 5f + 1));
		StringBuilder newDirection = new StringBuilder();

        /* Depending on the key pressed, append a newDirection with a direction.
         * If the direction was appended more than once, append the direction with a space
         this is to let the mob's update method know if the movement is diagonal */
		if (Gdx.input.isKeyPressed(Input.Keys.W)) newDirection.append("up");
		if (Gdx.input.isKeyPressed(Input.Keys.S)) newDirection.append(!newDirection.isEmpty() ? "" : "down");
		if (Gdx.input.isKeyPressed(Input.Keys.A)) newDirection.append(!newDirection.isEmpty() ? " left" : "left");
		if (Gdx.input.isKeyPressed(Input.Keys.D)) newDirection.append(!newDirection.isEmpty() ? " right" : "right");

		// If nothing was added to the StringBuilder, meaning the player isn't walking, change his state accordingly
		if (newDirection.isEmpty()) state = States.MobStates.IDLE;
		else state = States.MobStates.WALKING;

		// Set the direction to the final newDirection string and let the mod's update method do the rest
		direction = newDirection.toString().trim();
		super.update();

		// Modify the screenX and screenY depending on the size of the window
		cameraX -= (cameraX - x) * 0.15f;
		cameraY -= (cameraY - y) * 0.15f;

		// Clamp the camera to the map bounds
		Map map = MapTileHandler.maps.get(Main.currentMap);
		int maxCameraX = map.width() * Main.tileSize - Main.screenWidth / 2;
		int maxCameraY = map.height() * Main.tileSize - Main.screenHeight / 2;
		if (cameraX < Main.screenWidth / 2f) cameraX = Main.screenWidth / 2f;
		if (cameraY < Main.screenHeight / 2f) cameraY = Main.screenHeight / 2f;
		if (cameraX > maxCameraX) cameraX = maxCameraX;
		if (cameraY > maxCameraY) cameraY = maxCameraY;
	}

	@Override
	public void draw() {
		super.draw();

		// Set which eyes
		if (state == States.MobStates.IDLE) {
			// Update the sprite counter
			counters.put("eyes_idle", counters.get("eyes_idle") + 1);

			// If the counter hits the goal, change the position of the eyes
			if (counters.get("eyes_idle") >= animationSpeed * 12) {
				// To show Torgray is bored, his eyes will look around in this sequence:
				switch (eyesColumn) {
					case 0 -> eyesColumn = 1; // Look left
					case 1 -> eyesColumn = 2; // Look right
					case 2 -> eyesColumn = 0; // Look back
				}

				// Reset The counter
				counters.put("eyes_idle", 0);
			}
		} else if (state == States.MobStates.WALKING) {
			// Reset the eye counter
			counters.put("eyes_idle", 0);

			// Based on the current displayed sprite, change eyes
			eyesColumn = switch (spriteRow) {
				case 2 -> 1;
				case 3 -> 2;
				default -> 0;
			};
		}

		// Make the eyes eyes eyes blink
		// Update the sprite counter
		counters.put("eyes_blink", counters.get("eyes_blink") + 1);

		// If the counter hits the goal, and it's a high one meaning we're not blinking, make us blink
		if (counters.get("eyes_blink") >= animationSpeed * 15) {
			// Change the row to the blinking row
			eyesRow = 1;

			// Reset the counter
			counters.put("eyes_blink", 0);
		} else if (!blinking && eyesRow == 1 && counters.get("eyes_blink") >= animationSpeed / 2) {
			// If it hits the lower goal, and we are in the process of blinking, close our eyes (set to non-existent sprite)
			eyesRow = 2;

			// Reset the counter
			counters.put("eyes_blink", 0);
			blinking = true;
		} else if (eyesRow == 1 && counters.get("eyes_blink") >= animationSpeed / 2) {
			// If it hits the lower goal, and we are in the process of blinking (and almost done), re-open our eyes
			eyesRow = 0;

			// Reset the counter and blinking state
			counters.put("eyes_blink", 0);
			blinking = false;
		} else if (eyesRow == 2 & counters.get("eyes_blink") >= animationSpeed / 2) {
			// If it hits the lower goal, and we have our eyes closed, re-open our eyes
			eyesRow = 1;

			// Reset the counter
			counters.put("eyes_blink", 0);
		}

		// Draw the eyes (as long as the player isn't facing backward)
		if (spriteRow != 0) {
            float screenX = x - Main.player.cameraX + Main.screenWidth / 2f;
            float screenY = y - Main.player.cameraY + Main.screenHeight / 2f;

            // Draw a subregion from eyes sprite sheet
            Texture texture = eyesSheet.getTexture();
            int srcX = Main.tileSize * eyesColumn;
            int srcY = Main.tileSize * eyesRow;

            Main.batch.draw(texture,
                Math.round(screenX),        // dest x
                Math.round(screenY),        // dest y
                Main.tileSize,              // dest width
                Main.tileSize,              // dest height
                srcX,                       // src x
                srcY,                       // src y
                Main.tileSize,              // src width
                Main.tileSize,              // src height
                false,                      // flipX
                false);                     // flipY
		}
	}
}
