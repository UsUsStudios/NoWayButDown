package com.ususstudios.noway.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.ususstudios.noway.Main;
import com.badlogic.gdx.graphics.Color;
import java.awt.*;
import java.io.IOException;

/** The static class responsible for drawing tiles and UI on the screen */
public class GameRendering {
    /** The Fira medium font size */
	static BitmapFont firaMedium;
    /** The Fira bold font size */
	static BitmapFont firaBold;
	/** The Fira regular font size */
    static BitmapFont firaRegular;
    /** The Fira regular font size, but flipped */
	static BitmapFont firaRegularFlipped;

	// UI
    /** Which UI option is currently selected? */
	public static int uiSelected = 0;
    /** How many UI options are there at the moment? */
	public static int uiMaxOptions = 2;

    /** Initialize all the static stuff needed for this class to render properly. Called by {@link com.ususstudios.noway.Main}. */
	public static void init() {
		// Load in the fonts
		try {
			firaMedium = getFont("FiraSans-Medium", false);
			firaBold = getFont("FiraSans-Bold", false);
			firaRegular = getFont("FiraSans-Regular", false);
			firaRegularFlipped = getFont("FiraSans-Regular", true);
		} catch (FontFormatException | IOException e) {
			Main.handleException(e);
		}
	}

    /**
     * Get a com.badlogic.gdx.graphics.g2d.BitmapFont from a file name.
     * @param name The path of the font path, starting from /assets/fonts/
     * @param flip Whether to flip the font upside down
     * @return A BitmapFont of the font
     * @throws FontFormatException If there's an issue with the TrueTypeFont file format
     * @throws IOException If there's an issue with reading the file
     */
	public static BitmapFont getFont(String name, boolean flip) throws FontFormatException, IOException {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/" + name + ".ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 128; // fonts size in pixels
        parameter.flip = flip;
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
        return font;
    }

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
                String tileNumber = map.layer2()[worldRow][worldCol];
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

    /** Draws all the tiles and UI that should be drawn above the player in the {@link com.ususstudios.noway.main.States.GameStates} PLAYING state. */
    public static void drawPlayingUI() {
        Map map = MapTileHandler.maps.get(Main.currentMap);
        Main.batch.begin();
        drawLayer(map, map.layer3());

        if (!Main.bottomMiddleText.isBlank()) {
            firaRegularFlipped.getData().setScale(0.3f);
            drawCenteredString(firaRegularFlipped, Main.bottomMiddleText, Main.screenWidth / 2, 550);
        }
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
    private static void drawLayer(Map map, String[][] layer) {
        float camX = Main.cameraX + Main.tileSize / 2f;
        float camY = Main.cameraY + Main.tileSize / 2f;  // Slight adjustment to center the player on a block
        int tileSize = Main.tileSize;
        for (int worldRow = 0; worldRow < map.height(); worldRow++) {
            for (int worldCol = 0; worldCol < map.width(); worldCol++) {
                String tileNumber = layer[worldRow][worldCol];
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

    /** Draws the UI in the {@link com.ususstudios.noway.main.States.GameStates} TITLE state */
    public static void drawTitle() {
        ScreenUtils.clear(Color.BLACK);

        Main.batch.begin();
        firaMedium.setColor(0.234375f, 0.12109375f, 0.75390625f, 1f);
        firaMedium.getData().setScale(0.8f);
		drawCenteredString(firaMedium, "No Way But Down", Main.screenWidth / 2, 500);

        firaMedium.getData().setScale(0.5f);
        firaMedium.setColor(1f, 1f, 1f, 1f);
		drawButton("New Game", 300, 0);
		drawButton("Load Game", 220, 1);
		drawButton("Quit", 140, 2);
        Main.batch.end();
	}

    /** Draws the splash screen image at game startup */
    public static void drawSplash() {
        Main.batch.begin();
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Main.batch.enableBlending();
        Color c = Main.batch.getColor();
        Main.batch.setColor(c.r, c.g, c.b, (float) Main.transitionAlpha);
        Main.batch.draw(Image.loadImage("ususlogo").getTexture(), 125, 150, 400, 400);

        firaMedium.setColor(0.15f, 0.15f, 0.75f, (float) Main.transitionAlpha);
        firaMedium.getData().setScale(0.75f);
        firaMedium.draw(Main.batch, "UsUsStudios", 460, 450);
        Main.batch.end();
    }

	/** Updates the UI elements via keyboard input */
	public static void updateUI() {
        // Input canceling so that you can't press and hold
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            uiSelected--;
            if (uiSelected < 0) uiSelected = 0;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            uiSelected++;
            if (uiSelected > uiMaxOptions) uiSelected = uiMaxOptions;
        }

		if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
			switch (uiSelected) {
				case 0 -> Main.loadMap("main");
				case 1 -> {}
				case 2 -> System.exit(0);
			}
		}
	}

    /**
     * Draws a button with a given text and y position
     * @param text The text that should be drawn on the button
     * @param y The y position of the bottom (the x is centered)
     * @param i The index of the button, to know if it is currently being selected
     */
    public static void drawButton(String text, int y, int i) {
        if (i == uiSelected) {
            drawCenteredString(firaMedium, "> " + text + " <",
                Main.screenWidth / 2, y);
        } else {
            drawCenteredString(firaMedium, text, Main.screenWidth / 2, y);
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
    public static void dispose() {
        firaMedium.dispose();
        firaBold.dispose();
        firaRegular.dispose();
        firaRegularFlipped.dispose();
    }
}
