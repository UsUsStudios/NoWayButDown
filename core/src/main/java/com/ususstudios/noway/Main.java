package com.ususstudios.noway;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
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
    public static States.GameStates gameState = States.GameStates.SPLASH;
    public static String currentMap = "";
    public static double transitionAlpha = 0;

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
        GameObject.registerGameObjectTypes();
        darkness = new Darkness();

        // Load the player and game
        player = (Player) GameObject.createGameObject("Player");
        objects.add(player);
        darkness.addLightSource(player);

        // Start!
        new Thread(() -> {
            try {
                while (transitionAlpha < 1) {
                    Thread.sleep(10);
                    transitionAlpha += 0.007f;
                }
                Thread.sleep(1500);
                while (transitionAlpha > 0) {
                    Thread.sleep(10);
                    transitionAlpha -= 0.007f;
                }
                Thread.sleep(500);
                Sound.playMusic("Can't Go Up");
                gameState = States.GameStates.MAIN_MENU;
            } catch (InterruptedException e) {
                handleException(e);
            }
        });  // .start();

        gameState = States.GameStates.MAIN_MENU;
        LOGGER.info("Game started");
    }

    // This is run every frame
    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);

        // Check the game state and call the appropriate draw method
        switch (gameState) {
            case PLAYING -> GameRendering.drawPlaying();
            case MAIN_MENU -> GameRendering.drawTitle();
            case SPLASH -> GameRendering.drawSplash();
        }
        update();
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

        for (int i = 0; i < MapTileHandler.maps.get(map).objectNames().size(); i++) {
            GameObject obj = GameObject.createGameObject(MapTileHandler.maps.get(map).objectNames().get(i));
            obj.setPosition(MapTileHandler.maps.get(map).objectPos().get(i));
            obj.properties = MapTileHandler.maps.get(map).objectProperties().get(i);
            obj.setup();
            System.out.println(obj.properties);
            Main.objects.add(obj);
        }

        LOGGER.info("Map '{}' loaded", map);
    }

    public static void handleException(Exception e) {
        QueueAppender.printError(e);
        running = false;
    }
}
