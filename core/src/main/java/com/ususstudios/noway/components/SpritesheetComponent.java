package com.ususstudios.noway.components;

import java.math.BigDecimal;
import com.ususstudios.noway.Main;
import com.ususstudios.noway.rendering.*;

public class SpritesheetComponent implements Component {
    /// The sheet used for animations for the entity.
    public Image spriteSheet;

    /// This is the colum where the sprite would be pulled from.
    public int column;
    /// This is the row where the sprite would be pulled from.
    public int row;

    /// By how much to scale the image before drawing
    public float scaleX;
    public float scaleY;

    public SpritesheetComponent(String spriteSheetName, Integer column, Integer row,
            Integer sizeX, Integer sizeY, float scaleX, float scaleY) {
        spriteSheet = Image.loadImage(spriteSheetName);
        spriteSheet.scaleImage(sizeX * Main.tileSize, sizeY * Main.tileSize);

        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.column = column;
        this.row = row;
    }

    public SpritesheetComponent(String spriteSheetName, Integer column, Integer row,
            Integer sizeX, Integer sizeY, BigDecimal scaleX, BigDecimal scaleY) {
        spriteSheet = Image.loadImage(spriteSheetName);
        spriteSheet.scaleImage(sizeX * Main.tileSize, sizeY * Main.tileSize);

        this.scaleX = scaleX.floatValue();
        this.scaleY = scaleY.floatValue();
        this.column = column;
        this.row = row;
    }

    public SpritesheetComponent(String spriteSheetName, Integer column, Integer row,
            Integer sizeX, Integer sizeY, Integer scaleX, Integer scaleY) {
        spriteSheet = Image.loadImage(spriteSheetName);
        spriteSheet.scaleImage(sizeX * Main.tileSize, sizeY * Main.tileSize);

        this.scaleX = scaleX.floatValue();
        this.scaleY = scaleY.floatValue();
        this.column = column;
        this.row = row;
    }
}
