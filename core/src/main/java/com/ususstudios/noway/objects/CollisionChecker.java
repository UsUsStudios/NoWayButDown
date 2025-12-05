package com.ususstudios.noway.objects;

import com.ususstudios.noway.Main;
import com.ususstudios.noway.rendering.Map;
import com.ususstudios.noway.rendering.MapTileHandler;

public class CollisionChecker {
	// Check if two objects are colliding
	public static boolean check2EntityCollision(GameObject a, GameObject b) {
		float aLeft = a.x;
		float aTop = a.y - a.height / 2;
		float aRight = a.x + a.width;
		float aBottom = a.y + a.height / 2;

		float bLeft = b.x;
		float bTop = b.y - b.height / 2;
		float bRight = b.x + b.width;
		float bBottom = b.y + b.height / 2;

		return aLeft < bRight && aRight > bLeft && aTop < bBottom && aBottom > bTop;
	}

	public static boolean checkBlockCollision(Entity e, boolean[][] collisionPoints, float x, float y) {
		int gridX = collisionPoints.length;      // number of columns in collision grid
		int gridY = collisionPoints[0].length;   // number of rows in collision grid
		float cellW = Main.tileSize / (float) gridX;
		float cellH = Main.tileSize / (float) gridY;

		for (int i = 0; i < gridX; i++) {
			for (int j = 0; j < gridY; j++) {
				if (collisionPoints[i][j]) {
					float halfW = e.width / 2f;
					float halfH = e.height / 2f;

					float px = x + i * cellW + cellW / 2f;
					float py = y + j * cellH + cellH / 2f;
					if (Math.abs(px - e.x) <= halfW && Math.abs(py - (e.y + halfW + 10)) <= halfH) return true;
				}
			}
		}

		return false;
	}

	// Check if objects collides with any other objects or any tile on layer2
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
				int worldX = col * Main.tileSize - Main.tileSize / 2; // tile top-left X
				int worldY = row * Main.tileSize - Main.tileSize / 2; // tile top-left Y

				if (checkBlockCollision(e, collisionPoints, worldX, worldY)) return true;
			}
		}

		return false;
	}
}
