package com.ususstudios.noway.components;

import com.ususstudios.noway.main.States;

public class PlayerComponent implements Component {
    public States.MobStates state = States.MobStates.IDLE;
	public String direction = "down";
    public float speed;

    public PlayerComponent(float speed) {
        this.speed = speed;
    }
}
