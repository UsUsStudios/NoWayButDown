package com.ususstudios.noway.components;

import com.ususstudios.noway.Main;

public class PositionComponent implements Component {
    public float x;
    public float y;

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public PositionComponent(float x, float y) {
        this.x = x * Main.tileSize;
        this.y = y * Main.tileSize;
    }

    public void setPosition(int x, int y) {
        this.x = x * Main.tileSize;
        this.y = y * Main.tileSize;
    }
}
