package com.ususstudios.noway.systems;

import com.ususstudios.noway.Main;
import com.ususstudios.noway.main.*;
import com.ususstudios.noway.components.*;
import com.badlogic.gdx.graphics.Texture;
import java.nio.charset.StandardCharsets;

public class SpritesheetSystem implements ECSSystem {
    @Override
    public void process(World world) {
        for (Integer entity : world.query(SpritesheetComponent.class, PositionComponent.class)) {
            PositionComponent pc = world.getEntityComponent(entity, PositionComponent.class).get();
            SpritesheetComponent sc = world.getEntityComponent(entity, SpritesheetComponent.class).get();

            float screenX = pc.x - Main.cameraX + Main.screenWidth / 2f;
            float screenY = pc.y - Main.cameraY + Main.screenHeight / 2f;
            if (sc.spriteColumn == -1 && sc.spriteRow == -1) {
                // Draw full image
                Main.batch.draw(sc.currentImage.getTexture(),
                    Math.round(screenX),
                    Math.round(screenY));
            } else {
                // Draw a subregion from a sprite sheet
                Texture texture = sc.currentImage.getTexture();
                int srcX = Main.tileSize * sc.spriteColumn;
                int srcY = Main.tileSize * sc.spriteRow;

                Main.batch.draw(texture,
                    Math.round(screenX),                    // dest x
                    Math.round(screenY),                    // dest y
                    Main.tileSize * sc.scaleX,              // dest width
                    Main.tileSize * sc.scaleY,              // dest height
                    srcX,                                   // src x
                    texture.getWidth() - srcY,              // src y  I don't know why it's width instead of height...
                                                                      // if it works don't touch it
                    Main.tileSize,                          // src width
                    Main.tileSize,                          // src height
                    false,                                  // flipX
                    false);                                 // flipY
            }

            //if (Main.debugMode) {
            //    // Draw hitbox
            //    Main.batch.end();

            //    Main.shapes.begin(ShapeRenderer.ShapeType.Line);
            //    Main.shapes.setColor(Color.RED);
            //    screenX = pc.x + sc.colX - Main.cameraX + Main.screenWidth / 2f;
            //    screenY = pc.y + sc.colY - Main.cameraY + Main.screenHeight / 2f;
            //    Main.shapes.rect(screenX, Main.screenHeight - screenY - height, width, height);
            //    Main.shapes.end();

            //    Main.batch.begin();
            //}
        }
    }
}
