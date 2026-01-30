package com.ususstudios.noway.components;

import com.ususstudios.noway.Main;
import com.ususstudios.noway.rendering.*;

public class SpritesheetComponent implements Component {
    /// The sheet used for animations for the entity.
    public Image spriteSheet;

    /// This is the colum where the sprite would be pulled from. Set to -1 to disable it.
    public int column;
    /// This is the row where the sprite would be pulled from. Set to -1 to disable it.
    public int row;

    /// By how much to scale the image before drawing
    public float scaleX;
    public float scaleY;

    public SpritesheetComponent(String spriteSheetName, int column, int row, int sizeX, int sizeY, float scaleX, float scaleY) {
        spriteSheet = Image.loadImage(spriteSheetName);
        spriteSheet.scaleImage(sizeX * Main.tileSize, sizeY * Main.tileSize);

        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.column = column;
        this.row = row;
    }
}
