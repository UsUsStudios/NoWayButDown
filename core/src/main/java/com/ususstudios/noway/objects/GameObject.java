package com.ususstudios.noway.objects;

import com.ususstudios.noway.Main;

/** Extend this class to create a GameObject.
 * A GameObject is anything that has a position and size, and has an update function.
 */
public class GameObject {
    // Positions
    public float x;
    public float y;
    public float width = Main.tileSize;
    public float height = Main.tileSize;

    // Updating
    public boolean collision = true;
    /// Can the objects update while not being on the screen? If it's true, the {@code} onScreen} field will always be set to true inside the main update loop of the objects.
    public boolean updateOffScreen = false;
    /// Pretty self-explanatory. It's used to increase performance by not loading the objects while it's off-screen.
    public boolean onScreen = false;


    /// Called in the main draw method to draw the objects. Not all objects need to have this.
    public void draw() {}

    /** Called in the main update loop to update the objects.
     * The objects must be in a certain Hashmap in Main (depending on their type) to be updated.
     * For instance, a normal objects would have to be in the {@code} objects} array list to update.
     * Remove the objects from the array list if you stopped using it to stop updating it.
     * <p>
     * For performance, everything is behind an if statement with {@code} onScreen} to only update when the objects is on screen.
     * Use {@code} updateOffScreen} if you want to disable this.
     **/
    public void update() {
        // Check if the objects is on the screen using the player's camera position
        onScreen = x + width > Main.player.cameraX + Main.screenWidth / 2f &&
            x - width < Main.player.cameraX + Main.screenWidth / 2f &&
            y + height > Main.player.cameraY + Main.screenHeight / 2f &&
            y - height < Main.player.cameraY + Main.screenHeight / 2f || updateOffScreen;
    }
}
