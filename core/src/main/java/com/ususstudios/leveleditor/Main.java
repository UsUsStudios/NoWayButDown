package com.ususstudios.leveleditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ususstudios.noway.rendering.Map;
import com.ususstudios.noway.rendering.MapTileHandler;
import com.ususstudios.noway.rendering.Tile;

public class Main implements Screen {

    static SpriteBatch batch;
    static Stage stage;
    static ScreenViewport stageViewport;
    static ExtendViewport gameViewport;
    static OrthographicCamera gameCamera;
    static Texture pixel;
    static final int SIDEBAR_WIDTH = 200;

    static String currentMap = "main";
    static float cameraX = 0;
    static float cameraY = 0;
    static int tileSize = 48;
    static int screenWidth = 1000;
    static int screenHeight = 600;
    static int gameWidth = Gdx.graphics.getWidth() - SIDEBAR_WIDTH;
    static int gameHeight = Gdx.graphics.getHeight();
    static int[] mouseTile = {0, 0};
    static int layer = 0;
    static short tileID = 0;

    @Override
    public void show() {
        batch = new SpriteBatch();

        gameCamera = new OrthographicCamera();
        gameCamera.setToOrtho(true, gameWidth, gameHeight);
        gameViewport = new ExtendViewport(screenWidth - SIDEBAR_WIDTH, screenHeight, gameCamera);
        stageViewport = new ScreenViewport();
        stage = new Stage(stageViewport, batch);

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        pixel = new Texture(pixmap);
        pixmap.dispose();

        MapTileHandler.loadMaps();
        MapTileHandler.loadTiles();

        buildSidebar();

        Gdx.input.setInputProcessor(stage);
    }

    private void buildSidebar() {
        Table sidebar = new Table();
        sidebar.setPosition(Gdx.graphics.getWidth() - SIDEBAR_WIDTH, 0);
        sidebar.setSize(SIDEBAR_WIDTH, Gdx.graphics.getHeight());
        sidebar.top().left().pad(4);

        int buttonSize = 48;
        int columns = 5;
        int currColumn = 0;

        for (java.util.Map.Entry<Short, Tile> entry : MapTileHandler.tileTypes.entrySet()) {
            short thisTileId = entry.getKey();
            Tile tile = entry.getValue();

            TextureRegionDrawable drawable = new TextureRegionDrawable(tile.image().getTexture());

            ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
            style.imageUp = drawable;
            style.imageChecked = drawable;

            ImageButton button = new ImageButton(style);
            button.setSize(buttonSize, buttonSize);

            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    tileID = thisTileId;
                    for (Actor a : sidebar.getChildren()) {
                        if (a instanceof ImageButton b) b.getImage().setColor(Color.WHITE);
                    }
                    button.getImage().setColor(Color.CYAN);
                }
            });

            sidebar.add(button).size(buttonSize).pad(2);
            currColumn += 1;
            if (currColumn >= columns) {
                sidebar.row();
                currColumn = 0;
            }
        }

        ScrollPane scrollPane = new ScrollPane(sidebar);
        scrollPane.setScrollingDisabled(false, false); // enable both axes
        scrollPane.setOverscroll(false, false);
        scrollPane.setFlingTime(0f); // disable fling/momentum if you want crisp scrolling
        scrollPane.setPosition(Gdx.graphics.getWidth() - SIDEBAR_WIDTH, 0);
        scrollPane.setSize(SIDEBAR_WIDTH, Gdx.graphics.getHeight());

        stage.addActor(scrollPane);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameViewport.update(gameWidth, gameHeight);
        gameViewport.apply();

        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
        Gdx.gl.glScissor(0, 0, gameWidth, gameHeight);

        handleInput(delta);
        Rendering.drawPlaying();

        Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);

        stageViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        stageViewport.apply();
        stage.act(delta);
        stage.draw();
    }

    private void handleInput(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0)) layer = 0;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) layer = 1;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) layer = 2;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) layer = 3;

        float inputX = Gdx.input.getX() * (float) Main.screenWidth / (float) (Main.screenWidth - Main.SIDEBAR_WIDTH);
        Vector3 mouseScreen = new Vector3(inputX, Gdx.input.getY(), 0);
        Vector3 mouseWorld = Main.gameCamera.unproject(mouseScreen);

        mouseTile[1] = (int) Math.floor(mouseWorld.x / Main.tileSize);
        mouseTile[0] = (int) Math.floor(mouseWorld.y / Main.tileSize);
        handleClick();

        int xAxis = 0;
        int yAxis = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) yAxis -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) yAxis += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) xAxis -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) xAxis += 1;

        float mul = 1.41f;
        if (xAxis != 0 && yAxis != 0) mul = 1;
        cameraX += xAxis * 300 * delta * mul;
        cameraY += yAxis * 300 * delta * mul;

        Map map = MapTileHandler.maps.get(currentMap);
        int maxCameraX = map.width() * tileSize - (screenWidth - SIDEBAR_WIDTH) / 2 - tileSize / 2;
        int maxCameraY = map.height() * tileSize - screenHeight / 2 - tileSize / 2;
        float minCameraX = (screenWidth - SIDEBAR_WIDTH) / 2f + tileSize / 2;
        float minCameraY = screenHeight / 2f + tileSize / 2;
        if (cameraX < minCameraX) cameraX = minCameraX;
        if (cameraY < minCameraY) cameraY = minCameraY;
        if (cameraX > maxCameraX) cameraX = maxCameraX;
        if (cameraY > maxCameraY) cameraY = maxCameraY;
    }

    private void handleClick() {
        if (!checkMouseTile()) return;

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {}
        if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {}
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {

        }
    }

    private boolean checkMouseTile() {
        Map map = MapTileHandler.maps.get(currentMap);
        if (mouseTile[0] > map.width() - 1) return false;
        if (mouseTile[1] > map.height() - 1) return false;
        if (mouseTile[0] < 0) return false;
        if (mouseTile[1] < 0) return false;
        return true;
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

    public static void drawRect(float x, float y, float width, float height, float thickness, Color color) {
        batch.setColor(color);
        batch.draw(pixel, x, y, width, thickness);
        batch.draw(pixel, x, y + height - thickness, width, thickness);
        batch.draw(pixel, x, y, thickness, height);
        batch.draw(pixel, x + width - thickness, y, thickness, height);
        batch.setColor(Color.WHITE); // reset so other draws aren't tinted
    }

    @Override public void hide() {}
    @Override public void pause() {}
    @Override public void resume() {}
}
