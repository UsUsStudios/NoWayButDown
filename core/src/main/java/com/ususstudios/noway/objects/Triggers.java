package com.ususstudios.noway.objects;

import com.ususstudios.noway.Main;

public abstract class Triggers extends GameObject {
    public abstract void onTrigger();

    @Override
    public void update() {
        super.update();
        if (onScreen && CollisionChecker.check2EntityCollision(Main.player, this)) {
            onTrigger();
        }
    }
}
