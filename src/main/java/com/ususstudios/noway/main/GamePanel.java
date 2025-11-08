package com.ususstudios.noway.main;

import com.ususstudios.noway.rendering.GameRendering;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.Duration;

public class GamePanel extends JPanel implements Runnable {
	@Override
	public void run() {
		final int TARGET_FPS = 60;
		final long OPTIMAL_TIME = 1_000_000_000 / TARGET_FPS;
		
		while (Game.running) {
			long start = System.nanoTime();
			
			Game.update();
			repaint();
			
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
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		switch (Game.gameState) {
			case PLAYING -> GameRendering.draw(g);
		}
	}
}
