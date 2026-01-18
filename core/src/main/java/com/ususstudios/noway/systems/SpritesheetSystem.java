package com.ususstudios.noway.systems;

import com.ususstudios.noway.main.*;
import com.ususstudios.noway.components.*;

public class SpritesheetSystem implements ECSSystem {
    @Override
    public void process(World world) {
        for (Integer entity : world.query(SpritesheetComponent.class)) {
            SpritesheetComponent component = world.getEntityComponent(entity, SpritesheetComponent.class);
        }
    }
}
