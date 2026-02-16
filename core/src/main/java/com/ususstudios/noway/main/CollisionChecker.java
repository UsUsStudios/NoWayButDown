package com.ususstudios.noway.main;

import com.ususstudios.noway.Main;
import com.ususstudios.noway.components.*;
import com.ususstudios.noway.rendering.Map;
import com.ususstudios.noway.rendering.MapTileHandler;

import java.util.Objects;

public class CollisionChecker {
    public static boolean check2EntitiesCollision(int entityA, int entityB) {
        // Make sure the entites have the necessary components
        if (Main.world.getEntityComponent(entityA, PositionComponent.class).isEmpty() ||
            Main.world.getEntityComponent(entityB, PositionComponent.class).isEmpty()) return false;
        if (Main.world.getEntityComponent(entityA, CollisionComponent.class).isEmpty() ||
            Main.world.getEntityComponent(entityB, CollisionComponent.class).isEmpty()) return false;

        // Declare components
        CollisionComponent cA = Main.world.getEntityComponent(entityA, CollisionComponent.class).get();
        CollisionComponent cB = Main.world.getEntityComponent(entityB, CollisionComponent.class).get();
        PositionComponent pcA = Main.world.getEntityComponent(entityA, PositionComponent.class).get();
        PositionComponent pcB = Main.world.getEntityComponent(entityB, PositionComponent.class).get();

        float aLeft = pcA.x + cA.offX;
        float aTop = pcA.y + cA.offY;
        float aRight = pcA.x + cA.offX + cA.width;
        float aBottom = pcA.y + cA.offY + cA.height;

        float bLeft = pcB.x + cB.offX;
        float bTop = pcB.y + cB.offY;
        float bRight = pcB.x + cB.offX + cB.width;
        float bBottom = pcB.y + cB.offY + cB.height;

        return aLeft < bRight && aRight > bLeft && aTop < bBottom && aBottom > bTop;
    }

    public static boolean checkBlockCollision(Integer entity, boolean[][] collisionPoints, float x, float y) {
        // Make sure the entites have the necessary components
        if (Main.world.getEntityComponent(entity, PositionComponent.class).isEmpty()) return false;
        if (Main.world.getEntityComponent(entity, CollisionComponent.class).isEmpty()) return false;

        // Declare the components
        CollisionComponent c = Main.world.getEntityComponent(entity, CollisionComponent.class).get();
        PositionComponent pc = Main.world.getEntityComponent(entity, PositionComponent.class).get();

        int gridX = collisionPoints.length;      // number of columns in collision grid
        int gridY = collisionPoints[0].length;   // number of rows in collision grid
        float cellW = Main.tileSize / (float) gridX;
        float cellH = Main.tileSize / (float) gridY;

        for (int i = 0; i < gridX; i++) {
            for (int j = 0; j < gridY; j++) {
                if (collisionPoints[i][j]) {
                    float px = x + i * cellW;
                    float py = y + j * cellH;

                    float aLeft = pc.x + c.offX;
                    float aTop = pc.y + c.offY;
                    float aRight = pc.x + c.offX + c.width;
                    float aBottom = pc.y + c.offY + c.height;

                    // "oh but usus why are you flipping the x and y axes"
                    // I have no fucking idea
                    float bRight = py + cellW;
                    float bBottom = px + cellH;

                    if (aLeft < bRight && aRight > py && aTop < bBottom && aBottom > px) return true;
                }
            }
        }

        return false;
    }

    // Check if entity collides with any other entity or any tile on layer2
    public static boolean checkEntityCollision(Integer entity) {
        for (Integer other : Main.world.query(CollisionComponent.class, PositionComponent.class)) {
            if (!Objects.equals(other, entity))
                if (check2EntitiesCollision(entity, other)) return true;
        }

        // Check collision with player too (if it's not the player)
        if (Main.world.getEntityComponent(entity, PlayerComponent.class).isEmpty())
            if (check2EntitiesCollision(entity, Main.playerId)) return true;

        Map map = MapTileHandler.maps.get(Main.currentMap);
        if (map == null) return false;

        for (int row = 0; row < map.height(); row++) {
            for (int col = 0; col < map.width(); col++) {
                String tileNumber = map.layer2()[col][row];
                var tile = MapTileHandler.tileTypes.get(tileNumber);
                if (tile == null) continue;

                boolean[][] collisionPoints = tile.collision();
                int worldX = col * Main.tileSize; // tile top-left X
                int worldY = row * Main.tileSize; // tile top-left Y

                if (checkBlockCollision(entity, collisionPoints, worldX, worldY)) return true;
            }
        }

        return false;
    }
}
