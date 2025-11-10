package com.ususstudios.noway.rendering;

import com.ususstudios.noway.main.Game;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

// Loads maps and tile types
public class MapTileHandler {
	public static HashMap<String, Map> maps = new HashMap<>();
	public static HashMap<Integer, Tile> tileTypes = new HashMap<>();
	
	// Register all the tile types used in the game
	public static void loadTiles() {
		registerTile(0, "nothing", "00000/00000/00000/00000/00000");
		
		// Grass
		registerTile(10, "grass/grass_1", "00000/00000/00000/00000/00000");
		registerTile(11, "grass/grass_2", "00000/00000/00000/00000/00000");
		
		// Water
		registerTile(12, "water/water", "00000/00000/00000/00000/00000");
		registerTile(13, "water/white_line_water", "00000/00000/00000/00000/00000");
		registerTile(14, "water/water_corner_1", "00000/01111/01111/01111/01111");
		registerTile(19, "water/water_corner_2", "01111/01111/01111/01111/00000");
		registerTile(16, "water/water_corner_3", "00000/11110/11110/11110/11110");
		registerTile(21, "water/water_corner_4", "11110/11110/11110/11110/00000");
		registerTile(20, "water/water_edge_1", "11111/11111/11111/11111/00000");
		registerTile(18, "water/water_edge_2", "11110/11110/11110/11110/11110");
		registerTile(15, "water/water_edge_3", "00000/11111/11111/11111/11111");
		registerTile(17, "water/water_edge_4", "01111/01111/01111/01111/01111");
		registerTile(22, "water/water_outer_corner_1", "11111/11111/11111/11111/11110");
		registerTile(23, "water/water_outer_corner_2", "11111/11111/11111/11111/01111");
		registerTile(24, "water/water_outer_corner_3", "11110/11111/11111/11111/11111");
		registerTile(25, "water/water_outer_corner_4", "01111/11111/11111/11111/11111");
		
		// Path
		registerTile(26, "path/path", "00000/00000/00000/00000/00000");
		registerTile(27, "path/path_corner_1", "00000/00000/00000/00000/00000");
		registerTile(28, "path/path_edge_1", "00000/00000/00000/00000/00000");
		registerTile(29, "path/path_corner_2", "00000/00000/00000/00000/00000");
		registerTile(30, "path/path_edge_4", "00000/00000/00000/00000/00000");
		registerTile(31, "path/path_edge_2", "00000/00000/00000/00000/00000");
		registerTile(32, "path/path_corner_3", "00000/00000/00000/00000/00000");
		registerTile(33, "path/path_edge_3", "00000/00000/00000/00000/00000");
		registerTile(34, "path/path_corner_4", "00000/00000/00000/00000/00000");
		registerTile(35, "path/path_outer_corner_1", "00000/00000/00000/00000/00000");
		registerTile(36, "path/path_outer_corner_2", "00000/00000/00000/00000/00000");
		registerTile(37, "path/path_outer_corner_3", "00000/00000/00000/00000/00000");
		registerTile(38, "path/path_outer_corner_4", "00000/00000/00000/00000/00000");
		
		// Building Stuff
		registerTile(39, "floor", "00000/00000/00000/00000/00000");
		registerTile(40, "planks", "11111/11111/11111/11111/11111");
		
		// Tree
		registerTile(41, "tree/tree", "01110/11111/11111/11111/01110");
		
		// Event Tiles
		registerTile(42, "path/path_pit", "00000/00000/00000/00000/00000");
		registerTile(43, "grass/grass_pit", "00000/00000/00000/00000/00000");
		registerTile(44, "grass/grass_healing", "00000/00000/00000/00000/00000");
		registerTile(45, "coiner's_hut", "00000/00000/00000/00000/00000");
		
		// Dark Tiles
		registerTile(46, "tree/dark_tree", "11111/11111/11111/11111/11111");
		registerTile(47, "grass/dark_grass", "00000/00000/00000/00000/00000");
		
		registerTile(48, "tunnel_door", "00000/00000/00000/00000/00000");
		Game.LOGGER.info("Loaded {} tile images", tileTypes.size());
	}
	
	// Registers a tile type with the given ID, image name, and collision property
	public static void registerTile(int i, String imageName, String collision) {
		// Create a new tile and add it to the tileTypes HashMap, set its collision, and load the image
		boolean[][] collisionArray = new boolean[5][5];
		for (int row = 0; row < collision.split("/").length; row++) {
			for (int col = 0; col < collision.split("/")[row].length(); col++) {
				collisionArray[col][row] = collision.split("/")[row].charAt(col) == '1';
			}
		}
		// Load the image and scale it to the tileSize
		Image image = Image.loadImage("tile/" + imageName);
		image.scaleImage(Game.tileSize, Game.tileSize);
		
		// Register the tile
		Tile tile = new Tile(image, collisionArray);
		tileTypes.put(i, tile);
	}
	
	// Load all map files from the resources/values/maps/ directory
	public static void loadMaps() {
		String[] mapFiles = getResourceFileNames("/values/maps");
		for (String mapFile : mapFiles) {
			if (mapFile.endsWith(".json")) {
				String mapName = mapFile.substring(0, mapFile.lastIndexOf(".json"));
				loadMap(mapName);
			} else if (!mapFile.contains(".")) { // Check if it's a directory
				String[] subFiles = getResourceFileNames("/values/maps/" + mapFile);
				for (String subFile : subFiles) {
					if (subFile.endsWith(".json")) {
						String mapName = subFile.substring(0, subFile.lastIndexOf(".json"));
						loadMap(mapFile + "/" + mapName);
					}
				}
			}
		}
		Game.LOGGER.info("Loaded {} map files", maps.size());
	}
	
	// Load a specific map from a JSON file
	private static void loadMap(String fileName) {
		Game.LOGGER.info("Loading map: {}", fileName);
		JSONObject file = getJsonObject("/values/maps/" + fileName + ".json");
		if (file == null) {
			Game.LOGGER.error("Couldn't find /values/maps/{}.json", fileName);
			file = getJsonObject("/values/maps/disabled.json");
			if (file == null) {
				Game.LOGGER.error("Couldn't find /values/maps/disabled.json");
				return;
			}
		}
		
		// Get the basic map properties
		String name = file.getString("name");
		JSONArray map = file.getJSONArray("map");
		int width = file.getInt("width");
		int height = file.getInt("height");
		
		// Prepare the layers
		JSONArray lay1 = map.getJSONArray(0);
		int[][] layer1 = new int[height][width];
		int[][] layer2 = new int[height][width];
		int[][] layer3 = new int[height][width];
		
		// Load layer 1
		for (int y = 0; y < height; y++) {
			String[] row = lay1.getString(y).split(" ");
			for (int x = 0; x < width; x++) {
				layer1[y][x] = Integer.parseInt(row[x]);
			}
		}
		
		if (map.length() > 1) {
			// Load layer 2
			JSONArray lay2 = map.getJSONArray(1);
			for (int y = 0; y < height; y++) {
				String[] row = lay2.getString(y).split(" ");
				for (int x = 0; x < width; x++) {
					layer2[y][x] = Integer.parseInt(row[x]);
				}
			}
			if (map.length() > 2) {
				// Load layer 3
				JSONArray lay3 = map.getJSONArray(2);
				for (int y = 0; y < height; y++) {
					String[] row = lay3.getString(y).split(" ");
					for (int x = 0; x < width; x++) {
						layer3[y][x] = Integer.parseInt(row[x]);
					}
				}
			} else {
				// Load empty layer 3
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						layer3[y][x] = 0;
					}
				}
			}
		} else {
			// Load empty layer 2 and 3
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					layer2[y][x] = 0;
					layer3[y][x] = 0;
				}
			}
		}
		
		Map mapObj = new Map(name, width, height, layer1, layer2, layer3);
		maps.put(fileName, mapObj);
	}
	
	// Get the names of all resource files in a given directory
	public static String[] getResourceFileNames(String directoryPath) {
		try (InputStream inputStream = MapTileHandler.class.getResourceAsStream(directoryPath)) {
			if (inputStream == null) {
				Game.LOGGER.error("Warning: \"{}\" is not a valid resource file path.", directoryPath);
				return new String[0];
			}
			
			try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
				return bufferedReader.lines().toArray(String[]::new);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return new String[0];
		}
	}
	
	// Read a JSON object from a file in the resources
	public static JSONObject getJsonObject(String filePath) {
		try (InputStream inputStream = MapTileHandler.class.getResourceAsStream(filePath)) {
			if (inputStream == null) {
				Game.LOGGER.error("Warning: \"{}\" is not a valid path.", filePath);
				return null;
			}
			
			try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
				StringBuilder stringBuilder = new StringBuilder();
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					stringBuilder.append(line);
				}
				
				return new JSONObject(stringBuilder.toString());
			}
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}
}
