package com.ususstudios.noway.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ususstudios.noway.Main;
import com.ususstudios.noway.entity.Entity;
import com.ususstudios.noway.main.Translations;
import com.badlogic.gdx.graphics.Color;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.stream.IntStream;

public class GameRendering {
	static BitmapFont firaMedium;
	static BitmapFont firaBold;
	static BitmapFont firaRegular;

	// UI
	public static int uiSelected = 0;
	public static int uiMaxOptions = 2;

	public static void initialize() {
		// Load in the fonts
		try {
			firaMedium = getFont("FiraSans-Medium");
			firaBold = getFont("FiraSans-Bold");
			firaRegular = getFont("FiraSans-Regular");
		} catch (FontFormatException | IOException e) {
			Main.handleException(e);
		}
	}

	public static BitmapFont getFont(String name) throws FontFormatException, IOException {
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/" + name + ".ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 128; // fonts size in pixels
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();
		return font;
	}

	public static void drawPlaying() {
		float camX = Main.player.cameraX;
		float camY = Main.player.cameraY;
		int tileSize = Main.tileSize;

		Map map = MapTileHandler.maps.get(Main.currentMap);
		IntStream.range(0, map.height()).parallel().forEach(worldRow ->
				IntStream.range(0, map.width()).parallel().forEach(worldCol -> {
					int tileNumber = map.layer1()[worldRow][worldCol];
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
				})
		);
		IntStream.range(0, map.height()).parallel().forEach(worldRow ->
				IntStream.range(0, map.width()).parallel().forEach(worldCol -> {
					int tileNumber = map.layer2()[worldRow][worldCol];
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

						// g.setColor(new Color(255, 0, 0, 150));
						// for (int x = 0; x < 5; x++) {
						// 	for (int y = 0; y < 5; y++) {
						// 		// Debugging collision points
						// 		if (currentTile.collision()[x][y]) {
						// 			int pointX = Math.round(screenX + (x * (tileSize / 5f)) + (tileSize / 10f));
						// 			int pointY = Math.round(screenY + (y * (tileSize / 5f)) + (tileSize / 10f));
						// 			g.fillOval(pointX, pointY, 5, 5);
						// 		}
						// 	}
						// }
					}
				})
		);
		IntStream.range(0, map.height()).parallel().forEach(worldRow ->
				IntStream.range(0, map.width()).parallel().forEach(worldCol -> {
					int tileNumber = map.layer3()[worldRow][worldCol];
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
				})
		);

		Main.entities.forEach(Entity::draw);

		Main.darkness.draw();
	}

	public static void drawTitle() {
        initialize();
        Main.shapes.begin(ShapeRenderer.ShapeType.Filled);
        Main.shapes.setColor(Color.BLACK);
        Main.shapes.rect(0, 0, Main.screenWidth, Main.screenHeight);
        Main.shapes.end();

        Main.batch.begin();
        firaMedium.setColor(0.234375f, 0.12109375f, 0.75390625f, 1f);
		drawCenteredString(firaMedium, Translations.get(Main.identifier, "title"), Main.screenWidth / 2, 500);

        firaMedium.getData().setScale(0.5f);
        firaMedium.setColor(1f, 1f, 1f, 1f);
		drawButton("new_game", 300, 0);
		drawButton("load_game", 220, 1);
		drawButton("quit", 140, 2);
        Main.batch.end();
	}

	/// Updates the UI elements via keyboard input
	public static void updateUI() {
		if (Main.inputHandler.keyMap.get(KeyEvent.VK_UP)) {
			uiSelected--;
			if (uiSelected < 0) uiSelected = 0;
			Main.inputHandler.keyMap.put(KeyEvent.VK_UP, false);
		}
		if (Main.inputHandler.keyMap.get(KeyEvent.VK_DOWN)) {
			uiSelected++;
			if (uiSelected > uiMaxOptions) uiSelected = uiMaxOptions;
			Main.inputHandler.keyMap.put(KeyEvent.VK_DOWN, false);
		}

		if (Main.inputHandler.keyMap.get(KeyEvent.VK_ENTER)) {
			switch (uiSelected) {
				case 0 -> Main.loadMap("main");
				case 1 -> {}
				case 2 -> System.exit(0);
			}
		}
	}

	public static void drawButton(String textName, int y, int i) {
        if (i == uiSelected) {
            drawCenteredString(firaMedium, "> " + Translations.get(Main.identifier, textName) + " <",
                Main.screenWidth / 2, y);
        } else {
            drawCenteredString(firaMedium, Translations.get(Main.identifier, textName), Main.screenWidth / 2, y);
        }
	}

	public static void drawCenteredString(BitmapFont font, String text, int x1, int y1) {
        // Create a layout to measure the text
        GlyphLayout layout = new GlyphLayout();
        layout.setText(font, text);

        // Calculate center position
        float x = x1 - (layout.width) / 2;
        float y = y1 + (layout.height) / 2; // for vertical centering

        font.draw(Main.batch, layout, x, y);
	}

    public static void dispose() {
        firaMedium.dispose();
        firaBold.dispose();
        firaRegular.dispose();
    }
}
