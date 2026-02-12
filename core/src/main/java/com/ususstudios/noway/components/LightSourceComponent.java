package com.ususstudios.noway.components;

import java.math.BigDecimal;

public class LightSourceComponent implements Component {
    public float lightIntensity;
    public float lightRadius;
    public float lightFlickering;
    public float offX;
    public float offY;

    public LightSourceComponent(float lightIntensity, float lightRadius, float lightFlickering, float offX, float offY) {
        this.lightIntensity = lightIntensity;
        this.lightRadius = lightRadius;
        this.lightFlickering = lightFlickering;
        this.offX = offX;
        this.offY = offY;
    }

    public LightSourceComponent(BigDecimal lightIntensity, BigDecimal lightRadius,
            BigDecimal lightFlickering, BigDecimal offX, BigDecimal offY) {
        this.lightIntensity = lightIntensity.floatValue();
        this.lightRadius = lightRadius.floatValue();
        this.lightFlickering = lightFlickering.floatValue();
        this.offX = offX.floatValue();
        this.offY = offY.floatValue();
    }
}
