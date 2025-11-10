// Copyright (c) 2025 DingleTheRat. All Rights Reserved.
package com.ususstudios.noway.entity;

import com.ususstudios.noway.main.Game;
import com.ususstudios.noway.rendering.Map;
import com.ususstudios.noway.rendering.MapTileHandler;

public class CollisionChecker {
	// Check if two entities are colliding
	public static boolean check2EntityCollision(Entity a, Entity b) {
		// treat a.x/a.y and b.x/b.y as centers
		float halfW = (a.width + b.width) / 2f;
		float halfH = (a.height + b.height) / 2f;
		return Math.abs(a.x - b.x) <= halfW && Math.abs(a.y - b.y) <= halfH;
	}
	
	// Check if an entity is colliding with a block defined by collisionPoints at world position (x, y)
	public static boolean checkBlockCollision(Entity e, boolean[][] collisionPoints, float x, float y) {
		// tile positions are from the top-left corner; use tile center for broad-phase
		float tileHalfW = Game.tileSize / 2f;
		float tileCenterX = x + tileHalfW;
		float tileCenterY = y + tileHalfW;
		
		float xDiff = Math.abs(e.x - tileCenterX);
		float yDiff = Math.abs(e.y - tileCenterY);
		
		// use half-extents for the entity in the broad-phase test
		if (xDiff <= e.width / 2f + tileHalfW && yDiff <= e.height /2f + tileHalfW) {
			int gridX = collisionPoints.length;
			int gridY = collisionPoints[0].length;
			float cellW = Game.tileSize / (float) gridX;
			float cellH = Game.tileSize / (float) gridY;
			for (int i = 0; i < gridX; i++) {
				for (int j = 0; j < gridY; j++) {
					if (collisionPoints[i][j]) {
						float halfW = e.width / 2f;
						float halfH = e.height / 2f;
						// compute world-space center of the sub-cell (tile top-left + cell offset)
						float px = x + i * cellW + cellW / 2f;
						float py = y + j * cellH + cellH / 2f;
						if (Math.abs(px - e.x) <= halfW && Math.abs(py - e.y) <= halfH) return true;
					}
				}
			}
		}
		return false;
	}
	
	// Check if an entity is colliding with any other entity or block
	public static boolean checkEntityColliding(Entity e) {
		for (Entity other : Game.entities) {
			if (other != e) {
				if (check2EntityCollision(e, other)) {
					return true;
				}
			}
		}
		Map map = MapTileHandler.maps.get(Game.currentMap);
		for (int row = 0; row < map.height(); row++) {
			for (int col = 0; col < map.width(); col++) {
				int tileNumber = map.layer2()[col][row];
				boolean[][] collisionPoints = MapTileHandler.tileTypes.get(tileNumber).collision();
				int worldX = col * Game.tileSize - Game.tileSize / 2;
				int worldY = row * Game.tileSize - Game.tileSize / 2;
				if (checkBlockCollision(e, collisionPoints, worldX, worldY)) return true;
			}
		}
		return false;
	}
}
