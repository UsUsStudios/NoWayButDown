package com.ususstudios.noway.systems;

import com.ususstudios.noway.main.*;
import com.ususstudios.noway.rendering.CollisionChecker;
import com.ususstudios.noway.rendering.Map;
import com.ususstudios.noway.rendering.MapTileHandler;
import com.ususstudios.noway.components.*;
import com.ususstudios.noway.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class PlayerSystem implements ECSSystem {
    @Override
    public void process(World world) {
        for (Integer entity : world.query(PlayerComponent.class, PositionComponent.class)) {
            PositionComponent pc = world.getEntityComponent(entity, PositionComponent.class).get();
            PlayerComponent c = world.getEntityComponent(entity, PlayerComponent.class).get();

            StringBuilder newDirection = new StringBuilder();

            /* Depending on the key pressed, append a newDirection with a direction.
             * If the direction was appended more than once, append the direction with a space
             this is to let the mob's update method know if the movement is diagonal */
    		if (Gdx.input.isKeyPressed(Input.Keys.W)) newDirection.append("up");
		    if (Gdx.input.isKeyPressed(Input.Keys.S)) newDirection.append(!newDirection.isEmpty() ? "" : "down");
	    	if (Gdx.input.isKeyPressed(Input.Keys.A)) newDirection.append(!newDirection.isEmpty() ? " left" : "left");
    		if (Gdx.input.isKeyPressed(Input.Keys.D)) newDirection.append(!newDirection.isEmpty() ? " right" : "right");

		    // If nothing was added to the StringBuilder, meaning the player isn't walking, change his state accordingly
	    	if (newDirection.isEmpty()) c.state = States.MobStates.IDLE;
    		else c.state = States.MobStates.WALKING;

		    // Set the direction to the final newDirection string and let the mod's update method do the rest
	    	c.direction = newDirection.toString().trim();
        	if (c.state == States.MobStates.WALKING) {
	    		// Return if the direction is more than 2 words to not confuse the code
		    	String[] directionWords = c.direction.split(" ");
			    if (directionWords.length > 2) return;

                /* If the direction string contains a space, it means it has 2 words and 2 directions.
                 * 2 directions mean that the mob is going diagonally, which means they are going diagonally
                 Diagonal movement makes the mob faster, so we decrease the speed accordingly*/
			    float movementSpeed = c.direction.contains(" ") ? c.speed / 1.4f : c.speed;
                movementSpeed *= Gdx.graphics.getDeltaTime();

    			// Initialize movement offsets for both X and Y axes that will store the movement direction.
	    		float moveX = 0f;
		    	float moveY = 0f;

    			// Convert directions to movement offsets
	    		for (String singleDirection : directionWords) {
		    		switch (singleDirection) {
			    		case "up" -> moveY -= 1;
				    	case "down" -> moveY += 1;
					    case "left" -> moveX -= 1;
    					case "right" -> moveX += 1;
	    			}
		    	}

                /* Apply the movement to the object's position individually.
                 * If moving in one direction collides. If it does, undo the movement, so the objects remains in a valid position.
                 * This will make it so if you are moving diagonally, and you only collide with something on the X axis, you will still move on the Y.
                  The movement also offsets are scaled by the movementSpeed to produce smooth movement.*/

    			// First, X
	    		pc.x += moveX * movementSpeed;
		    	if (CollisionChecker.checkEntityCollision(entity)) pc.x -= moveX * movementSpeed;

    			// Then, Y
	    		pc.y += moveY * movementSpeed;
		    	if (CollisionChecker.checkEntityCollision(entity)) pc.y -= moveY * movementSpeed;
            }


		    // Modify the screenX and screenY depending on the size of the window
	    	Main.cameraX -= (Main.cameraX - pc.x) * 0.15f;
    		Main.cameraY -= (Main.cameraY - pc.y) * 0.15f;

	    	// Clamp the camera to the map bounds
    		Map map = MapTileHandler.maps.get(Main.currentMap);
		    int maxCameraX = map.width() * Main.tileSize - Main.screenWidth / 2 - Main.tileSize / 2;
	    	int maxCameraY = map.height() * Main.tileSize - Main.screenHeight / 2 - Main.tileSize / 2;
    		float minCameraX = Main.screenWidth / 2f - Main.tileSize / 2;
            float minCameraY = Main.screenHeight / 2f - Main.tileSize / 2;
            if (Main.cameraX < minCameraX) Main.cameraX = minCameraX;
		    if (Main.cameraY < minCameraY) Main.cameraY = minCameraY;
	    	if (Main.cameraX > maxCameraX) Main.cameraX = maxCameraX;
    		if (Main.cameraY > maxCameraY) Main.cameraY = maxCameraY;
        }
    }
}
