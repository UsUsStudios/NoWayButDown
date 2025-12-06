package com.ususstudios.noway.objects.custom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.ususstudios.noway.Main;
import com.ususstudios.noway.main.States;
import com.ususstudios.noway.objects.Mob;
import com.ususstudios.noway.rendering.Image;
import com.ususstudios.noway.rendering.Map;
import com.ususstudios.noway.rendering.MapTileHandler;

public class Player extends Mob {
	public float cameraX, cameraY;

	public Player() {
		super("Player", 0f, 0f);

		// Adjust spriteSheet properties
		spriteSheet = Image.loadImage("entity/player/player");
		spriteRow = 0;
		spriteColumn = 0;
        scaleX = 1;
        scaleY = 2;
		spriteSheet.scaleImage(Main.tileSize * 4, Main.tileSize * 5);
		currentImage = spriteSheet;

		// Set some properties
        animationSpeed = 10;
        animationFrames = 4;
		speed = 150;
        colX = 10f;
        colY = 10f;
        width = 30;
        height = 80;
        mainRow = 1;
        mainCol = 0;
		updateOffScreen = true;
        properties.put("light_radius", 125f);
        properties.put("light_intensity", 0.8f);

        /* Set onScreen to true, so the player can be drawn
        Since the super class's update method isn't called, and the player is always on Screen, it doesn't update to false*/
		onScreen = true;

		Main.LOGGER.info("Player loaded");
	}

    @Override
	public void setPosition(float setX, float setY) {
		super.setPosition(setX, setY);
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
    }
}
