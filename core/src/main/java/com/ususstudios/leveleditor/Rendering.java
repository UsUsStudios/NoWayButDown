package com.ususstudios.leveleditor;

import com.badlogic.gdx.graphics.Color;
import com.ususstudios.noway.rendering.Map;
import com.ususstudios.noway.rendering.MapTileHandler;
import com.ususstudios.noway.rendering.Tile;

/** The static class responsible for drawing tiles and some UI on the screen */
public class Rendering {
    public static void drawPlaying() {
        Map map = MapTileHandler.maps.get(Main.currentMap);

        // Position the camera on the player (in world coordinates)
        Main.gameCamera.position.set(
            Main.cameraX + Main.tileSize / 2f,
            Main.cameraY + Main.tileSize / 2f,
            0
        );
        Main.gameCamera.update();

        Main.batch.setProjectionMatrix(Main.gameCamera.combined);
        Main.batch.begin();
        drawLayer(map, map.layer1(), Main.layer == 1 || Main.layer == 0);
        drawLayer(map, map.layer2(), Main.layer == 2 || Main.layer == 0);
        drawLayer(map, map.layer3(), Main.layer == 3 || Main.layer == 0);
        float worldX = Main.mouseTile[1] * Main.tileSize;
        float worldY = Main.mouseTile[0] * Main.tileSize;
        Main.drawRect(worldX, worldY, Main.tileSize, Main.tileSize, 2, Color.RED);

        Main.batch.end();
    }

    private static void drawLayer(Map map, String[][] layer, boolean notOnioned) {
        int tileSize = Main.tileSize;

        // Compute visible tile range using the camera's world bounds
        float camX = Main.gameCamera.position.x;
        float camY = Main.gameCamera.position.y;
        float halfW = Main.gameCamera.viewportWidth / 2f;
        float halfH = Main.gameCamera.viewportHeight / 2f;

        int minCol = Math.max(0, (int) ((camX - halfW) / tileSize));
        int maxCol = Math.min(map.width() - 1, (int) ((camX + halfW) / tileSize) + 1);
        int minRow = Math.max(0, (int) ((camY - halfH) / tileSize));
        int maxRow = Math.min(map.height() - 1, (int) ((camY + halfH) / tileSize) + 1);
        if (notOnioned) Main.batch.setColor(1, 1, 1, 1);
        else Main.batch.setColor(1, 1, 1, 0.15f);

        for (int worldRow = minRow; worldRow <= maxRow; worldRow++) {
            for (int worldCol = minCol; worldCol <= maxCol; worldCol++) {
                String tileNumber = layer[worldRow][worldCol];
                float worldX = worldCol * tileSize;
                float worldY = worldRow * tileSize;

                Tile currentTile = MapTileHandler.tileTypes.get(tileNumber);
                Main.batch.draw(currentTile.image().getTexture(), worldX, worldY);
            }
        }
    }
}
