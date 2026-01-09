package com.ususstudios.noway.entities.custom;

import com.ususstudios.noway.main.Sound;
import com.ususstudios.noway.entities.Trigger;

public class SoundTrigger extends Trigger {
    public SoundTrigger(String name, float x, float y) {
        super(name, x, y);
    }

    @Override
    public void onTrigger() {
        if ((boolean) properties.get("sfx")) Sound.playSFX((String) properties.get("name"));
        else Sound.playMusic((String) properties.get("name"));
    }
}
