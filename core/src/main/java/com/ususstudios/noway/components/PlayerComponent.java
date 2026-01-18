package com.ususstudios.noway.components;

import com.ususstudios.noway.main.States;

public class PlayerComponent implements Component {
    public float x;
    public float y;
    public States.MobStates state = States.MobStates.IDLE;
	public String direction = "down";
    public float speed;

    public PlayerComponent(float x, float y, float speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
    }
}
