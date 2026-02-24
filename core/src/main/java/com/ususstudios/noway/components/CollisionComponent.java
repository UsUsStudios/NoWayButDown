package com.ususstudios.noway.components;

import java.math.BigDecimal;
import com.ususstudios.noway.Main;

/**
 * A CollisionComponent is used by systems that move an entity to check whether the entity is inside another CollisionComponent or tile.
 **/
public class CollisionComponent implements Component {
    /** The height of the collision rectangle */
    public int height;
    /** The width of the collision rectangle */
    public int width;
    /** The x offset from the entity's position (left edge) to the left edge of the collision rectangle */
    public float offX;
    /** The y offset from the entity's position (top edge) to the top edge of the collision rectangle */
    public float offY;

    /** The constructor for initializing a {@code CollisionComponent} programmatically.
     * @param width The width of the collision rectangle
     * @param height The height of the collision rectangle
     * @param offX The x offset from the entity's position (left edge) to the left edge of the collision rectangle
     * @param offY The y offset from the entity's position (top edge) to the top edge of the collision rectangle
    */
    public CollisionComponent(float width, float height, float offX, float offY) {
        this.width = (int) (width * Main.tileSize);
        this.height = (int) (height * Main.tileSize);
        this.offX = offX * Main.tileSize;
        this.offY = offY * Main.tileSize;
    }

    /** The constructor for initializing a {@code CollisionComponent} with decimal values in JSON data.
     * @param width The width of the collision rectangle
     * @param height The height of the collision rectangle
     * @param offX The x offset from the entity's position (left edge) to the left edge of the collision rectangle
     * @param offY The y offset from the entity's position (top edge) to the top edge of the collision rectangle
    */
    public CollisionComponent(BigDecimal width, BigDecimal height, BigDecimal offX, BigDecimal offY) {
        this.width = (int) (width.floatValue() * Main.tileSize);
        this.height = (int) (height.floatValue() * Main.tileSize);
        this.offX = offX.floatValue() * Main.tileSize;
        this.offY = offY.floatValue() * Main.tileSize;
    }

    // TODO: make this method obsolete by making the map parser automatically convert integers to BigDecimal.

    /** The constructor for initializing a {@code CollisionComponent} with integer values in JSON data.
     * @param width The width of the collision rectangle
     * @param height The height of the collision rectangle
     * @param offX The x offset from the entity's position (left edge) to the left edge of the collision rectangle
     * @param offY The y offset from the entity's position (top edge) to the top edge of the collision rectangle
    */
    public CollisionComponent(Integer width, Integer height, Integer offX, Integer offY) {
        this.width = (int) (width.floatValue() * Main.tileSize);
        this.height = (int) (height.floatValue() * Main.tileSize);
        this.offX = offX.floatValue() * Main.tileSize;
        this.offY = offY.floatValue() * Main.tileSize;
    }
}
