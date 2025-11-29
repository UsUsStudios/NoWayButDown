package com.ususstudios.noway.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.ScreenUtils;
import com.ususstudios.noway.Main;
import com.ususstudios.noway.main.Translations;
import com.badlogic.gdx.graphics.Color;
import com.ususstudios.noway.objects.GameObject;
import java.awt.*;
import java.io.IOException;

public class GameRendering {
	static BitmapFont firaMedium;
	static BitmapFont firaBold;
	static BitmapFont firaRegular;

	// UI
	public static int uiSelected = 0;
	public static int uiMaxOptions = 2;

	public static void init() {
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
		Map map = MapTileHandler.maps.get(Main.currentMap);
        Main.batch.getProjectionMatrix().setToOrtho(0, Main.screenWidth, Main.screenHeight, 0, 0, 1);
        Main.batch.begin();

        drawLayer(map, map.layer1());
        drawLayer(map, map.layer2());
        drawLayer(map, map.layer3());

		Main.objects.forEach(GameObject::draw);

        Main.darkness.draw();
	}

    private static void drawLayer(Map map, int[][] layer) {
        float camX = Main.player.cameraX;
        float camY = Main.player.cameraY;
        int tileSize = Main.tileSize;
        for (int worldRow = 0; worldRow < map.height(); worldRow++) {
            for (int worldCol = 0; worldCol < map.width(); worldCol++) {
                int tileNumber = layer[worldRow][worldCol];
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

    public static void drawTitle() {
        ScreenUtils.clear(Color.BLACK);

        Main.batch.begin();
        firaMedium.setColor(0.234375f, 0.12109375f, 0.75390625f, 1f);
        firaMedium.getData().setScale(0.8f);
		drawCenteredString(firaMedium, Translations.get(Main.identifier, "title"), Main.screenWidth / 2, 500);

        firaMedium.getData().setScale(0.5f);
        firaMedium.setColor(1f, 1f, 1f, 1f);
		drawButton("new_game", 300, 0);
		drawButton("load_game", 220, 1);
		drawButton("quit", 140, 2);
        Main.batch.end();
	}

    public static void drawSplash() {
        ScreenUtils.clear(Color.BLACK);
    }

	/// Updates the UI elements via keyboard input
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
