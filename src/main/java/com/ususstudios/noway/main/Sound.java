package com.ususstudios.noway.main;

import com.ususstudios.noway.rendering.MapTileHandler;
import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Sound {
    public static final HashMap<String, URL> SOUND_LIBRARY = new HashMap<>();
    ArrayList<Clip> clips = new ArrayList<>();
    FloatControl floatControl;
    int volumeScale = 3;
    float volume;

    public static void loadLibrary() {
        // Music
	    SOUND_LIBRARY.put("Can't Go Up", Sound.class.getResource("/sound/music/can't go up.wav"));
        Game.LOGGER.info("Loaded all music files");

        // SFX
	    SOUND_LIBRARY.put("Cursor", Sound.class.getResource("/sound/sfx/cursor.wav"));
        Game.LOGGER.info("Loaded all SFX files");
    }

	public int getFile(String soundName) {
		try {
			if (SOUND_LIBRARY.get(soundName) != null) {
				AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(SOUND_LIBRARY.get(soundName));
				Clip clip = AudioSystem.getClip();
				clip.open(audioInputStream);
				floatControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
				checkVolume();
				clips.add(clip);
				return clips.size() - 1;
			} else {
				Game.LOGGER.warn("Warning: \"{}\" is not a valid sfx.", soundName);
				return -1;
			}
		} catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
			Game.handleException(e);
		}
		return -1;
	}
	public void play(int idx) {
		if (idx != -1) {
			Clip clip = clips.get(idx);
			if (clip != null) clip.start();
			else Game.LOGGER.warn("Warning: No clip found to play");
		}
	}
	public void loop(int idx) {
		if (idx != -1) {
			Clip clip = clips.get(idx);
			if (clip != null) clip.loop(Clip.LOOP_CONTINUOUSLY);
			else Game.LOGGER.warn("Warning: No clip found to loop");
		}
	}
	public void stop(int idx) {
		if (idx != -1) {
			Clip clip = clips.get(idx);
			if (clip != null) clip.stop();
		}
	}
	public void checkVolume() {
		switch (volumeScale) {
			case 0 -> volume = -80f;
			case 1 -> volume = -20f;
			case 2 -> volume = -12f;
			case 3 -> volume = -5f;
			case 4 -> volume = 1f;
			case 5 -> volume = 6f;
		}
		floatControl.setValue(volume);
	}
	
	// Static Stuff
	public static Sound music = new Sound();
	public static Sound sfx = new Sound();
	
	public static void playMusic(String songName) {
		stopMusic();
		int idx = music.getFile(songName);
		music.play(idx);
		music.loop(idx);
	}
	
	/// Play randomly out of a set of songs for a given map
	public static void playMapMusic(String mapName) {
		new Thread(() -> {
			try {
				stopMusic();
				List<Object> songNames = MapTileHandler.maps.get(mapName).songs();
				if (!songNames.isEmpty()) {
					while (Objects.equals(Game.currentMap, mapName)) {
						// Pick a random song from the list
						int idx = music.getFile((String) songNames.get(Game.random.nextInt(songNames.size())));
						music.playBlocking(idx);
						
						Thread.sleep(Game.random.nextInt(10000) + 5000);
					}
					stopMusic();
				}
			} catch (InterruptedException e) {
				Game.handleException(e);
			}
		}).start();
	}
	
	/// Play a sound clip and block until it finishes
	public void playBlocking(int idx) throws InterruptedException {
		if (idx == -1) return;
		
		Clip clip = clips.get(idx);
		if (clip == null) return;
		
		final Object lock = new Object();
		
		clip.addLineListener(event -> {
			if (event.getType() == LineEvent.Type.STOP) {
				synchronized (lock) {
					lock.notify();
				}
			}
		});
		
		synchronized (lock) {
			clip.start();
			lock.wait();  // blocks until STOP event fires
		}
		
		clip.close();  // optional cleanup
	}
	
	public static void stopMusic() {
		if (!music.clips.isEmpty()) music.stop(0);
	}
	public static void playSFX(String sfxName) {
		sfx.play(sfx.getFile(sfxName));
	}
}
