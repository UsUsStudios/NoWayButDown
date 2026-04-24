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
import com.ususstudios.noway.rendering.Image;

public class Main implements Screen {

    private SpriteBatch batch;
    private Stage stage;
    private ScreenViewport stageViewport;
    private ExtendViewport gameViewport;
    private OrthographicCamera gameCamera;
    private static final int SIDEBAR_WIDTH = 200;

    Image image;

    @Override
    public void show() {
        batch = new SpriteBatch();

        gameCamera = new OrthographicCamera();
        gameViewport = new ExtendViewport(800, 600, gameCamera);
        stageViewport = new ScreenViewport();
        stage = new Stage(stageViewport, batch);
        image = Image.loadImage("disabled");
        image.scaleImage(1000, 1000);

        buildSidebar();

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        // multiplexer.addProcessor(gameInputProcessor);
        Gdx.input.setInputProcessor(multiplexer);
    }

    public void buildSidebar() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        int gameWidth = Gdx.graphics.getWidth() - SIDEBAR_WIDTH;
        int gameHeight = Gdx.graphics.getHeight();

        gameViewport.update(gameWidth, gameHeight);
        gameViewport.apply();

        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
        Gdx.gl.glScissor(0, 0, gameWidth, gameHeight);

        batch.setProjectionMatrix(gameCamera.combined);
        batch.begin();
        batch.draw(image.getTexture(), -375, -300);
        batch.end();

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
