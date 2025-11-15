package com.ususstudios.noway.main;

import com.ususstudios.noway.QueueAppender;
import com.ususstudios.noway.entity.Entity;
import com.ususstudios.noway.entity.Player;
import com.ususstudios.noway.rendering.Darkness;
import com.ususstudios.noway.rendering.GameRendering;
import com.ususstudios.noway.rendering.MapTileHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

// Main game class
public class Game {
    public static final Logger LOGGER = LoggerFactory.getLogger("NoWayButDown");
	
    // Game State
    public static States.GameStates gameState = States.GameStates.NULL;
    public static String currentMap = "";
    
    // Custom Classes
    public static GamePanel gamePanel;
    public static InputHandler inputHandler;
    public static Darkness darkness;
    
    // Entities
    public static Player player;
    public static ArrayList<Entity> entities = new ArrayList<>();
    
	public static boolean running = true;
    public static String FPS = "0.00";
    public static int screenWidth = 1100;
    public static int screenHeight = 700;
    public static int tileSize = 48;
    public static String language = "english";
    public static String identifier = "nowaybutdown";
    
    public static void main(String[] args) {
        LOGGER.info("Program started");
        // Load everything we need
        MapTileHandler.loadTiles();
        MapTileHandler.loadMaps();
        GameRendering.initialize();
        Sound.loadLibrary();
        Translations.loadFiles();
        
        // Set up the window
        JFrame jFrame = new JFrame(Translations.get(identifier, "title"));
        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                endGame();
                System.exit(0);
            }
        });
        jFrame.setSize(screenWidth, screenHeight);
        jFrame.getContentPane().setBackground(Color.BLACK);
        
        // Set up the game panel
        gamePanel = new GamePanel();
        gamePanel.setPreferredSize(new Dimension(screenWidth, screenHeight));
        jFrame.add(gamePanel);
        jFrame.pack();
        
        jFrame.setVisible(true);
        
        Sound.playMusic("Can't Go Up");
        darkness = new Darkness();
        inputHandler = new InputHandler();
        jFrame.addKeyListener(inputHandler);
        
        // Load the player and game
        player = new Player();
        entities.add(player);
        darkness.addLightSource(player);
        
        // Start game thread!
	    Thread gameThread = new Thread(gamePanel, "gThread");
        gameThread.start();
        gameState = States.GameStates.MAIN_MENU;
        LOGGER.info("Game thread started");
    }
    
    public static void update() {
        if (gameState == States.GameStates.PLAYING) Game.entities.forEach(Entity::update);
        else GameRendering.updateUI();
    }
    
    public static void endGame() {
        running = false;
        LOGGER.info("Game ended");
    }
    
    public static void loadMap(String map) {
        currentMap = map;
        player.setPosition(MapTileHandler.maps.get(map).spawnX(), MapTileHandler.maps.get(map).spawnY());
        gameState = States.GameStates.PLAYING;
        LOGGER.info("Map {} loaded", map);
    }
    
    public static void handleException(Exception e) {
        QueueAppender.printError(e);
        running = false;
    }
}