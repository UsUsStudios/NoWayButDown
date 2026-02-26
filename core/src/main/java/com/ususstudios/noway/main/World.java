package com.ususstudios.noway.main;

import com.ususstudios.noway.components.*;
import com.ususstudios.noway.systems.*;
import java.util.*;

/**
 * This (theoretically) singleton class holds all the ECS stuff and the ways to interact with them.
 */
public class World {
    /** The keys are the IDs of the entities and the value is the list of components of the entity */
    public final Map<Integer, List<Component>> entities = new HashMap<>();
    /** The list of the systems that should be ran in order in the ubdate cycle */
    private final List<ECSSystem> updateSystems = new ArrayList<>();
    /** The list of the systems that should be ran in order in the render cycle */
    private final List<ECSSystem> renderSystems = new ArrayList<>();
    /** The next available entity ID to assign */
    private int nextId = 0;

    /**
     * Adds a new system to the {@code updateSystems} list
     * This is required for any system that should be ran before the rendering cycle.
     * @param system An instance of the system that should be put in the {@code updateSystems} list
     */
    public void addUpdateSystem(ECSSystem system) {
        this.updateSystems.add(system);
    }

    /**
     * Adds a new system to the {@code renderSystems} list
     * This is required for any system that should be ran after the updating cycle.
     * @param system An instance of the system that should be put in the {@code renderingSystems} list
     */
    public void addRenderSystem(ECSSystem system) {
        this.renderSystems.add(system);
    }

    /**
     * Add an entity with the given components, and the next available ID.
     * @param components A list of instances of all the components the entity should have.
     * @return The ID of the entity that was just created
     */
    public int createEntity(Component... components) {
        entities.put(nextId, new ArrayList<Component>(Arrays.asList(components)));
        return nextId++;
    }

    /**
     * Fetches a list of entities that have all of the component types in the list
     * @param types The list of component types that the entities fetched must have
     * @return A list of all the entities that have all the component types in the list
     */
    public List<Integer> query(Class<? extends Component>... types) {
        // Return all the entities that have all the components in the list
        return entities.keySet().stream().filter(e -> {
                // Test if the list has all the components in the list
                return entities.get(e).stream().filter(c ->
                        Arrays.asList(types).contains(c.getClass()))
                    .distinct().count() == types.length;
            }).toList();
    }

    /**
     * Gets the instance of the given component type that a specific entity has
     * @param id The ID of the entity that you want to fetch the component of
     * @param type The Class of the component type you want to fetch
     * @param <T> The component type
     * @return the instance of the given component type that belonds to the entity
     */
    public <T extends Component> Optional<T> getEntityComponent(int id, Class<T> type) {
        List<Component> entity = entities.get(id);
        if (entity == null) return Optional.empty();
        for (Component element : entity) {
            if (type.isInstance(element)) {
                return Optional.of((T) element);
            }
        }
        return Optional.empty();
    }

    /**
     * Runs a tick of the update cycle
     * This will run the {@code process} method of every system in the {@code updateSystems} list in order.
     */
    public void update() {
        for (ECSSystem system : updateSystems) {
            system.process(this);
        }

    }

    /**
     * Runs a tick of the render cycle
     * This will run the {@code process} method of every system in the {@code renderSystems} list in order.
     */
    public void render() {
        for (ECSSystem system : renderSystems) {
            system.process(this);
        }
    }
}
