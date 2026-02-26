package com.ususstudios.noway.main;

/** Holds all the states enums for all sorts of different stuff in the game */
public class States {
    /** The states that the game as a whole can be in */
	public enum GameStates {
		/** The splash screen displayed before the main menu */
        SPLASH,
        /** In-game, with tiles and entities rendering and no additional UI */
        PLAYING,
        /** The main menu displayed at startup to load a game */
        MAIN_MENU
	}

    /** The states that a mob can be in */
	public enum MobStates {
		/** Not moving */
        IDLE,
        /** Moving in a direction */
        WALKING
	}
}
