package com.ususstudios.noway.rendering;

import com.ususstudios.noway.Main;
import com.ususstudios.noway.components.*;

public class CollisionChecker {
    public static boolean check2EntitiesCollision(int entityA, int entityB) {
        // Make sure the entites have the necessary components
        if (!Main.world.getEntityComponent(entityA, PositionComponent.class).isPresent() ||
                !Main.world.getEntityComponent(entityB, PositionComponent.class).isPresent()) return false;
        if (!Main.world.getEntityComponent(entityA, CollisionComponent.class).isPresent() ||
                !Main.world.getEntityComponent(entityB, CollisionComponent.class).isPresent()) return false;

        // Declare components
        CollisionComponent cA = Main.world.getEntityComponent(entityA, CollisionComponent.class).get();
        CollisionComponent cB = Main.world.getEntityComponent(entityB, CollisionComponent.class).get();
        PositionComponent pcA = Main.world.getEntityComponent(entityA, PositionComponent.class).get();
        PositionComponent pcB = Main.world.getEntityComponent(entityB, PositionComponent.class).get();

        float aLeft = pcA.x + cA.offX;
        float aTop = pcA.y + cA.offY;
        float aRight = pcA.x + cA.width;
        float aBottom = pcA.y + cA.height;

        float bLeft = pcB.x + cB.offX;
        float bTop = pcB.y + cB.offY;
        float bRight = pcB.x + cB.width;
        float bBottom = pcB.y + cB.height;

        return aLeft < bRight && aRight > bLeft && aTop < bBottom && aBottom > bTop;
    }

        public static boolean checkBlockCollision(Integer entity, boolean[][] collisionPoints, float x, float y) {
        // Make sure the entites have the necessary components
        if (!Main.world.getEntityComponent(entity, PositionComponent.class).isPresent()) return false;
        if (!Main.world.getEntityComponent(entity, CollisionComponent.class).isPresent()) return false;

        // Declare the components
        CollisionComponent component = Main.world.getEntityComponent(entity, CollisionComponent.class).get();
        PositionComponent positionComponent = Main.world.getEntityComponent(entity, PositionComponent.class).get();

        int gridX = collisionPoints.length;      // number of columns in collision grid
        int gridY = collisionPoints[0].length;   // number of rows in collision grid
        float cellW = Main.tileSize / (float) gridX;
        float cellH = Main.tileSize / (float) gridY;

        for (int i = 0; i < gridX; i++) {
            for (int j = 0; j < gridY; j++) {
                if (collisionPoints[i][j]) {
                    float halfW = component.width / 2f;
                    float halfH = component.height / 2f;

                    float px = x + i * cellW + cellW / 2f;
                    float py = y + j * cellH + cellH / 2f;
                    if (Math.abs(px - positionComponent.x) <= halfW && Math.abs(py - positionComponent.y) <= halfH) return true;
                }
            }
        }

        return false;
    }

    // Check if entity collides with any other entity or any tile on layer2
    public static boolean checkEntityCollision(Integer entity) {
        for (Integer other : Main.world.query(CollisionComponent.class, PositionComponent.class)) {
            if (other != entity)
                if (check2EntitiesCollision(entity, other)) return true;
        }

        // Check collision with player too (if it's not the player)
        if (!Main.world.getEntityComponent(entity, PlayerComponent.class).isPresent())
            if (check2EntitiesCollision(entity, Main.playerId)) return true;

        Map map = MapTileHandler.maps.get(Main.currentMap);
        if (map == null) return false;

        for (int row = 0; row < map.height(); row++) {
            for (int col = 0; col < map.width(); col++) {
                int tileNumber = map.layer2()[col][row];
                var tile = MapTileHandler.tileTypes.get(tileNumber);
                if (tile == null) continue;

                boolean[][] collisionPoints = tile.collision();
                int worldX = col * Main.tileSize - Main.tileSize / 2; // tile top-left X
                int worldY = row * Main.tileSize - Main.tileSize / 2; // tile top-left Y

                if (checkBlockCollision(entity, collisionPoints, worldX, worldY)) return true;
            }
        }

        return false;
    }
}
