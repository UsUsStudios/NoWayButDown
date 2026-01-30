package com.ususstudios.noway.systems;

import com.ususstudios.noway.Main;
import com.ususstudios.noway.main.*;
import com.ususstudios.noway.components.*;
import com.badlogic.gdx.graphics.Texture;

public class SpritesheetSystem implements ECSSystem {
    @Override
    public void process(World world) {
        for (Integer entity : world.query(SpritesheetComponent.class, PositionComponent.class)) {
            PositionComponent positionComponent = world.getEntityComponent(entity, PositionComponent.class).get();

            float screenX = positionComponent.x - Main.cameraX + Main.screenWidth / 2f;
            float screenY = positionComponent.y - Main.cameraY + Main.screenHeight / 2f;

            SpritesheetComponent sc = world.getEntityComponent(entity, SpritesheetComponent.class).get();

            // Store what part of the sprite sheet to draw
            int imageX = Main.tileSize * sc.column;
            int imageY = Main.tileSize * (sc.row - 1);

            Main.batch.draw(sc.spriteSheet.getTexture(),
                // Image position in the world
                Math.round(screenX - Main.tileSize / 2), Math.round(screenY - Main.tileSize / 2), sc.scaleX * Main.tileSize, sc.scaleY * Main.tileSize,
                // Image position in the sprite sheet
                imageX, sc.spriteSheet.getTexture().getWidth() - imageY, Main.tileSize, -Main.tileSize,
                // Flip X and Y
                false, true);
        }
    }
}
