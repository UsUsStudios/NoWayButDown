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
    public static SpriteBatch batch;
    public static ShapeRenderer shapes;
    public static final Logger LOGGER = LoggerFactory.getLogger("NoWayButDown");

    // Game State
    public static States.GameStates gameState = States.GameStates.SPLASH;
    public static String currentMap = "";
    public static double transitionAlpha = 0;
    public static boolean debugMode = false;

    // Classes
    public static Random random = new Random();

    // Entities
    public static World world = new World();
    public static float cameraX = 0;
    public static float cameraY = 0;
    public static int playerId = 0;

    // Miscellaneous
    public static boolean running = true;
    public static int screenWidth = 1100;
    public static int screenHeight = 700;
    public static int tileSize = 48;
    public static String language = "english";
    public static String identifier = "nowaybutdown";
    public static String bottomMiddleText = "";  // Text for "press E to interact" and such

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
        SoundManager.loadLibrary();
        setupECSWorld();

        // Start!
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
                SoundManager.playMusic("Can't Go Up", true, false);
                gameState = States.GameStates.MAIN_MENU;
            } catch (InterruptedException e) {
                handleException(e);
            }
        }).start();

        LOGGER.info("Game started");
    }

    // This is run every frame
    @Override
    public void render() {
        bottomMiddleText = "";
        update();
        ScreenUtils.clear(0, 0, 0, 1);

        // Check the game state and call the appropriate draw method
        if (gameState == States.GameStates.PLAYING) {
            GameRendering.drawPlaying();
        }

        if (gameState != States.GameStates.SPLASH) world.render();

        // Draw UI based on game state
        switch (gameState) {
            case PLAYING -> GameRendering.drawPlayingUI();
            case MAIN_MENU -> GameRendering.drawTitle();
            case SPLASH -> GameRendering.drawSplash();
        }
    }

    public static void update() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) debugMode = !debugMode;

        if (gameState == States.GameStates.PLAYING) world.update();
        else GameRendering.updateUI();
    }

    // This is run when the window is closed
    @Override
    public void dispose() {
        batch.dispose();
        shapes.dispose();
        GameRendering.dispose();
        SoundManager.dispose();
        running = false;
        LOGGER.info("Game ended");
    }

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

    public static void handleException(Exception e) {
        QueueAppender.printError(e);
        running = false;
    }
}
