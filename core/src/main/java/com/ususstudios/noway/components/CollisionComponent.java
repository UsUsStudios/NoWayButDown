package com.ususstudios.noway.components;

import java.math.BigDecimal;

import com.ususstudios.noway.Main;

public class CollisionComponent implements Component {
    public int height;
    public int width;
    public float offX;
    public float offY;

    public CollisionComponent(float width, float height, float offX, float offY) {
        this.width = (int) (width * Main.tileSize);
        this.height = (int) (height * Main.tileSize);
        this.offX = offX * Main.tileSize;
        this.offY = offY * Main.tileSize;
    }

    public CollisionComponent(BigDecimal width, BigDecimal height, BigDecimal offX, BigDecimal offY) {
        this.width = (int) (width.floatValue() * Main.tileSize);
        this.height = (int) (height.floatValue() * Main.tileSize);
        this.offX = offX.floatValue() * Main.tileSize;
        this.offY = offY.floatValue() * Main.tileSize;
    }

    public CollisionComponent(Integer width, Integer height, Integer offX, Integer offY) {
        this.width = (int) (width.floatValue() * Main.tileSize);
        this.height = (int) (height.floatValue() * Main.tileSize);
        this.offX = offX.floatValue() * Main.tileSize;
        this.offY = offY.floatValue() * Main.tileSize;
    }
}
