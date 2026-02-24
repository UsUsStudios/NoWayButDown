package com.ususstudios.noway.components;

import java.math.BigDecimal;
import com.ususstudios.noway.Main;

/**
 * A TriggerComponent calls an event of an {@link com.ususstudios.noway.components.EventComponent} when the player enters its collision box.
 */
public class TriggerComponent implements Component {
    /** The identifier of the event that should be called */
    public String event;
    /** The height of the collision box of the trigger (in pixels) */
    public int height;
    /** The width of the collision box of the trigger (in pixels) */
    public int width;
    /** The x offset from the entity's position (left edge) to the left edge of the collision rectangle (in pixels) */
    public float offX;
    /** The y offset from the entity's position (top edge) to the top edge of the collision rectangle (in pixels) */
    public float offY;
    /** Whether the event should be called every frame that the player is inside the trigger or only once when the player enters */
    public boolean repeat;
    /** Is the player currently in the collision box? Used internally for if {@code repeat} is off */
    public boolean isTriggered;

    /**
     * A constructor for initializing a {@code TriggerComponent} programmatically.
     * @param event The identifier of the event that should be called when the player enters the trigger
     * @param width The width of the collision box of the trigger (in tiles)
     * @param height The height of the collision box of the trigger (in tiles)
     * @param offX The x offset from the entity's position (left edge) to the left edge of the collision rectangle (in tiles)
     * @param offY The y offset from the entity's position (top edge) to the top edge of the collision rectangle (in tiles)
     * @param repeat Whether the event should be called every frame that the player is inside the trigger or only once when the player enters
     */
    public TriggerComponent(String event, float width, float height, float offX, float offY, boolean repeat) {
        this.event = event;
        this.width = (int) (width * Main.tileSize);
        this.height = (int) (height * Main.tileSize);
        this.offX = offX * Main.tileSize;
        this.offY = offY * Main.tileSize;
        this.repeat = repeat;
    }

    /**
     * A constructor for initializing a {@code TriggerComponent} in JSON data when the values are decimals.
     * @param event The identifier of the event that should be called when the player enters the trigger
     * @param width The width of the collision box of the trigger (in tiles)
     * @param height The height of the collision box of the trigger (in tiles)
     * @param offX The x offset from the entity's position (left edge) to the left edge of the collision rectangle (in tiles)
     * @param offY The y offset from the entity's position (top edge) to the top edge of the collision rectangle (in tiles)
     * @param repeat Whether the event should be called every frame that the player is inside the trigger or only once when the player enters
     */
    public TriggerComponent(String event, BigDecimal width, BigDecimal height, BigDecimal offX, BigDecimal offY, Boolean repeat) {
        this.event = event;
        this.width = (int) (width.floatValue() * Main.tileSize);
        this.height = (int) (height.floatValue() * Main.tileSize);
        this.offX = offX.floatValue() * Main.tileSize;
        this.offY = offY.floatValue() * Main.tileSize;
        this.repeat = repeat;
    }

    // TODO: automatically change integers into BigDecimal
    /**
     * A constructor for initializing a {@code TriggerComponent} in JSON data when the values are integers.
     * @param event The identifier of the event that should be called when the player enters the trigger
     * @param width The width of the collision box of the trigger (in tiles)
     * @param height The height of the collision box of the trigger (in tiles)
     * @param offX The x offset from the entity's position (left edge) to the left edge of the collision rectangle (in tiles)
     * @param offY The y offset from the entity's position (top edge) to the top edge of the collision rectangle (in tiles)
     * @param repeat Whether the event should be called every frame that the player is inside the trigger or only once when the player enters
     */
    public TriggerComponent(String event, Integer width, Integer height, Integer offX, Integer offY, Boolean repeat) {
        this.event = event;
        this.width = (int) (width.floatValue() * Main.tileSize);
        this.height = (int) (height.floatValue() * Main.tileSize);
        this.offX = offX.floatValue() * Main.tileSize;
        this.offY = offY.floatValue() * Main.tileSize;
        this.repeat = repeat;
    }
}
