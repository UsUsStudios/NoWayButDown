package com.ususstudios.noway.objects;

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
}
