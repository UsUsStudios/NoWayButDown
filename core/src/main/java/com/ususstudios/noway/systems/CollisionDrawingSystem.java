package com.ususstudios.noway.systems;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.ususstudios.noway.Main;
import com.ususstudios.noway.components.CollisionComponent;
import com.ususstudios.noway.components.PositionComponent;
import com.ususstudios.noway.main.World;

public class CollisionDrawingSystem implements ECSSystem {
    @Override
    public void process(World world) {
        // draw entity collision boxes
        Main.shapes.begin(ShapeType.Line);
        for (int entity : world.query(CollisionComponent.class, PositionComponent.class)) {
            PositionComponent pc = world.getEntityComponent(entity, PositionComponent.class).get();
            CollisionComponent cc = world.getEntityComponent(entity, CollisionComponent.class).get();
            float screenX = pc.x  + cc.offX - Main.cameraX + Main.screenWidth / 2f - Main.tileSize / 2f;
            float screenY = -pc.y - cc.offY + Main.cameraY + Main.screenHeight / 2f + Main.tileSize / 2f - cc.height;

            Main.shapes.rect(screenX, screenY, cc.width, cc.height);
        }
        Main.shapes.end();
    }
}
