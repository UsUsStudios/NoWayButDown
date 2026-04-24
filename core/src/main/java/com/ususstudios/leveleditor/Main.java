package com.ususstudios.leveleditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ususstudios.noway.rendering.MapTileHandler;

public class Main implements Screen {

    public static SpriteBatch batch;
    private Stage stage;
    private ScreenViewport stageViewport;
    private ExtendViewport gameViewport;
    public static OrthographicCamera gameCamera;
    private static final int SIDEBAR_WIDTH = 200;

    static String currentMap = "main";
    public static float cameraX = 0;
    public static float cameraY = 0;
    public static int tileSize = 48;
    public static int screenWidth = 1000;
    public static int screenHeight = 600;
    static int gameWidth = Gdx.graphics.getWidth() - SIDEBAR_WIDTH;
    static int gameHeight = Gdx.graphics.getHeight();


    @Override
    public void show() {
        batch = new SpriteBatch();

        gameCamera = new OrthographicCamera();
        gameCamera.setToOrtho(true, gameWidth, gameHeight);
        gameViewport = new ExtendViewport(screenWidth - SIDEBAR_WIDTH, screenHeight, gameCamera);
        stageViewport = new ScreenViewport();
        stage = new Stage(stageViewport, batch);

        MapTileHandler.loadMaps();
        MapTileHandler.loadTiles();

        buildSidebar();

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        // multiplexer.addProcessor(gameInputProcessor);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void buildSidebar() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameViewport.update(gameWidth, gameHeight);
        gameViewport.apply();

        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
        Gdx.gl.glScissor(0, 0, gameWidth, gameHeight);

        Rendering.drawPlaying();

        Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);

        stageViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        stageViewport.apply();
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        gameViewport.update(width - SIDEBAR_WIDTH, height);
        stageViewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        batch.dispose();
    }

    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
}
