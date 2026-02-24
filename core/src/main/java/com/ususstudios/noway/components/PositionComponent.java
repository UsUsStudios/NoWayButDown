package com.ususstudios.noway.components;

import java.math.BigDecimal;
import com.ususstudios.noway.Main;

/**
 * A PositionComponent is used for any entity that has a position in the world, which is almost all of them.
 * It doesn't have a system of its own, instead, it's used in almost every other system.
 */
public class PositionComponent implements Component {
    /** The x position in pixels */
    public float x;
    /** The y position in pixels */
    public float y;

    // TODO: automatically convert integers to decimals.
    /**
     * The constructor for initializing a {@code PositionComponent} in JSON data with integer values.
     * @param x The world x position in tiles
     * @param y The world y position in tiles
     */
    public PositionComponent(Integer x, Integer y) {
        this.x = x * Main.tileSize;
        this.y = y * Main.tileSize;
    }

    /**
     * The constructor for initializing a {@code PositionComponent} programmatically.
     * @param x The world x position in tiles
     * @param y The world y position in tiles
     */
    public PositionComponent(float x, float y) {
        this.x = x * Main.tileSize;
        this.y = y * Main.tileSize;
    }

    /**
     * The constructor for initializing a {@code PositionComponent} in JSON data with decimal values.
     * @param x The world x position in tiles
     * @param y The world y position in tiles
     */
    public PositionComponent(BigDecimal x, BigDecimal y) {
        this.x = x.floatValue() * Main.tileSize;
        this.y = y.floatValue() * Main.tileSize;
    }

    /**
     * Set the position of the {@code PositionComponent}.
     * @param x The target world x position in tiles
     * @param y The target world y position in tiles
     */
    public void setPosition(int x, int y) {
        this.x = x * Main.tileSize;
        this.y = y * Main.tileSize;
    }

    /**
     * Set the position of the {@code PositionComponent}.
     * @param x The target world x position in tiles
     * @param y The target world y position in tiles
     */
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }


}
