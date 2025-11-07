package com.ususstudios.noway;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.Duration;

public class Game {
    static GamePanel gamePanel;
	
	public static boolean running = true;
    public static String FPS = "0.00";
    public static int screenWidth = 800;
    public static int screenHeight = 600;
    
    public static void main(String[] args) {
        JFrame jFrame = new JFrame("No Way But Down");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(screenWidth, screenHeight);
        jFrame.getContentPane().setBackground(Color.BLACK);
        
        gamePanel = new GamePanel();
        gamePanel.setPreferredSize(new Dimension(screenWidth, screenHeight));
        jFrame.add(gamePanel);
        jFrame.pack();
        
        jFrame.setVisible(true);
	    
	    Thread gameThread = new Thread(gamePanel);
        gameThread.start();
    }
    
    public static void update() {
    }
}

class GamePanel extends JPanel implements Runnable {
    @Override
    public void run() {
        final int TARGET_FPS = 60;
        final long OPTIMAL_TIME = 1_000_000_000 / TARGET_FPS;
        
        while (Game.running) {
            long start = System.nanoTime();
            
            Game.update();
            repaint();
            System.out.println("FPS: " + Game.FPS);
            
            long elapsed = System.nanoTime() - start;
            long sleepTime = OPTIMAL_TIME - elapsed;
            
            if (sleepTime > 0) {
                try {
                    Thread.sleep(Duration.ofNanos(sleepTime - 750_000));
                    elapsed = System.nanoTime() - start;
                    DecimalFormat df = new DecimalFormat("0.00");
                    Game.FPS = df.format(1_000_000_000.0 / elapsed).replace(",", ".");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, Game.screenWidth, Game.screenHeight);
        g.setColor(Color.WHITE);
        g.drawString("FPS: " + Game.FPS, 10, 20);
    }
}