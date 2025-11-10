package com.ususstudios.noway.rendering;

import com.ususstudios.noway.main.Game;

import java.awt.*;

public class GameRendering {
	public static void drawPlaying(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 800, 600);
		
		Map map = MapTileHandler.maps.get(Game.currentMap);
		System.out.println(map.name());
		for (int row = 0; row < map.height(); row++) {
			for (int col = 0; col < map.width(); col++) {
				int tileID = map.layer1()[row][col];
				Tile tile = MapTileHandler.tileTypes.get(tileID);
				if (tile != null && tile.image != null) {
					g.drawImage(tile.image, col * Game.tileSize, row * Game.tileSize, Game.tileSize, Game.tileSize, null);
				}
			}
		}
		for (int row = 0; row < map.height(); row++) {
			for (int col = 0; col < map.width(); col++) {
				int tileID = map.layer2()[row][col];
				Tile tile = MapTileHandler.tileTypes.get(tileID);
				if (tile != null && tile.image != null) {
					g.drawImage(tile.image, col * Game.tileSize, row * Game.tileSize, Game.tileSize, Game.tileSize, null);
				}
			}
		}
		for (int row = 0; row < map.height(); row++) {
			for (int col = 0; col < map.width(); col++) {
				int tileID = map.layer3()[row][col];
				Tile tile = MapTileHandler.tileTypes.get(tileID);
				if (tile != null && tile.image != null) {
					g.drawImage(tile.image, col * Game.tileSize, row * Game.tileSize, Game.tileSize, Game.tileSize, null);
				}
			}
		}
	}
}
