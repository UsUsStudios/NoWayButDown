package com.ususstudios.noway.rendering;

import com.ususstudios.noway.main.Game;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Objects;

// Loads maps and tile types
public class MapTileHandler {
	public static HashMap<String, Map> maps = new HashMap<>();
	public static HashMap<Integer, Tile> tileTypes = new HashMap<>();
	
	// Register all the tile types used in the game
	public static void loadTiles() {
		registerTile(0, "nothing", false);
		
		// Grass
		registerTile(10, "grass/grass_1", false);
		registerTile(11, "grass/grass_2", false);
		
		// Water
		registerTile(12, "water/water", false);
		registerTile(13, "water/white_line_water", false);
		registerTile(14, "water/water_corner_1", true);
		registerTile(15, "water/water_edge_3", true);
		registerTile(16, "water/water_corner_3", true);
		registerTile(17, "water/water_edge_4", true);
		registerTile(18, "water/water_edge_2", true);
		registerTile(19, "water/water_corner_2", true);
		registerTile(20, "water/water_edge_1", true);
		registerTile(21, "water/water_corner_4", true);
		registerTile(22, "water/water_outer_corner_1", true);
		registerTile(23, "water/water_outer_corner_2", true);
		registerTile(24, "water/water_outer_corner_3", true);
		registerTile(25, "water/water_outer_corner_4", true);
		
		// Path
		registerTile(26, "path/path", false);
		registerTile(27, "path/path_corner_1", false);
		registerTile(28, "path/path_edge_1", false);
		registerTile(29, "path/path_corner_2", false);
		registerTile(30, "path/path_edge_4", false);
		registerTile(31, "path/path_edge_2", false);
		registerTile(32, "path/path_corner_3", false);
		registerTile(33, "path/path_edge_3", false);
		registerTile(34, "path/path_corner_4", false);
		registerTile(35, "path/path_outer_corner_1", false);
		registerTile(36, "path/path_outer_corner_2", false);
		registerTile(37, "path/path_outer_corner_3", false);
		registerTile(38, "path/path_outer_corner_4", false);
		
		// Building Stuff
		registerTile(39, "floor", false);
		registerTile(40, "planks", true);
		
		// Tree
		registerTile(41, "tree/tree", true);
		
		// Event Tiles
		registerTile(42, "path/path_pit", false);
		registerTile(43, "grass/grass_pit", false);
		registerTile(44, "grass/grass_healing", false);
		registerTile(45, "coiner's_hut", false);
		
		// Dark Tiles
		registerTile(46, "tree/dark_tree", true);
		registerTile(47, "grass/dark_grass", false);
		
		registerTile(48, "tunnel_door", false);
		Game.LOGGER.info("Loaded {} tile images", tileTypes.size());
	}
	
	// Registers a tile type with the given ID, image name, and collision property
	public static void registerTile(int i, String imageName, boolean collision) {
		// Create a new tile and add it to the tileTypes HashMap, as well as store it in a field for later use.
		Tile tile = new Tile();
		tile.collision = collision;
		tileTypes.put(i, tile);

        // Entering try and catch zone because this part involves ImageIO.
        // The catch also has a NullPointerException because "requireNonNull" is used in the code
		try {
			// Get the imageStream that we will use for the tile. It's separately instantiated as it will be used for null checking
			InputStream imageStream = MapTileHandler.class.getResourceAsStream("/drawable/tile/" + imageName + ".png");

            // Make sure the imageStream is a member of "/drawable/tile/" and is a png. If not, use the disabledimageStream and warn.
            // The way we find out that is by checking if the imageStream is null. If it is, it's likely not a valid member.
            // However, if the imageName was just "", don't warn as it may be a result of an error.
			if (imageName.isEmpty()) {
				tile.image = ImageIO.read(Objects.requireNonNull(
						MapTileHandler.class.getResourceAsStream("/drawable/disabled.png")));
				return;
			}
			if (imageStream == null) {
				Game.LOGGER.warn("{} is not a valid member of \"/drawable/tiles\". ", imageName);
				tile.image = ImageIO.read(Objects.requireNonNull(
						MapTileHandler.class.getResourceAsStream("/drawable/disabled.png")));
				return;
			}
			
			// If all checks have passed, then set the tile's image to a scaled version of the imageStream
			tile.image = scaleImage(ImageIO.read(imageStream), Game.tileSize, Game.tileSize);
		} catch (IOException | NullPointerException e) {
			e.printStackTrace();
		}
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
		maps.put(name, mapObj);
	}
	
	// Scale an image to the specified width and height
	public static BufferedImage scaleImage(BufferedImage original, int width, int height) {
		BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics2D = scaledImage.createGraphics();
		graphics2D.drawImage(original, 0, 0, width, height, null);
		graphics2D.dispose();
		
		return scaledImage;
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
