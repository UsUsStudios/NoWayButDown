package com.ususstudios.leveleditor;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;
import java.util.List;
import org.json.JSONObject;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.ususstudios.noway.main.UtilityTool;
import com.ususstudios.noway.rendering.Map;
import com.ususstudios.noway.rendering.MapTileHandler;
import com.ususstudios.noway.rendering.Tile;
import com.ususstudios.noway.rendering.UI;

public class Main implements Screen {

    static SpriteBatch batch;
    static Stage stage;
    static boolean inputEnabled = true;
    static ScreenViewport stageViewport;
    static ExtendViewport gameViewport;
    static OrthographicCamera gameCamera;
    static Texture pixel;
    static Texture greyPixel;
    static final int SIDEBAR_WIDTH = 268;

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
    static short tileID = 0;  // the selected tile

    static String filename = "main";
    static Map map = null;
    static List<Object> mapEntities = null;
    static TextField filenameField;

    private void loadMap() {
        filename = filenameField.getText();
        map = MapTileHandler.maps.get(filename);
        if (map == null) {
            filename = "disabled";
            map = MapTileHandler.maps.get("disabled");
            filenameField.setText("Not found!");
        }
        mapEntities = UtilityTool.getJsonObject("/values/maps/" + filename + ".json").getJSONArray("entities").toList();
    }

    private void saveMap() {
        filename = filenameField.getText();
        JSONObject mapJSON = new JSONObject(map);
        mapJSON.put("name", map.name());
        mapJSON.put("size", new int[]{ map.width(), map.height() });
        mapJSON.put("spawn", new int[]{ map.spawnX(), map. spawnY() });
        mapJSON.put("map", new String[]{ serializeLayer(map.layer1()), serializeLayer(map.layer2()), serializeLayer(map.layer3()) });
        mapJSON.put("songs", map.songs());
        mapJSON.put("entities", mapEntities);
        try {
            // this is quite hacky lol
            String workingDir = System.getProperty("user.dir");
            FileWriter fileWriter = new FileWriter(workingDir.substring(0, workingDir.length() - 7) + "/assets/values/maps/" + filename + ".json");
            fileWriter.write(mapJSON.toString());
            fileWriter.close();
        } catch (IOException e) { com.ususstudios.noway.Main.LOGGER.error("An error occurred while saving map to '/values/maps/" + filename
                                                                        + ".json: " + e.getMessage()); }
    }

    private String serializeLayer(short[][] layer) {
        StringBuilder string = new StringBuilder();
        ByteBuffer buf = ByteBuffer.allocate(layer.length * layer[0].length * 2)
                                       .order(ByteOrder.LITTLE_ENDIAN);
        Base64.Encoder enc = Base64.getEncoder();
        for (short[] row : layer) {
            for (short tile : row) buf.putShort(tile);
        }
        string.append(enc.encodeToString(buf.array()));
        return string.toString();
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        gameCamera = new OrthographicCamera();
        gameCamera.setToOrtho(true, gameWidth, gameHeight);
        gameViewport = new ExtendViewport(screenWidth - SIDEBAR_WIDTH, screenHeight, gameCamera);
        stageViewport = new ScreenViewport();
        stage = new Stage(stageViewport, batch);
        stage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (event.getTarget() != filenameField) {
                    stage.setKeyboardFocus(null);
                }
                return false;
            }
        });

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        pixel = new Texture(pixmap);
        pixmap.dispose();

        pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.DARK_GRAY);
        pixmap.fill();
        greyPixel = new Texture(pixmap);
        pixmap.dispose();

        MapTileHandler.loadMaps();
        MapTileHandler.loadTiles();

        buildSidebar();

        loadMap();

        Gdx.input.setInputProcessor(stage);
    }

    private void buildSidebar() {
        Table tileSelect = new Table();
        tileSelect.setPosition(Gdx.graphics.getWidth() - SIDEBAR_WIDTH, 0);
        tileSelect.setSize(SIDEBAR_WIDTH, Gdx.graphics.getHeight() / 2);
        tileSelect.top().left().pad(4);

        int buttonSize = 48;
        int columns = 5;
        int currColumn = 0;

        for (java.util.Map.Entry<Short, Tile> entry : MapTileHandler.tileTypes.entrySet()) {
            short thisTileId = entry.getKey();
            Tile tile = entry.getValue();

            TextureRegion region = new TextureRegion(tile.image().getTexture());
            region.flip(false, true);
            TextureRegionDrawable drawable = new TextureRegionDrawable(region);

            ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
            style.imageUp = drawable;
            style.imageChecked = drawable;

            ImageButton button = new ImageButton(style);
            button.setSize(buttonSize, buttonSize);

            button.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    tileID = thisTileId;
                    for (Actor a : tileSelect.getChildren()) {
                        if (a instanceof ImageButton b) b.getImage().setColor(Color.WHITE);
                    }
                    button.getImage().setColor(Color.CYAN);
                }
            });

            tileSelect.add(button).size(buttonSize).pad(2);
            currColumn += 1;
            if (currColumn >= columns) {
                tileSelect.row();
                currColumn = 0;
            }
        }

        ScrollPane scrollPane = new ScrollPane(tileSelect);
        scrollPane.setScrollingDisabled(false, false); // enable both axes
        scrollPane.setOverscroll(false, false);
        scrollPane.setFlingTime(0f); // disable fling/momentum if you want crisp scrolling
        scrollPane.setPosition(Gdx.graphics.getWidth() - SIDEBAR_WIDTH, Gdx.graphics.getHeight() / 2);
        scrollPane.setSize(SIDEBAR_WIDTH, Gdx.graphics.getHeight() / 2);

        stage.addActor(scrollPane);

        Table settingsBar = new Table();
        settingsBar.setPosition(Gdx.graphics.getWidth() - SIDEBAR_WIDTH, 0);
        settingsBar.setSize(SIDEBAR_WIDTH, Gdx.graphics.getHeight() / 2);
        settingsBar.top().left().pad(4);

        BitmapFont font = UI.getFont("FiraSans-Regular", 16, false);
        TextFieldStyle textFieldStyle = new TextFieldStyle();
        textFieldStyle.font = font;
        textFieldStyle.cursor = new TextureRegionDrawable(pixel);
        textFieldStyle.background = new TextureRegionDrawable(greyPixel);
        textFieldStyle.background.setLeftWidth(4);
        textFieldStyle.background.setRightWidth(4);
        textFieldStyle.fontColor = Color.WHITE;
        filenameField = new TextField(filename, textFieldStyle);
        filenameField.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                inputEnabled = !focused;
            }
        });

        settingsBar.add(filenameField).width(SIDEBAR_WIDTH / 2 - 4);

        TextButtonStyle buttonStyle = new TextButtonStyle();
        buttonStyle.font = font;

        TextButton loadButton = new TextButton("Load", buttonStyle);
        loadButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                loadMap();
            }
        });

        settingsBar.add(loadButton).width(SIDEBAR_WIDTH / 4 - 4);

        TextButton saveButton = new TextButton("Save", buttonStyle);
        loadButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                saveMap();
            }
        });

        settingsBar.add(saveButton).width(SIDEBAR_WIDTH / 4 - 4);
        settingsBar.row();

        stage.addActor(settingsBar);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameViewport.update(gameWidth, gameHeight);
        gameViewport.apply();

        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
        Gdx.gl.glScissor(0, 0, gameWidth, gameHeight);

        if (inputEnabled) handleInput(delta);
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
