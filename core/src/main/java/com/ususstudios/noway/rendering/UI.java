package com.ususstudios.noway.rendering;

import java.util.HashMap;
import com.ususstudios.noway.Main;
import com.ususstudios.noway.components.*;
import com.ususstudios.noway.main.SoundManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class UI {
    /** The font cache! Used to save performance instead of loading the same font over and over again. */
    private final static HashMap<String, HashMap<Integer, BitmapFont>> fontCache = new HashMap<>();
    /**
     * The hashMap that holds all UI components for each UI state.
     * Create a UI state by simple making an entry into this hashmap with the key being the name of the uiState.
     * All the components listed in the ArrayList will be enabled once the UIState variable is equal to the name.
     **/
    public static final HashMap<String, Table> uiStates = new HashMap<>();
    /** Each actor in the {@code stage} puts their update method here if needed to update every tick. */
    public static final HashMap<String, Runnable> uiUpdates = new HashMap<>();
    /** Depending on what the string is, it will display the corresponding components inside the uiStates HashMap. */
    public static String uiState = "";
    /** The UI state of the previous tick, for checking if it was changed. */
    private static String currentUIstate = "";
    /** The LibGDX thing that handles buttons for me. */
    public static Stage stage;

    /**
     * Sets up the UI class, so it functions properly.
     * <p>
     * First, this method loads the {@link Stage} that will display UI elements.
     * The stage is also added as an input processor to process mouse clicks.
     * Secondly, the method calls setup methods for each UI state.
     * These methods initialize their state's elements and add them to the {@code UIStates} hashmap to be displayed.
     **/
    public static void setup() {
        // Create the stage
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        // Setup (almost) all uiStates
        titleScreen();
        debugState();

        Main.LOGGER.info("Loaded UI");
    }

    /**
     * Generates a specific font with a specific size if one isn't in a cache already.
     * @param fileName The name of the font's file (.ttf).
     * The font is taken from the {@code /assets/font/} directory inside the assets' directory.
     * @param size The size of the font that will be generated.
     * @param flipped Whether to flip the font over the x-axis.
     * @return A {@link BitmapFont} which is the generated font (the final product).
     * If the font was already generated, it will be taken from a cache to save resources.
     **/
    public static BitmapFont getFont(String fileName, int size, boolean flipped) {
        // If the font has already been created, get it from the cache
        if (fontCache.containsKey(fileName) && fontCache.get(fileName).containsKey(size))
            return fontCache.get(fileName).get(size);

        // If not, we'll have to make it
        // Load in the font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/" + fileName + ".ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        // Set the parameters
        parameter.size = size;
        parameter.flip = flipped;

        // Generate the font and dispose of the evidence
        BitmapFont font = generator.generateFont(parameter);
        generator.dispose();

        // Store the font in a fontCache
        fontCache.putIfAbsent(fileName, new HashMap<>());
        fontCache.get(fileName).put(size, font);

        return font;
    }

    /**
     * Updates the currently displayed components.
     * Displayed components are obtained from the {@code uiStates} HashMap.
     * If a key of an entry is equal to the {@code uiState} field, then it's displayed
     **/
    public static void update() {
        /*
         * If the currentUIstate isn't equal to the uiState, meaning the uiState has changed, so update the active table.
         * This is done to save performance, as there is no point in updating the table after the uiState changed.
         */
        if (!currentUIstate.equals(uiState)) {
            // Clear the stage to prepare for the next table
            stage.clear();

            // Update the stage with all the actors that are in the uiState if they have a table in the uiStates HashMap
            uiStates.entrySet().stream()
                .filter(entry -> uiState.contains(entry.getKey()))
                .forEach(entry -> stage.addActor(entry.getValue()));

            // Finally, update the currentUIstate
            currentUIstate = uiState;
        }

        // Run updates for the current actors in the uiState if they have an update method in the uiUpdates HashMap
        uiUpdates.entrySet().stream()
            .filter(entry -> uiState.contains(entry.getKey()))
            .forEach(entry -> entry.getValue().run());

        stage.act();
    }

    /** Sets up the elements for the title screen. */
    public static void titleScreen() {
        // Create a new table to hold all the items
        Table table = new Table();

        // Position it on the Y axis (e.g., 200 px from bottom)
        table.setY(350);

        // Center the table
        table.setPosition((stage.getWidth() - table.getPrefWidth()) / 2f - 50, table.getY());

        // Create the style that will be used for the title
        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.fontColor = Color.valueOf("#3c1fc0");
        titleStyle.font = getFont("FiraSans-Bold", 112, false);

        // Create the style that will be used for the buttons
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.font = getFont("FiraSans-Medium", 72, false);

        // Create the title
        Label title = new Label("No Way But Down", titleStyle);
        table.add(title).pad(10);

        // New Row
        table.row().pad(5).left();

        // Create the first actual button that creates a new game
        TextButton newMain = createButton("New Game", 5,
            buttonStyle, () -> Main.loadMap("main"));
        table.add(newMain);

        // New Row
        table.row().pad(5).left();

        // A load button (no functionality for now)
        TextButton loadMain = new TextButton("     Load Game", buttonStyle);
        table.add(loadMain);

        // New Row
        table.row().pad(5).left();

        // Finally, a quit button, so players can touch grass
        TextButton quit = createButton("Quit", 5,
            buttonStyle, () -> Gdx.app.exit());
        table.add(quit);

        // Create the uiState
        uiStates.put("Title", table);
    }

    /** Sets up the elements for the play state. */
    public static void playState() {
        // Create a new table to hold all the items (and adjust its properties for our needs)
        Table table = new Table();
        table.top().left();
        table.setFillParent(true);
        table.pad(12);
    }

    /** Sets up the elements for the debug state. */
    public static void debugState() {
        // Create a new table to hold all the items and align its contents to the left
        Table table = new Table();
        table.setFillParent(true);
        table.left().padLeft(10); // Padding is added to make the labels not stick to the edge

        // Make the style used for the labels
        Label.LabelStyle style = new Label.LabelStyle();
        style.fontColor = Color.WHITE;
        style.font = getFont("FiraSans-Regular", 25, false);

        // Create a divider label
        Label divider = new Label("", style);

        // Create the labels that will display the current position of the player
        Label x = new Label("X: null", style);
        Label y = new Label("Y: null", style);
        Label col = new Label("Col: null", style);
        Label row = new Label("Row: null", style);
        Label direction = new Label("Direction: null", style);

        // States (in general)
        Label ui = new Label("UI: null", style);
        Label player = new Label("Player: null", style);

        // Player Sprite Stuff
        Label scol = new Label("SCol: null", style);
        Label srow = new Label("SRow: null", style);
        Label ecol = new Label("SCol: null", style);
        Label erow = new Label("SRow: null", style);

        // Add all the labels to the table (The .left() aligns the text to the left)
        // Position
        table.add(col).left().row();
        table.add(row).left().row();
        table.add(x).left().row();
        table.add(y).left().row();
        table.add(direction).left().row();

        table.add(divider).left().row(); // Divider

        // States
        table.add(ui).left().row();
        table.add(player).left().row();

        table.add(divider).left().row(); // Divider

        // Player Sprite Stuff
        table.add(scol).left().row();
        table.add(srow).left().row();
        table.add(ecol).left().row();
        table.add(erow).left().row();

        // Create the uiState
        uiStates.put("Debug", table);

        // Set up an update method to update the labels when the actors are active
        uiUpdates.put("Debug", () -> {
            Main.world.getEntityComponent(Main.playerId, PositionComponent.class).ifPresent(component -> {
                // Position
                x.setText("X: " + component.x);
                y.setText("Y: " + component.y);
                col.setText("Col: " + component.x / Main.tileSize);
                row.setText("Row: " + component.y / Main.tileSize);
            });

            // States
            Main.world.getEntityComponent(Main.playerId, PlayerComponent.class).ifPresent(component -> {
                direction.setText("Direction: " + component.direction);
                player.setText("Player: " + component.state);
            });
            ui.setText("UI: " + uiState);

            // Player Sprite Stuff
            Main.world.getEntityComponent(Main.playerId, SpritesheetComponent.class).ifPresent(component -> {
                scol.setText("SCol: " + component.column);
                srow.setText("SRow: " + component.row);
            });
        });
    }

    /**
     * Creates a {@link TextButton} with effects when you hover over it.
     * <p>
     * The effects are a sound when you hover over the button and also arrows that surround the text on hover.
     * @param text The text that will be on the button. Change it with the {@code setText} function.
     * <p>
     * @param style The style of the button. This might be the background, the font, the color.
     * Use {@link TextButton.TextButtonStyle}.
     * <p>
     * @param action A lambda of the action you want to take. Example of what to put in:
     * {@code () -> Gdx.app.exit()} (Closes the app).
     * <p>
     * @return The resulting button with hover effects.
     **/
    public static TextButton createButton(String text, int leftPad, TextButton.TextButtonStyle style, Runnable action) {
        // Create the button
        TextButton button = new TextButton(" ".repeat(leftPad) + text, style);

        // Add in a click listener for epik hover effects
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Just run the lambda for clicking
                action.run();
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                // If the mouse entered, let the player know they can press it by putting arrows around it and playing a sound
                SoundManager.playSFX("Cursor");
                button.setText(" ".repeat(leftPad-1) + "> " + text);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                // Once the cursor leaves, undo the last method's work
                button.setText(" ".repeat(leftPad) + text);
            }
        });

        return button;
    }
}
