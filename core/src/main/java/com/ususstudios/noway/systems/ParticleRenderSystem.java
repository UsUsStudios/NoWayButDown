package com.ususstudios.noway.systems;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.ususstudios.noway.Main;
import com.ususstudios.noway.main.World;

/** This system ticks and renders every ParticleInstance. */
public class ParticleRenderSystem implements ECSSystem {
    /**
     * Processes all the entities that this system applies to, and applies any changes to them.
     * @param world The {@link com.ususstudios.noway.main.World} that this system should apply changes to.
    */
    @Override
    public void process(World world) {
        Main.shapes.begin(ShapeType.Filled);
        Main.particles.draw();
        Main.shapes.end();
    }
}

