package com.ususstudios.noway.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Game {
    public static final Logger LOGGER = LoggerFactory.getLogger("NoWayButDown");
	
    // Custom Classes
    public static GameState gameState = GameState.PLAYING;
    public static GamePanel gamePanel;
    
	public static boolean running = true;
    public static String FPS = "0.00";
    public static int screenWidth = 800;
    public static int screenHeight = 600;
    
    public static void main(String[] args) {
        LOGGER.info("Program started");
        JFrame jFrame = new JFrame("No Way But Down");
        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                endGame();
                System.exit(0);
            }
        });
        jFrame.setSize(screenWidth, screenHeight);
        jFrame.getContentPane().setBackground(Color.BLACK);
        
        gamePanel = new GamePanel();
        gamePanel.setPreferredSize(new Dimension(screenWidth, screenHeight));
        jFrame.add(gamePanel);
        jFrame.pack();
        
        jFrame.setVisible(true);
	    
	    Thread gameThread = new Thread(gamePanel, "gThread");
        gameThread.start();
        LOGGER.info("Game thread started");
    }
    
    public static void update() {
    }
    
    public static void endGame() {
        running = false;
        LOGGER.info("Game ended");
    }
}