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

import java.math.BigDecimal;
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
        setupECSWorld();

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
        update();
        ScreenUtils.clear(0, 0, 0, 1);

        // Check the game state and call the appropriate draw method
        switch (gameState) {
            case PLAYING -> GameRendering.drawPlaying();
            case MAIN_MENU -> GameRendering.drawTitle();
            case SPLASH -> GameRendering.drawSplash();
        }

        world.render();
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
        running = false;
        LOGGER.info("Game ended");
    }

    public static void setupECSWorld() {
        playerId = world.createEntity(new PlayerComponent(BigDecimal.valueOf(300)),
                new PositionComponent(BigDecimal.valueOf(0f), BigDecimal.valueOf(0f)),
                new SpritesheetComponent("entity/player/player", 0, 1, 4, 5, BigDecimal.valueOf(1f), BigDecimal.valueOf(2f)),
                new CollisionComponent(BigDecimal.valueOf(0.4f), BigDecimal.valueOf(1.4f), BigDecimal.valueOf(0.3f), BigDecimal.valueOf(0.4f)));

        world.addUpdateSystem(new PlayerSystem());

        world.addRenderSystem(new SpritesheetSystem());
        world.addRenderSystem(new CollisionDrawingSystem());
    }

    public static void loadMap(String map) {
        currentMap = map;
        world.getEntityComponent(playerId, PositionComponent.class).get()
            .setPosition(MapTileHandler.maps.get(map).spawnX(), MapTileHandler.maps.get(map).spawnY());
        gameState = States.GameStates.PLAYING;
        Sound.playMapMusic(currentMap);

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
