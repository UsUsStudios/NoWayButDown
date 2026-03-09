// What are you doing, looking through my horrible code? You do not belong here.
package com.ususstudios.noway;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.ususstudios.noway.main.*;
import com.ususstudios.noway.rendering.*;
import com.ususstudios.noway.components.*;
import com.ususstudios.noway.systems.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Random;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    /** The com.badlogic.gdx.graphics.g2d.SpriteBatch used to render textures on the screen */
    public static SpriteBatch batch;
    /** The com.badlogic.gdx.graphics.glutils.ShapeRenderer used to render shapes and lines on the screen */
    public static ShapeRenderer shapes;
    /** The log4j logger used globally. */
    public static final Logger LOGGER = LoggerFactory.getLogger("NoWayButDown");

    // Game State
    /** The states that the game as a whole can be in */
    public static States.GameStates gameState = States.GameStates.SPLASH;
    /** The map ID of the map that is currently being rendered */
    public static String currentMap = "";
    /** The alpha value (0.0 - 1.0) of the black curtain faded in and out during transitions */
    public static double transitionAlpha = 0;
    /** Whether debug mode is on, which weakens darkness and displays collisions */
    public static boolean debugMode = false;

    // Classes
    /** Just used for random number generation */
    public static Random random = new Random();

    // Entities
    /** The {@link com.ususstudios.noway.main.World} that contains the ECS data of the current map */
    public static World world = new World();
    /** The x position of the center of the screen in the map (in pixels) */
    public static float cameraX = 0;
    /** The y position of the center of the screen in the map (in pixels) */
    public static float cameraY = 0;
    /** The entity ID of the player entity */
    public static int playerId = 0;

    // Miscellaneous
    /** The width of the game window in pixels */
    public static int screenWidth = 1100;
    /** The height of the game window in pixels */
    public static int screenHeight = 700;
    /** The height and length of tiles in pixels. Changing this is undefined behavior, so don't. */
    public static int tileSize = 48;
    /** Text for "press E to interact" and such */
    public static String bottomMiddleText = "";

    /** This is run when the window is created */
    @Override
    public void create() {
        LOGGER.info("Program started");
        batch = new SpriteBatch();
        shapes = new ShapeRenderer();

        // Load everything we need
        MapTileHandler.loadTiles();
        MapTileHandler.loadMaps();
        GameRendering.init();
        SoundManager.loadLibrary();
        UI.setup();
        setupECSWorld();


        // Start the splash screen
        new Thread(() -> {
            try {
                gameState = States.GameStates.SPLASH;
                while (transitionAlpha < 1) {
                    Thread.sleep(10);
                    transitionAlpha += 0.007f;
                }
                // Thread.sleep(1500);
                while (transitionAlpha > 0) {
                    Thread.sleep(10);
                    transitionAlpha -= 0.007f;
                }
                Thread.sleep(500);

                // Change to the main menu state
                SoundManager.playMusic("Can't Go Up", true, false);
                gameState = States.GameStates.MAIN_MENU;
                UI.uiState = "Title";

                // Load the main map so we can see it in the main menu
                currentMap = "main";
                world.getEntityComponent(playerId, PositionComponent.class).get()
                    .setPosition(MapTileHandler.maps.get(currentMap).spawnX(), MapTileHandler.maps.get(currentMap).spawnY());
                cameraX = MapTileHandler.maps.get(currentMap).spawnX() * tileSize;
                cameraY = MapTileHandler.maps.get(currentMap).spawnY() * tileSize;
                gameState = States.GameStates.MAIN_MENU;
                SoundManager.playMapMusic(currentMap);

                for (List<Component> entity : MapTileHandler.maps.get(currentMap).entities()) {
                    world.createEntity(entity.toArray(new Component[entity.size()]));
                }
            } catch (InterruptedException e) {
                handleException(e);
            }
        }).start();

        LOGGER.info("Game started");
    }

    /** This is run every frame */
    @Override
    public void render() {
        bottomMiddleText = "";
        update();
        ScreenUtils.clear(0, 0, 0, 1);

        if (gameState != States.GameStates.SPLASH) {
            // Draw the tiles and system
            GameRendering.drawPlaying();
            world.render();
        }

        // Draw UI based on game state
        switch (gameState) {
            case PLAYING -> GameRendering.drawPlayingUI();
            case MAIN_MENU -> { ScreenUtils.clear(0f, 0f, 0f, 0.1f); UI.stage.draw(); }  // TODO: make the curtain not transparent
            case SPLASH -> GameRendering.drawSplash();
        }
    }

    /** Updates the game and UI states */
    public static void update() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) debugMode = !debugMode;

        if (gameState == States.GameStates.PLAYING) world.update();
        else UI.update();
    }

    /** This is run when the window is closed */
    @Override
    public void dispose() {
        batch.dispose();
        shapes.dispose();
        GameRendering.dispose();
        SoundManager.dispose();
        LOGGER.info("Game ended");
    }

    /** Adds all the stuff that will be needed in the {@code world} like the player and systems */
    public static void setupECSWorld() {
        playerId = world.createEntity(new PlayerComponent(300), new PositionComponent(0f, 0f),
                new SpritesheetComponent("entity/player/player", 0, 1, 4, 5, 1f, 2f),
                new CollisionComponent(0.4f, 1.4f, 0.3f, 0.4f),
                new LightSourceComponent(0.8f, 125f, 0.1f, -.5f * Main.tileSize, -.2f * Main.tileSize));

        world.addUpdateSystem(new PlayerSystem());
        world.addUpdateSystem(new TriggerSystem());

        world.addRenderSystem(new SpritesheetSystem());
        world.addRenderSystem(new CollisionDrawingSystem());
        world.addRenderSystem(new TriggerDrawingSystem());
        world.addRenderSystem(new DarknessSystem());
    }

    /**
     * Changes the current map
     * @param map The name of the map from {@link com.ususstudios.noway.rendering.MapTileHandler}
     */
    public static void loadMap(String map) {
        currentMap = map;
        world.getEntityComponent(playerId, PositionComponent.class).get()
            .setPosition(MapTileHandler.maps.get(map).spawnX(), MapTileHandler.maps.get(map).spawnY());
        gameState = States.GameStates.PLAYING;
        SoundManager.playMapMusic(currentMap);

        for (List<Component> entity : MapTileHandler.maps.get(map).entities()) {
            world.createEntity(entity.toArray(new Component[entity.size()]));
        }

        LOGGER.info("Map '{}' loaded", map);
    }

    /**
     * Handles an exception. This is called by every place in the code that might raise an exception.
     * Currently it just stops the game, but I'm hoping to add an error dialog eventually.
     * @param e The exception that was raised.
     */
    public static void handleException(Exception e) {
        QueueAppender.printError(e);
        System.exit(0);
    }
}
