package com.ususstudios.noway.components;

import java.util.HashMap;
import com.ususstudios.noway.main.SoundManager;

/**
 * An Event is a component that can be called by systems to execute arbitrary code.
 * This is especially used for triggering cutscenes, dialog etc.
 */
public class EventComponent implements Component {
    HashMap<String, Runnable> events;

    public EventComponent(HashMap<String, Runnable> events) {
        this.events = events;
    }

    public EventComponent(String name, Runnable event) {
        events = new HashMap<>();
        events.put(name, event);
    }

    // TODO: Figure out if this should be in a thread
    public void call(String eventName) {
        events.get(eventName).run();
    }




    // EXTRA EVENT TYPES: used because I can't extend this class because the query method won't identify it
    public EventComponent(String eventName, String soundName) {
        this(eventName, () -> { SoundManager.playSFX(soundName); } );
    }
}
