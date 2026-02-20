package com.ususstudios.noway.systems;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.ususstudios.noway.Main;
import com.ususstudios.noway.components.PositionComponent;
import com.ususstudios.noway.components.TriggerComponent;
import com.ususstudios.noway.main.World;

public class TriggerDrawingSystem implements ECSSystem {
    @Override
    public void process(World world) {
        if (Main.debugMode) {
            // draw entity collision boxes
            Main.shapes.begin(ShapeType.Line);
            Main.shapes.setColor(Color.GREEN);
            for (int entity : world.query(TriggerComponent.class, PositionComponent.class)) {
                PositionComponent pc = world.getEntityComponent(entity, PositionComponent.class).get();
                TriggerComponent tc = world.getEntityComponent(entity, TriggerComponent.class).get();
                float screenX = pc.x  + tc.offX - Main.cameraX + Main.screenWidth / 2f - Main.tileSize / 2f;
                float screenY = -pc.y - tc.offY + Main.cameraY + Main.screenHeight / 2f + Main.tileSize / 2f - tc.height;

                Main.shapes.rect(screenX, screenY, tc.width, tc.height);
            }
            Main.shapes.end();
        }
    }
}
