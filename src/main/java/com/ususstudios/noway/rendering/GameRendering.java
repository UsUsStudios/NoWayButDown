package com.ususstudios.noway.rendering;

import com.ususstudios.noway.main.Game;
import java.awt.*;
import java.util.stream.IntStream;

public class GameRendering {
	public static void drawPlaying(Graphics g) {
		float camX = Game.player.cameraX;
		float camY = Game.player.cameraY;
		int tileSize = Game.tileSize;
		
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, 800, 600);
		
		Map map = MapTileHandler.maps.get(Game.currentMap);
		IntStream.range(0, map.height()).parallel().forEach(worldRow ->
				IntStream.range(0, map.width()).parallel().forEach(worldCol -> {
					int tileNumber = map.layer1()[worldRow][worldCol];
					int worldX = worldCol * tileSize;
					int worldY = worldRow * tileSize;
					float screenX = worldX - camX + Game.screenWidth / 2f;
					float screenY = worldY - camY + Game.screenHeight / 2f;
					
					// Check if the tile is within the visible screen
					if (worldX + tileSize > camX - Game.screenWidth / 2f &&
							worldX - tileSize < camX + Game.screenWidth / 2f &&
							worldY + tileSize > camY - Game.screenHeight / 2f &&
							worldY - tileSize < camY + Game.screenHeight / 2f) {
						Tile currentTile = MapTileHandler.tileTypes.get(tileNumber);
						g.drawImage(currentTile.image().getImage(), Math.round(screenX), Math.round(screenY), null);
					}
				})
		);
		IntStream.range(0, map.height()).parallel().forEach(worldRow ->
				IntStream.range(0, map.width()).parallel().forEach(worldCol -> {
					int tileNumber = map.layer2()[worldRow][worldCol];
					int worldX = worldCol * tileSize;
					int worldY = worldRow * tileSize;
					float screenX = worldX - camX + Game.screenWidth / 2f;
					float screenY = worldY - camY + Game.screenHeight / 2f;
					
					// Check if the tile is within the visible screen
					if (worldX + tileSize > camX - Game.screenWidth / 2f &&
							worldX - tileSize < camX + Game.screenWidth / 2f &&
							worldY + tileSize > camY - Game.screenHeight / 2f &&
							worldY - tileSize < camY + Game.screenHeight / 2f) {
						Tile currentTile = MapTileHandler.tileTypes.get(tileNumber);
						g.drawImage(currentTile.image().getImage(), Math.round(screenX), Math.round(screenY), null);
						
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
					float screenX = worldX - camX + Game.screenWidth / 2f;
					float screenY = worldY - camY + Game.screenHeight / 2f;
					
					// Check if the tile is within the visible screen
					if (worldX + tileSize > camX - Game.screenWidth / 2f &&
							worldX - tileSize < camX + Game.screenWidth / 2f &&
							worldY + tileSize > camY - Game.screenHeight / 2f &&
							worldY - tileSize < camY + Game.screenHeight / 2f) {
						Tile currentTile = MapTileHandler.tileTypes.get(tileNumber);
						g.drawImage(currentTile.image().getImage(), Math.round(screenX), Math.round(screenY), null);
					}
				})
		);
		
		Game.entities.forEach(entity -> entity.draw(g));
		
		Game.darkness.draw(g);
	}
}
