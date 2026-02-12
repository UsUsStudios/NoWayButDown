package com.ususstudios.noway.components;

import java.math.BigDecimal;

import com.ususstudios.noway.Main;

public class PositionComponent implements Component {
    public float x;
    public float y;

    public PositionComponent(Integer x, Integer y) {
        this.x = x * Main.tileSize;
        this.y = y * Main.tileSize;
    }

    public PositionComponent(float x, float y) {
        this.x = x * Main.tileSize;
        this.y = y * Main.tileSize;
    }

    public PositionComponent(BigDecimal x, BigDecimal y) {
        this.x = x.floatValue() * Main.tileSize;
        this.y = y.floatValue() * Main.tileSize;
    }

    public void setPosition(int x, int y) {
        this.x = x * Main.tileSize;
        this.y = y * Main.tileSize;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }


}
