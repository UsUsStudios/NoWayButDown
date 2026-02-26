package com.ususstudios.noway.systems;

import com.ususstudios.noway.main.*;

/** This interface is implemented by every ECS system in this codebase. It's used for things that can accept any system. */
public interface ECSSystem {
    /**
     * Processes all the entities that this system applies to, and applies any changes to them.
     * @param world The {@link com.ususstudios.noway.main.World} that this system should apply changes to.
    */
    void process(World world);
}
