package com.ususstudios.noway.rendering;

import com.ususstudios.noway.Main;
import com.ususstudios.noway.main.UtilityTool;
import org.json.JSONArray;
import org.json.JSONObject;
import com.ususstudios.noway.components.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.*;

/** A static class that loads maps and tile types */
public class MapTileHandler {
    /** A HashMap of all the {@link com.ususstudios.noway.rendering.Map}s and their names */
	public static HashMap<String, Map> maps = new HashMap<>();
    /** A HashMap of all the tile types and their 2-digit base 64 tile IDs */
	public static HashMap<String, Tile> tileTypes = new HashMap<>();

	/** Register all the tile types used in the game */
	public static void loadTiles() {
		registerTile("AA", "nothing", "00000/00000/00000/00000/00000");

		// Grass
		registerTile("AB", "grass/grass_1", "00000/00000/00000/00000/00000");
		registerTile("AC", "grass/grass_2", "00000/00000/00000/00000/00000");

		// Water
		registerTile("AD", "water/water", "00000/00000/00000/00000/00000");
		registerTile("AE", "water/white_line_water", "00000/00000/00000/00000/00000");
		registerTile("AF", "water/water_corner_1", "00000/01111/01111/01111/01111");
		registerTile("AG", "water/water_corner_2", "01111/01111/01111/01111/00000");
		registerTile("AH", "water/water_corner_3", "00000/11110/11110/11110/11110");
		registerTile("AI", "water/water_corner_4", "11110/11110/11110/11110/00000");
		registerTile("AJ", "water/water_edge_1", "11111/11111/11111/11111/00000");
		registerTile("AK", "water/water_edge_2", "11110/11110/11110/11110/11110");
		registerTile("AL", "water/water_edge_3", "00000/11111/11111/11111/11111");
		registerTile("AM", "water/water_edge_4", "01111/01111/01111/01111/01111");
		registerTile("AN", "water/water_outer_corner_1", "11111/11111/11111/11111/11110");
		registerTile("AO", "water/water_outer_corner_2", "11111/11111/11111/11111/01111");
		registerTile("AP", "water/water_outer_corner_3", "11110/11111/11111/11111/11111");
		registerTile("AQ", "water/water_outer_corner_4", "01111/11111/11111/11111/11111");

		// Path
		registerTile("AR", "path/path", "00000/00000/00000/00000/00000");
		registerTile("AS", "path/path_corner_1", "00000/00000/00000/00000/00000");
		registerTile("AT", "path/path_edge_1", "00000/00000/00000/00000/00000");
		registerTile("AU", "path/path_corner_2", "00000/00000/00000/00000/00000");
		registerTile("AV", "path/path_edge_4", "00000/00000/00000/00000/00000");
		registerTile("AW", "path/path_edge_2", "00000/00000/00000/00000/00000");
		registerTile("AX", "path/path_corner_3", "00000/00000/00000/00000/00000");
		registerTile("AY", "path/path_edge_3", "00000/00000/00000/00000/00000");
		registerTile("AZ", "path/path_corner_4", "00000/00000/00000/00000/00000");
		registerTile("Aa", "path/path_outer_corner_1", "00000/00000/00000/00000/00000");
		registerTile("Ab", "path/path_outer_corner_2", "00000/00000/00000/00000/00000");
		registerTile("Ac", "path/path_outer_corner_3", "00000/00000/00000/00000/00000");
		registerTile("Ad", "path/path_outer_corner_4", "00000/00000/00000/00000/00000");

		// Building Stuff
		registerTile("Ae", "floor", "00000/00000/00000/00000/00000");
		registerTile("Af", "planks", "11111/11111/11111/11111/11111");

		// Tree
		registerTile("Ag", "tree/tree", "01110/11111/11111/11111/01110");

		// Event Tiles
		registerTile("Ah", "path/path_pit", "00000/00000/00000/00000/00000");
		registerTile("Ai", "grass/grass_pit", "00000/00000/00000/00000/00000");
		registerTile("Aj", "grass/grass_healing", "00000/00000/00000/00000/00000");
		registerTile("Ak", "coiner's_hut", "00000/00000/00000/00000/00000");

		// Dark Tiles
		registerTile("Al","tree/dark_tree", "11111/11111/11111/11111/11111");
		registerTile("Am","grass/dark_grass", "00000/00000/00000/00000/00000");

    	registerTile("An","tunnel_door", "00000/00000/00000/00000/00000");
		Main.LOGGER.info("Loaded {} tile images", tileTypes.size());
	}

	/**
     * Registers a tile type with the given ID (in 2-digit Base64), image name, and collision property
     * @param id The 2-digit base 64 tile ID
     * @param imageName The path to the image file from /assets/drawable/tile/
     * @param collision The collision points, with rows of 0 for off and 1 for on, and rows split with /
     */
	public static void registerTile(String id, String imageName, String collision) {
		// Create a new tile and add it to the tileTypes HashMap, set its collision, and load the image
		boolean[][] collisionArray = new boolean[5][5];
		for (int row = 0; row < collision.split("/").length; row++) {
			for (int col = 0; col < collision.split("/")[row].length(); col++) {
				collisionArray[col][row] = collision.split("/")[row].charAt(col) == '1';
			}
		}
		// Load the image and scale it to the tileSize
		Image image = Image.loadImage("tile/" + imageName);
		image.scaleImage(Main.tileSize, Main.tileSize);

		// Register the tile
		Tile tile = new Tile(image, collisionArray);
		tileTypes.put(id, tile);
	}

	/** Load all map files from the /assets/values/maps/ directory */
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
		Main.LOGGER.info("Loaded {} map files", maps.size());
	}

	/**
     * Load a specific map from a JSON file
     * @param fileName The path to the JSON file, starting from /assets/values/maps/
     */
	private static void loadMap(String fileName) {
		Main.LOGGER.info("Loading map: {}", fileName);
		JSONObject file = UtilityTool.getJsonObject("/values/maps/" + fileName + ".json");
		if (file == null) {
			Main.LOGGER.error("Couldn't find /values/maps/{}.json", fileName);
			file = UtilityTool.getJsonObject("/values/maps/disabled.json");
			if (file == null) {
				Main.LOGGER.error("Couldn't find /values/maps/disabled.json");
				return;
			}
		}

		// Get the basic map properties
		String name = file.getString("name");
		JSONArray map = file.getJSONArray("map");
		JSONArray size = file.getJSONArray("size");
		JSONArray spawn = file.getJSONArray("spawn");
		int width = size.getInt(0);
		int height = size.getInt(1);
		int spawnX = spawn.getInt(0);
		int spawnY = spawn.getInt(1);
		JSONArray songs = file.getJSONArray("songs");

		// Prepare the layers
		JSONArray lay1 = map.getJSONArray(0);
		String[][] layer1 = new String[height][width];
		String[][] layer2 = new String[height][width];
		String[][] layer3 = new String[height][width];

		// Load layer 1
		for (int y = 0; y < height; y++) {
			String[] row = lay1.getString(y).split(" ");
            System.arraycopy(row, 0, layer1[y], 0, width);
		}

		if (map.length() > 1) {
			// Load layer 2
			JSONArray lay2 = map.getJSONArray(1);
			for (int y = 0; y < height; y++) {
				String[] row = lay2.getString(y).split(" ");
                System.arraycopy(row, 0, layer2[y], 0, width);
			}
			if (map.length() > 2) {
				// Load layer 3
				JSONArray lay3 = map.getJSONArray(2);
				for (int y = 0; y < height; y++) {
					String[] row = lay3.getString(y).split(" ");
                    System.arraycopy(row, 0, layer3[y], 0, width);
				}
			} else {
				// Load empty layer 3
				for (int y = 0; y < height; y++) {
					for (int x = 0; x < width; x++) {
						layer3[y][x] = "AA";
					}
				}
			}
		} else {
			// Load empty layer 2 and 3
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					layer2[y][x] = "AA";
					layer3[y][x] = "AA";
				}
			}
		}

        JSONArray entitiesArray = file.getJSONArray("entities");
        ArrayList<List<Component>> entities = new ArrayList<>();
        for (int i = 0; i < entitiesArray.length(); i++) {
            JSONArray entityArray = entitiesArray.getJSONArray(i);
            ArrayList<Component> entity = new ArrayList<>();
            for (int j = 0; j < entityArray.length(); j++) {
                JSONArray componentArray = entityArray.getJSONArray(j);
                Class componentClass = Component.class;
                try {
                    componentClass = Class.forName(componentArray.getString(0));
                } catch (ClassNotFoundException e) {
                    Main.LOGGER.error("Class not found: " + componentArray.getString(0));
                    Main.handleException(e);
                }

                Class<?>[] parameterTypes = componentArray
                    .toList()
                    .subList(1, componentArray.length())
                    .stream()
                    .map(Object::getClass)
                    .toArray(Class<?>[]::new);

                Constructor constructor = componentClass.getDeclaredConstructors()[0];
                try {
                    constructor = componentClass.getConstructor(parameterTypes);
                } catch (NoSuchMethodException e) {
                    Main.handleException(e);
                    System.exit(0);
                }

                try {
                    Component component = (Component) constructor.newInstance(componentArray
                            .toList()
                            .subList(1, componentArray.length())
                            .toArray());
                    entity.add(component);
                } catch (Exception e) {
                    Main.handleException(e);
                }
            }
            entities.add(entity);
        }

		Map mapObj = new Map(name, width, height, spawnX, spawnY, layer1, layer2, layer3,
            songs.toList(), entities);
		maps.put(fileName, mapObj);
	}

	/**
     * A utility method that gets the names of all resource files in a given directory
     * @param directoryPath The path of the resource directory you want to check
     * @return The list of file names in that directory
     */
	public static String[] getResourceFileNames(String directoryPath) {
		try (InputStream inputStream = MapTileHandler.class.getResourceAsStream(directoryPath)) {
			if (inputStream == null) {
				Main.LOGGER.error("Warning: \"{}\" is not a valid resource file path.", directoryPath);
				return new String[0];
			}

			try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
				return bufferedReader.lines().toArray(String[]::new);
			}
		} catch (IOException e) {
			Main.handleException(e);
			return new String[0];
		}
	}
}
