package com.ususstudios.noway.main;

/** Holds all the states enums for all sorts of different stuff in the game */
public class States {
    /** The states that the game as a whole can be in */
	public enum GameStates {
		SPLASH, PLAYING, MAIN_MENU
	}

    /** The states that a mob can be in */
	public enum MobStates {
		IDLE, WALKING
	}
}
