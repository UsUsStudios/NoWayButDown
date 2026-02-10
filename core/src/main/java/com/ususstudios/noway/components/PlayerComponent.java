package com.ususstudios.noway.components;

import java.math.BigDecimal;

import com.ususstudios.noway.main.States;

public class PlayerComponent implements Component {
    public States.MobStates state = States.MobStates.IDLE;
	public String direction = "down";
    public float speed;

    public PlayerComponent(BigDecimal speed) {
        this.speed = speed.floatValue();
    }
}
