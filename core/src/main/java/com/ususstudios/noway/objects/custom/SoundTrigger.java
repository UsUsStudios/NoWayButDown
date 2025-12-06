package com.ususstudios.noway.objects.custom;

import com.ususstudios.noway.main.Sound;
import com.ususstudios.noway.objects.Trigger;

public class SoundTrigger extends Trigger {
    @Override
    public void onTrigger() {
        if ((boolean) properties.get("sfx")) Sound.playSFX((String) properties.get("name"));
        else Sound.playMusic((String) properties.get("name"));
    }
}
