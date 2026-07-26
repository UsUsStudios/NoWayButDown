package com.ususstudios.noway.components;

import java.util.HashMap;
import com.badlogic.gdx.Gdx;
import com.ususstudios.noway.Main;
import com.ususstudios.noway.main.SoundManager;
import com.ususstudios.noway.rendering.MapTileHandler;
import com.ususstudios.noway.rendering.particles.ParticleConfiguration;
import com.ususstudios.noway.rendering.particles.ParticleInstance;

// TODO: improve this code to make it work better with JSON data
/**
 * An Event is a component that can be called by systems to execute arbitrary code.
 * This is especially used for triggering cutscenes, dialog etc.
 */
public class EventComponent implements Component {
    /** The key is the identifier of the event, which is called by systems. The value is the arbitrary code that is run. */
    HashMap<String, Runnable> events;

    /**
     * The constructor for programmatically creating an {@code EventComponent} with multiple events
     * @param events The key is the identifier of the event, which is called by systems. The value is the arbitrary code that is run.
    */
    public EventComponent(HashMap<String, Runnable> events) {
        this.events = events;
    }

    /**
     * The constructor for programmatically creating an {@code EventComponent} with one event.
     * @param name The identifier of the event, which is called by systems
     * @param event The arbitrary code that is run when the event is called
     */
    public EventComponent(String name, Runnable event) {
        events = new HashMap<>();
        events.put(name, event);
    }

    // TODO: Figure out if this should be in a thread
    /**
     * Call an event to run the arbitrary code associated with it.
     * @param eventName The identifier of the event
     */
    public void call(String eventName) {
        events.get(eventName).run();
    }


    // EXTRA EVENT TYPES: used because I can't extend this class or else the query method won't identify it
    /**
     * The constructor for initializing a {@code SoundEvent} in JSON data.
     * @param eventName The identifier of the event that systems should use to call it
     * @param soundName The name of the sound effect from the sound library to play when the event is called
     */
    public EventComponent(String eventName, String soundName) {
        this(eventName, () -> { SoundManager.playSFX(soundName); } );
    }

    /**
     * The constructor for initializing a {@code PrintTextInteractionEvent} in JSON data.
     * A {@code PrintTextInteractionEvent} shows a dialog in the spirit of "Press [button] to interact",
     * and then when the button is pressed text is printed.
     * @param areaEnteringEventName The identifier of the event that a {@link com.ususstudios.noway.components.TriggerComponent} should use to call it
     * @param interactionText The text that should be shown when the {@link com.ususstudios.noway.components.TriggerComponent} is triggered
     * @param interactionKey The key that needs to be pressed to trigger the text printing event
     * @param textToPrint The text to print when the {@code interactionKey} is pressed
     */
    public EventComponent(String areaEnteringEventName, String interactionText, Integer interactionKey, String textToPrint) {
        this(new HashMap<>(){{
            put("_interacted", () -> { System.out.println(textToPrint); });
        }});
        events.put(areaEnteringEventName, () -> {
            Main.bottomMiddleText = interactionText;
            if (Gdx.input.isKeyJustPressed(interactionKey)) {
                call("_interacted");
            }
        });
    }

    public EventComponent(Integer interactionKey) {
        this(new HashMap<>(){{
            put("_interacted", () -> {
                PositionComponent position = Main.world.getEntityComponent(Main.playerId, PositionComponent.class).get();
                synchronized (Main.particles) {
                    Main.particles.add(new ParticleInstance(new ParticleConfiguration(800, 50, 50, 10, 2, 1, 100, -1.3f, 0.1f, 10, 0, 500, 50, 0f, -0.16f, 2, 0.2f, 3, 0.2f, MapTileHandler.tileTypes.get((short) 3).image()), position));
                }
            });
        }});

        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(16);
                } catch (Exception e) {}
                if (Gdx.input.isKeyJustPressed(interactionKey)) {
                    call("_interacted");
                }
            }
        }).start();
    }
}
