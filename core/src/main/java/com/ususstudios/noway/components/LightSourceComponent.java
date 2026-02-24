package com.ususstudios.noway.components;

import java.math.BigDecimal;

/**
 * A LightSourceComponent emits light in the darkness drawn by {@link com.ususstudios.noway.systems.DarknessSystem}.
 */
public class LightSourceComponent implements Component {
    /** How intensely the light should be, 0.0-1.0 */
    public float lightIntensity;
    /** The radius of the light (in pixels) */
    public float lightRadius;
    /** How intensely the light will be flickering */
    public float lightFlickering;
    /** The X offset from the right edge of the entity to the center of the light (in tiles) */
    public float offX;
    /** The Y offset from the sort of bottom of the entity (I'm not sure) to the center of the light (in tiles) */
    public float offY;

    /**
     * A constructor for initializing a {@code LightSourceComponent} programmatically.
     * @param lightIntensity How intensely the light should be, 0.0-1.0
     * @param lightRadius The radius of the light (in pixels)
     * @param lightFlickering How intensely will the light be flickering
     * @param offX The X offset from the right edge of the entity to the center of the light (in tiles)
     * @param offY The Y offset from the sort of bottom of the entity (just experiment) to the center of the light (in tiles)
     */
    public LightSourceComponent(float lightIntensity, float lightRadius, float lightFlickering, float offX, float offY) {
        this.lightIntensity = lightIntensity;
        this.lightRadius = lightRadius;
        this.lightFlickering = lightFlickering;
        this.offX = offX;
        this.offY = offY;
    }

    /**
     * A constructor for initializing a {@code LightSourceComponent} in JSON data.
     * @param lightIntensity How intensely the light should be, 0.0-1.0
     * @param lightRadius The radius of the light (in pixels)
     * @param lightFlickering How intensely will the light be flickering
     * @param offX The X offset from the right edge of the entity to the center of the light
     * @param offY The Y offset from the sort of bottom of the entity (just experiment) to the center of the light
     */
    public LightSourceComponent(BigDecimal lightIntensity, BigDecimal lightRadius,
            BigDecimal lightFlickering, BigDecimal offX, BigDecimal offY) {
        this.lightIntensity = lightIntensity.floatValue();
        this.lightRadius = lightRadius.floatValue();
        this.lightFlickering = lightFlickering.floatValue();
        this.offX = offX.floatValue();
        this.offY = offY.floatValue();
    }
}
