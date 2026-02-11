package com.ususstudios.noway.components;

import java.math.BigDecimal;

import com.ususstudios.noway.Main;

public class CollisionComponent implements Component {
    public int height;
    public int width;
    public float offX;
    public float offY;

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
