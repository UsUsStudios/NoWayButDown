package com.ususstudios.noway.components;

import java.math.BigDecimal;
import com.ususstudios.noway.Main;

public class TriggerComponent implements Component {
    public String event;
    public int height;
    public int width;
    public float offX;
    public float offY;
    public boolean repeat;
    public boolean isTriggered;

    public TriggerComponent(String event, float width, float height, float offX, float offY, boolean repeat) {
        this.event = event;
        this.width = (int) (width * Main.tileSize);
        this.height = (int) (height * Main.tileSize);
        this.offX = offX * Main.tileSize;
        this.offY = offY * Main.tileSize;
        this.repeat = repeat;
    }

    public TriggerComponent(String event, BigDecimal width, BigDecimal height, BigDecimal offX, BigDecimal offY, Boolean repeat) {
        this.event = event;
        this.width = (int) (width.floatValue() * Main.tileSize);
        this.height = (int) (height.floatValue() * Main.tileSize);
        this.offX = offX.floatValue() * Main.tileSize;
        this.offY = offY.floatValue() * Main.tileSize;
        this.repeat = repeat;
    }

    public TriggerComponent(String event, Integer width, Integer height, Integer offX, Integer offY, Boolean repeat) {
        this.event = event;
        this.width = (int) (width.floatValue() * Main.tileSize);
        this.height = (int) (height.floatValue() * Main.tileSize);
        this.offX = offX.floatValue() * Main.tileSize;
        this.offY = offY.floatValue() * Main.tileSize;
        this.repeat = repeat;
    }
}
