package com.ususstudios.noway.objects;

import com.ususstudios.noway.Main;
import com.ususstudios.noway.rendering.Map;
import com.ususstudios.noway.rendering.MapTileHandler;

public class CollisionChecker {
    // helper AABB overlap check (positions are top-left)
    private static boolean rectsOverlap(float ax, float ay, float aw, float ah,
                                        float bx, float by, float bw, float bh) {
        return ax < bx + bw && ax + aw > bx && ay < by + bh && ay + ah > by;
    }

    // Check if two objects are colliding.
    // `colX` and `colY` are offsets from the object's `x`/`y` where the collision rect starts.
    public static boolean check2EntityCollision(GameObject a, GameObject b) {
        float ax = a.x + a.colX;
        float ay = a.y + a.colY;
        float bx = b.x + b.colX;
        float by = b.y + b.colY;
        return rectsOverlap(ax, ay, a.width, a.height, bx, by, b.width, b.height);
    }

    // Check collision against a tile's collision grid.
    // `x`/`y` are the tile top-left world coordinates.
    public static boolean checkBlockCollision(Entity e, boolean[][] collisionPoints, float x, float y) {
        if (collisionPoints == null || collisionPoints.length == 0 || collisionPoints[0].length == 0) return false;

        int gridCols = collisionPoints.length;        // number of columns in provided collision grid
        int gridRows = collisionPoints[0].length;     // number of rows in provided collision grid
        float cellW = Main.tileSize / (float) gridCols;
        float cellH = Main.tileSize / (float) gridRows;

        // entity collision rect top-left using offsets
        float ex = e.x + e.colX;
        float ey = e.y + e.colY;
        float ew = e.width;
        float eh = e.height;

        for (int i = 0; i < gridCols; i++) {
            for (int j = 0; j < gridRows; j++) {
                if (collisionPoints[i][j]) {
                    float cellX = x + i * cellW; // tile cell top-left X
                    float cellY = y + j * cellH; // tile cell top-left Y
                    if (rectsOverlap(ex, ey, ew, eh, cellX, cellY, cellW, cellH)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    // Check if entity collides with any other objects or any tile on layer2
    public static boolean checkEntityColliding(Entity e) {
        for (GameObject other : Main.objects) {
            if (other != e && other.collision) {
                if (check2EntityCollision(e, other)) return true;
            }
        }

        Map map = MapTileHandler.maps.get(Main.currentMap);
        if (map == null) return false;

        for (int row = 0; row < map.height(); row++) {
            for (int col = 0; col < map.width(); col++) {
                int tileNumber = map.layer2()[row][col];
                var tile = MapTileHandler.tileTypes.get(tileNumber);
                if (tile == null) continue;

                boolean[][] collisionPoints = tile.collision();
                int worldX = col * Main.tileSize; // tile top-left X (fixed)
                int worldY = row * Main.tileSize; // tile top-left Y (fixed)

                if (checkBlockCollision(e, collisionPoints, worldX, worldY)) return true;
            }
        }

        return false;
    }
}
