package com.ususstudios.noway.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.ususstudios.noway.Main;
import com.ususstudios.noway.rendering.MapTileHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class SoundManager {
    private static final HashMap<String, Music> musicLibrary = new HashMap<>();
    private static final HashMap<String, Sound> soundLibrary = new HashMap<>();
    private static Music currentMusic;
    public static float musicVolume = 0.1f;
    public static float soundVolume = 0.5f;

    public static void loadLibrary() {
        // Load music
        musicLibrary.put("Can't Go Up", Gdx.audio.newMusic(Gdx.files.internal("sound/music/can't go up.wav")));
        musicLibrary.put("Neverending Maze", Gdx.audio.newMusic(Gdx.files.internal("sound/music/neverending maze.wav")));

        // Load SFX
        soundLibrary.put("Cursor", Gdx.audio.newSound(Gdx.files.internal("sound/sfx/cursor.wav")));
    }

    public static void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    public static void playMusic(String songName, boolean looping, boolean blocking) {
        if (currentMusic != null) {
            currentMusic.stop();
        }
        currentMusic = musicLibrary.get(songName);
        if (currentMusic != null) {
            currentMusic.setLooping(looping);
            currentMusic.setVolume(musicVolume);
            currentMusic.play();
        }

        if (blocking) {
            while (currentMusic.isPlaying()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Main.handleException(e);
                }
            }
        }
	}

	/// Play randomly out of a set of songs for a given map
	public static void playMapMusic(String mapName) {
		new Thread(() -> {
			try {
				stopMusic();
				List<Object> songNames = MapTileHandler.maps.get(mapName).songs();
				if (!songNames.isEmpty()) {
					while (Objects.equals(Main.currentMap, mapName)) {
						// Pick a random song from the list
						playMusic((String) songNames.get(Main.random.nextInt(songNames.size())), false, true);

						Thread.sleep(Main.random.nextInt(20_000) + 20_000);
					}
					stopMusic();
				}
			} catch (InterruptedException e) {
				Main.handleException(e);
			}
		}).start();
	}

    public static void playSFX(String sfxName) {
        Sound sound = soundLibrary.get(sfxName);
        if (sound != null) {
            sound.play(soundVolume);
        }
    }

    public static void dispose() {
        for (Music music : musicLibrary.values()) {
            music.dispose();
        }
        for (Sound sound : soundLibrary.values()) {
            sound.dispose();
        }

    }
}
