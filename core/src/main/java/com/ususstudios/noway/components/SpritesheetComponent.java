package com.ususstudios.noway.components;

import com.ususstudios.noway.main.*;
import com.ususstudios.noway.rendering.*;

public class SpritesheetComponent implements Component {
    /// The image that is drawn at the objects's location to represent the objects.
	public Image currentImage;
	/// If there's a sprite sheet, this is the colum where the sprite would be pulled from. Set to -1 to disable it.
	public int spriteColumn;
	/// If there's a sprite sheet, this is the row where the sprite would be pulled from. Set to -1 to disable it.
	public int spriteRow;
    /// How much to scale the image before it's drawn?
    public int scaleX;
    public int scaleY;

    public SpritesheetComponent(Image currentImage, int spriteColumn, int spriteRow, int scaleX, int scaleY) {
        this.currentImage = currentImage;
        this.spriteColumn = spriteColumn;
        this.spriteRow = spriteRow;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }
}
