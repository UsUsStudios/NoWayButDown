package com.ususstudios.noway.components;

import com.ususstudios.noway.Main;

public class CollisionComponent implements Component {
    public int height;
    public int width;
    public float offX;
    public float offY;

    public CollisionComponent(float height, float width, float offX, float offY) {
        this.height = (int) height * Main.tileSize;
        this.width = (int) width * Main.tileSize;
        this.offX = offX;
        this.offY = offY;
    }
}
