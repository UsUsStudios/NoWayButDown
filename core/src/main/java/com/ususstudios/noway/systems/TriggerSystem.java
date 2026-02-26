package com.ususstudios.noway.systems;

import com.ususstudios.noway.components.*;
import com.ususstudios.noway.main.World;
import com.ususstudios.noway.Main;

/**
 * The TriggerSystem processes {@link com.ususstudios.noway.components.TriggerComponent}.
 */
public class TriggerSystem implements ECSSystem {
    @Override
    public void process(World world) {
        for (Integer entity : world.query(PositionComponent.class, TriggerComponent.class, EventComponent.class)) {
            TriggerComponent tc = world.getEntityComponent(entity, TriggerComponent.class).get();
            EventComponent ec = world.getEntityComponent(entity, EventComponent.class).get();

            if (checkTriggerCollision(entity)) {
                if (tc.repeat) {
                    ec.call(tc.event);
                } else {
                    if (!tc.isTriggered) {
                        tc.isTriggered = true;
                        ec.call(tc.event);
                    }
                }
            } else {
                tc.isTriggered = false;
            }
        }
    }

    /**
     * Stolen from the CollisionChecker class
     * @param triggerEntity The entity that has the {@link com.ususstudios.noway.components.TriggerComponent}
     * @return Whether the player is inside the entity's collision box
     */
    public static boolean checkTriggerCollision(int triggerEntity) {
        // Make sure the entites have the necessary components
        if (Main.world.getEntityComponent(Main.playerId, PositionComponent.class).isEmpty() ||
            Main.world.getEntityComponent(triggerEntity, PositionComponent.class).isEmpty()) return false;
        if (Main.world.getEntityComponent(Main.playerId, CollisionComponent.class).isEmpty() ||
            Main.world.getEntityComponent(triggerEntity, TriggerComponent.class).isEmpty()) return false;

        // Declare components
        CollisionComponent cA = Main.world.getEntityComponent(Main.playerId, CollisionComponent.class).get();
        TriggerComponent cB = Main.world.getEntityComponent(triggerEntity, TriggerComponent.class).get();
        PositionComponent pcA = Main.world.getEntityComponent(Main.playerId, PositionComponent.class).get();
        PositionComponent pcB = Main.world.getEntityComponent(triggerEntity, PositionComponent.class).get();

        float aLeft = pcA.x + cA.offX;
        float aTop = pcA.y + cA.offY;
        float aRight = pcA.x + cA.offX + cA.width;
        float aBottom = pcA.y + cA.offY + cA.height;

        float bLeft = pcB.x + cB.offX;
        float bTop = pcB.y + cB.offY;
        float bRight = pcB.x + cB.offX + cB.width;
        float bBottom = pcB.y + cB.offY + cB.height;

        return aLeft < bRight && aRight > bLeft && aTop < bBottom && aBottom > bTop;
    }
}
