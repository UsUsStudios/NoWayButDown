package com.ususstudios.noway.rendering;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.ususstudios.noway.Main;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

/** The static class responsible for drawing tiles and some UI on the screen */
public class GameRendering {
    /** Initialize all the static stuff needed for this class to render properly. Called by {@link com.ususstudios.noway.Main}. */
    public static void init() {}

    /** Draws all the tiles that should be drawn below the player in the {@link com.ususstudios.noway.main.States.GameStates} PLAYING state. */
    public static void drawPlaying() {
        Map map = MapTileHandler.maps.get(Main.currentMap);
        Main.batch.begin();
        Main.batch.getProjectionMatrix().setToOrtho(0, Main.screenWidth, Main.screenHeight, 0, 0, 1);

        drawLayer(map, map.layer1());
        drawLayer(map, map.layer2());

        Main.batch.end();

        if (!Main.debugMode) {
            return;
        }

        // Draw debug collisions
        Main.shapes.begin(ShapeRenderer.ShapeType.Filled);
        Main.shapes.setColor(Color.BLUE);
        float camX = Main.cameraX;
        float camY = Main.cameraY;
        int tileSize = Main.tileSize;
        for (int worldRow = 0; worldRow < map.height(); worldRow++) {
            for (int worldCol = 0; worldCol < map.width(); worldCol++) {
                short tileNumber = map.layer2()[worldRow][worldCol];
                int worldX = worldCol * tileSize;
                int worldY = worldRow * tileSize;
                float screenX = worldX - camX + Main.screenWidth / 2f;
                float screenY = worldY - camY + Main.screenHeight / 2f;

                // Check if the tile is within the visible screen
                if (worldX + tileSize > camX - Main.screenWidth / 2f &&
                    worldX - tileSize < camX + Main.screenWidth / 2f &&
                    worldY + tileSize > camY - Main.screenHeight / 2f &&
                    worldY - tileSize < camY + Main.screenHeight / 2f) {
                    Tile currentTile = MapTileHandler.tileTypes.get(tileNumber);
                    drawBlockCollision(currentTile.collision(), screenX, screenY);
                }
            }
        }
        Main.shapes.end();
    }

    /** Draws the pause UI that should be drawn above the player in the {@link com.ususstudios.noway.main.States.GameStates} PAUSED state. */
    public static void drawPaused() {}

    /** Draws all the tiles and UI that should be drawn above the player in the {@link com.ususstudios.noway.main.States.GameStates} PLAYING state. */
    public static void drawPlayingUI() {
        Map map = MapTileHandler.maps.get(Main.currentMap);
        Main.batch.begin();
        drawLayer(map, map.layer3());

        if (!Main.bottomMiddleText.isBlank()) {
            drawCenteredString(UI.getFont("FiraSans-Regular", 38, true), Main.bottomMiddleText, Main.screenWidth / 2, 550);
        }
        Main.batch.end();

        Main.shapes.begin(ShapeType.Filled);
        for (ParticleInstance.Particle particle : Main.particles) {
            particle.tick(1);
            particle.draw(Main.shapes, 500, 300);
        }
        Main.shapes.end();
    }

    /** Draws the splash screen image at game startup */
    public static void drawSplash() {
        Main.batch.begin();
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Main.batch.enableBlending();
        Color c = Main.batch.getColor();
        Main.batch.setColor(c.r, c.g, c.b, (float) Main.transitionAlpha);
        Main.batch.draw(Image.loadImage("ususlogo").getTexture(), 125, 150, 400, 400);

        BitmapFont firaMedium = UI.getFont("FiraSans-Medium", 128, false);
        firaMedium.setColor(0.15f, 0.15f, 0.75f, (float) Main.transitionAlpha);
        firaMedium.getData().setScale(0.75f);
        firaMedium.draw(Main.batch, "UsUsStudios", 460, 450);
        Main.batch.end();
    }

    /**
     * Draws the collision points of a tile
     * @param collisionPoints The 2D array of collision points that are either on or off
     * @param x The x screen position (in pixels) of the top-left of the tile
     * @param y The y screen position (in pixels) of the top-left of the tile
     */
    public static void drawBlockCollision(boolean[][] collisionPoints, float x, float y) {
        float mar = 2f; // margin to not draw on the edges
        if (collisionPoints == null || collisionPoints.length == 0 || collisionPoints[0].length == 0) return;

        int gridCols = collisionPoints.length;        // number of columns in provided collision grid
        int gridRows = collisionPoints[0].length;     // number of rows in provided collision grid
        float cellW = Main.tileSize / (float) gridCols;
        float cellH = Main.tileSize / (float) gridRows;

        for (int i = 0; i < gridCols; i++) {
            for (int j = 0; j < gridRows; j++) {
                if (collisionPoints[i][j]) {
                    float cellX = x + i * cellW - Main.tileSize / 2f; // tile cell top-left X
                    float cellY = y + j * cellH - Main.tileSize / 2f; // tile cell top-left Y
                    Main.shapes.rect(cellX-mar+3, Main.screenHeight-cellY-cellH-mar+3, cellW-mar, cellH-mar);
                }
            }
        }
    }

    /**
     * Draws an entire layer of tiles from a {@link com.ususstudios.noway.rendering.Map}
     * @param map The Map that is currently being drawn
     * @param layer The 2D array of 2-digit base 64 tile IDs that represent the layer currently being drawn
     */
    private static void drawLayer(Map map, short[][] layer) {
        float camX = Main.cameraX + Main.tileSize / 2f;
        float camY = Main.cameraY + Main.tileSize / 2f;  // Slight adjustment to center the player on a block
        int tileSize = Main.tileSize;
        for (int worldRow = 0; worldRow < map.height(); worldRow++) {
            for (int worldCol = 0; worldCol < map.width(); worldCol++) {
                short tileNumber = layer[worldRow][worldCol];
                int worldX = worldCol * tileSize;
                int worldY = worldRow * tileSize;
                float screenX = worldX - camX + Main.screenWidth / 2f;
                float screenY = worldY - camY + Main.screenHeight / 2f;

                // Check if the tile is within the visible screen
                if (worldX + tileSize > camX - Main.screenWidth / 2f &&
                    worldX - tileSize < camX + Main.screenWidth / 2f &&
                    worldY + tileSize > camY - Main.screenHeight / 2f &&
                    worldY - tileSize < camY + Main.screenHeight / 2f) {
                    Tile currentTile = MapTileHandler.tileTypes.get(tileNumber);
                    Main.batch.draw(currentTile.image().getTexture(), Math.round(screenX), Math.round(screenY));
                }
            }
        }
    }

    /**
     * Utility for drawing a x- and y-centered string
     * @param font The font to draw the string with
     * @param text The text to draw
     * @param x1 The x-position that the center of the text should be
     * @param y1 The y-position that the center of the text should be
     */
    public static void drawCenteredString(BitmapFont font, String text, int x1, int y1) {
        // Create a layout to measure the text
        GlyphLayout layout = new GlyphLayout();
        layout.setText(font, text);

        // Calculate center position
        float x = x1 - (layout.width) / 2;
        float y = y1 + (layout.height) / 2; // for vertical centering

        font.draw(Main.batch, layout, x, y);
    }

    /** Release all the BitmapFonts to avoid leaking memory */
    public static void dispose() {}
}
