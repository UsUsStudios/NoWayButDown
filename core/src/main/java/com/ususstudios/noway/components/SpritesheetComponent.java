package com.ususstudios.noway.components;

import java.math.BigDecimal;
import com.ususstudios.noway.Main;
import com.ususstudios.noway.rendering.*;

/**
 * A SpritesheetComponent is a sprite drawn at the position of the {@link com.ususstudios.noway.components.PositionComponent} from a spritesheet.
 */
public class SpritesheetComponent implements Component {
    /** The sheet used for animations for the entity. */
    public Image spriteSheet;

    /** This is the colum where the sprite would be pulled from. */
    public int column;
    /** This is the row where the sprite would be pulled from. */
    public int row;

    /** By how much to scale the image in x axis before drawing */
    public float scaleX;
    /** By how much to scale the image in y axis before drawing */
    public float scaleY;

    /**
     * The constructor for initializing a {@code SpritesheetComponent} programmatically when the position and size are integer values.
     * @param spriteSheetName The path of the spritesheet image starting from assets/drawable/
     * @param column The column of the currently shown sprite in the spritesheet
     * @param row The row of the currently shown sprite in the spritesheet
     * @param sizeX How many columns there are total in the spritesheet
     * @param sizeY How many rows there are total in the spritesheet
     * @param scaleX How much to scale the x axis from exactly one tile width before drawing
     * @param scaleY How much to scale the y axis from exactly one tile height before drawing
     */
    public SpritesheetComponent(String spriteSheetName, Integer column, Integer row,
            Integer sizeX, Integer sizeY, float scaleX, float scaleY) {
        spriteSheet = Image.loadImage(spriteSheetName);
        spriteSheet.scaleImage(sizeX * Main.tileSize, sizeY * Main.tileSize);

        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.column = column;
        this.row = row;
    }

    /**
     * The constructor for initializing a {@code SpritesheetComponent} in JSON data when the position and size are decimal values.
     * @param spriteSheetName The path of the spritesheet image starting from assets/drawable/
     * @param column The column of the currently shown sprite in the spritesheet
     * @param row The row of the currently shown sprite in the spritesheet
     * @param sizeX How many columns there are total in the spritesheet
     * @param sizeY How many rows there are total in the spritesheet
     * @param scaleX How much to scale the x axis from exactly one tile width before drawing
     * @param scaleY How much to scale the y axis from exactly one tile height before drawing
     */
    public SpritesheetComponent(String spriteSheetName, Integer column, Integer row,
            Integer sizeX, Integer sizeY, BigDecimal scaleX, BigDecimal scaleY) {
        spriteSheet = Image.loadImage(spriteSheetName);
        spriteSheet.scaleImage(sizeX * Main.tileSize, sizeY * Main.tileSize);

        this.scaleX = scaleX.floatValue();
        this.scaleY = scaleY.floatValue();
        this.column = column;
        this.row = row;
    }

    /**
     * The constructor for initializing a {@code SpritesheetComponent} in JSON data when the values are integers.
     * @param spriteSheetName The path of the spritesheet image starting from assets/drawable/
     * @param column The column of the currently shown sprite in the spritesheet
     * @param row The row of the currently shown sprite in the spritesheet
     * @param sizeX How many columns there are total in the spritesheet
     * @param sizeY How many rows there are total in the spritesheet
     * @param scaleX How much to scale the x axis from exactly one tile width before drawing
     * @param scaleY How much to scale the y axis from exactly one tile height before drawing
     */
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
