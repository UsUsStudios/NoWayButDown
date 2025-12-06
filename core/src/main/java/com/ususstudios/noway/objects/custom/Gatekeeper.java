package com.ususstudios.noway.objects.custom;

import com.ususstudios.noway.Main;
import com.ususstudios.noway.main.States;
import com.ususstudios.noway.objects.Mob;
import com.ususstudios.noway.rendering.Image;

public class Gatekeeper extends Mob {
    public Gatekeeper() {
        super("Gatekeeper", 0f, 0f, 36f, 48f);

        // Ajust spriteSheet properties
        spriteSheet = Image.loadImage("entity/npc/gatekeeper_sheet");
        spriteRow = 0;
        spriteColumn = 0;
        spriteSheet.scaleImage(Main.tileSize * 3, Main.tileSize * 4);
        currentImage = spriteSheet;

        // Set some properties
        speed = 80;
        updateOffScreen = true;
        properties.put("light_radius", 100f);
        properties.put("light_intensity", 0.5f);

        /* Set onScreen to true, so the player can be drawn
        Since the super class's update method isn't called, and the player is always on Screen, it doesn't update to false*/
        onScreen = true;
    }

    @Override
    public void update() {
        if (Main.random.nextInt(15) == 0) {
            int rand = Main.random.nextInt(6);
            direction = switch (rand) {
                case 0 -> "up";
                case 1 -> "down";
                case 2 -> "left";
                case 3 -> "right";
                default -> "";
            };
        }

        // If nothing was added to the StringBuilder, meaning the player isn't walking, change his state accordingly
        if (direction.isEmpty()) state = States.MobStates.IDLE;
        else state = States.MobStates.WALKING;

        // Set the direction to the final newDirection string and let the mod's update method do the rest
        direction = direction.trim();
        super.update();
    }
}
