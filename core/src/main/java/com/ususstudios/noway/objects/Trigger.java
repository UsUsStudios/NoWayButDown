package com.ususstudios.noway.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ususstudios.noway.Main;

public abstract class Trigger extends GameObject {
    public boolean triggered = false;

    public Trigger() {
        collision = false;
        updateOffScreen = true;
    }

    public abstract void onTrigger();

    @Override
    public void update() {
        super.update();
        if (onScreen && CollisionChecker.check2EntityCollision(Main.player, this)) {
            if (!triggered) onTrigger();
            triggered = true;
        } else {
            triggered = false;
        }
    }

    @Override
    public void draw() {
        if (Main.debugMode) {
            // Draw hitbox
            Main.batch.end();

            Main.shapes.begin(ShapeRenderer.ShapeType.Line);
            Main.shapes.setColor(Color.RED);
            float screenX = x + colX - Main.player.cameraX + Main.screenWidth / 2f;
            float screenY = y + colY - Main.player.cameraY + Main.screenHeight / 2f;
            Main.shapes.rect(screenX, Main.screenHeight - screenY - height, width, height);
            Main.shapes.end();

            Main.batch.begin();
        }
    }
}
