package com.ususstudios.noway.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.ususstudios.noway.Main;
import com.ususstudios.noway.rendering.MapTileHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/** A static class that stores all the sounds and music for easy playing. */
public class SoundManager {
    /** A map of all the music files and their names (used to play them) */
    private static final HashMap<String, Music> musicLibrary = new HashMap<>();
    /** A map of all the SFX files and their names (used to play them) */
    private static final HashMap<String, Sound> soundLibrary = new HashMap<>();
    /** The music currently being played */
    private static Music currentMusic;
    /** The volume that music should be played at */
    public static float musicVolume = 0.1f;
    /** The volume that sounds should be played at */
    public static float soundVolume = 0.5f;

    /**
     * Puts all the sound files in their respective libraries
     * Called by {@link com.ususstudios.noway.Main} at creation.
     */
    public static void loadLibrary() {
        // Load music
        musicLibrary.put("Can't Go Up", Gdx.audio.newMusic(Gdx.files.internal("sound/music/can't go up.wav")));
        musicLibrary.put("Neverending Maze", Gdx.audio.newMusic(Gdx.files.internal("sound/music/neverending maze.wav")));

        // Load SFX
        soundLibrary.put("Cursor", Gdx.audio.newSound(Gdx.files.internal("sound/sfx/cursor.wav")));
    }

    /** Stops the currently being played music, if it exists */
    public static void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    /**
     * Stops the currently played music and starts a new song
     * @param songName The name of the song from the {@code musicLibrary}
     * @param looping Whether the song should loop indefinitely until it is stopped
     * @param blocking Whether the method should be blocking - stop the thread until the song is over
     */
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

        // Wait until the music stops
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

    /**
     * Plays randomly out of a set of songs for a given map, repeated until the map changes
     * Reads the {@link com.ususstudios.noway.rendering.Map} {@code songs} field and picks
     * @param mapName The name of the current map whose music should be played
     */
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

    /**
     * Plays a sound effect from the sound effect library
     * @param sfxName The name registered to the sound effect in the sound library
     */
    public static void playSFX(String sfxName) {
        Sound sound = soundLibrary.get(sfxName);
        if (sound != null) {
            sound.play(soundVolume);
        }
    }

    /**
     * Disposes all the music and sounds from the libraries
     * Used when {@link com.ususstudios.noway.Main} is disposed to avoid memory leaking.
     */
    public static void dispose() {
        for (Music music : musicLibrary.values()) {
            music.dispose();
        }
        for (Sound sound : soundLibrary.values()) {
            sound.dispose();
        }
    }
}
