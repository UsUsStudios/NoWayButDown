package com.ususstudios.noway.components;

import java.util.HashMap;

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
}
