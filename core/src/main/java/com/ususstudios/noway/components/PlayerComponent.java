package com.ususstudios.noway.components;

import java.math.BigDecimal;
import com.ususstudios.noway.main.States;

/**
 * PlayerComponent is a component that handles single-player controls and is meant to belong to only one entity (the player).
 */
public class PlayerComponent implements Component {
    /** The current player state */
    public States.MobStates state = States.MobStates.IDLE;
    /** The direction that the player is moving in */
    public String direction = "down";
    /** The speed at which the player moves in ideal horizontal or verticle movement */
    public float speed;

    /**
     * The constructor used for initializing the {@code PlayerComponent} programmatically.
     * @param speed The movement speed of the player moving horizontally or vertically
     */
    public PlayerComponent(float speed) {
        this.speed = speed;
    }

    /**
     * The constructor used for initializing the {@code PlayerComponent} in JSON data.
     * No idea why you'd need this but it exists!
     * @param speed The movement speed of the player moving horizontally or vertically
     */
    public PlayerComponent(BigDecimal speed) {
        this.speed = speed.floatValue();
    }
}
