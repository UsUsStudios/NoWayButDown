package com.ususstudios.noway;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ususstudios.noway.objects.GameObject;
import com.ususstudios.noway.objects.custom.Player;
import com.ususstudios.noway.main.*;
import com.ususstudios.noway.rendering.Darkness;
import com.ususstudios.noway.rendering.GameRendering;
import com.ususstudios.noway.rendering.MapTileHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Random;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    public static SpriteBatch batch;
    public static ShapeRenderer shapes;
    public static final Logger LOGGER = LoggerFactory.getLogger("NoWayButDown");

    // Game State
    public static States.GameStates gameState = States.GameStates.NULL;
    public static String currentMap = "";

    // Classes
    public static Darkness darkness;
    public static Random random = new Random();

    // Entities
    public static Player player;
    public static ArrayList<GameObject> objects = new ArrayList<>();

    // Miscellaneous
    public static boolean running = true;
    public static int screenWidth = 1100;
    public static int screenHeight = 700;
    public static int tileSize = 48;
    public static String language = "english";
    public static String identifier = "nowaybutdown";

    // This is run when the window is created
    @Override
    public void create() {
        LOGGER.info("Program started");
        batch = new SpriteBatch();
        shapes = new ShapeRenderer();

        // Load everything we need
        MapTileHandler.loadTiles();
        MapTileHandler.loadMaps();
        GameRendering.init();
        Sound.loadLibrary();

        Sound.playMusic("Can't Go Up");
        darkness = new Darkness();

        // Load the player and game
        player = new Player();
        objects.add(player);
        darkness.addLightSource(player);

        // Start!
        gameState = States.GameStates.MAIN_MENU;
        LOGGER.info("Game thread started");
    }

    // This is run every frame
    @Override
    public void render() {
        update();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Check the game state and call the appropriate draw method
        switch (gameState) {
            case PLAYING -> GameRendering.drawPlaying();
            case MAIN_MENU -> GameRendering.drawTitle();
        }
    }

    public static void update() {
        if (gameState == States.GameStates.PLAYING) Main.objects.forEach(GameObject::update);
        else GameRendering.updateUI();
    }

    // This is run when the window is closed
    @Override
    public void dispose() {
        batch.dispose();
        shapes.dispose();
        GameRendering.dispose();
        running = false;
        LOGGER.info("Game ended");
    }

    public static void loadMap(String map) {
        currentMap = map;
        player.setPosition(MapTileHandler.maps.get(map).spawnX(), MapTileHandler.maps.get(map).spawnY());
        gameState = States.GameStates.PLAYING;
        Sound.playMapMusic(currentMap);
        LOGGER.info("Map {} loaded", map);
    }

    public static void handleException(Exception e) {
        QueueAppender.printError(e);
        running = false;
    }
}
