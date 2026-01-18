package com.ususstudios.noway.main;

import com.ususstudios.noway.components.*;
import com.ususstudios.noway.systems.*;
import java.util.*;

public class World {
    private final Map<Integer, List<Component>> entities = new HashMap<>();
    private final List<ECSSystem> updateSystems = new ArrayList<>();
    private final List<ECSSystem> renderSystems = new ArrayList<>();
    private int nextId = 0;

    public void addUpdateSystem(ECSSystem system) {
        this.updateSystems.add(system);
    }

    public void addRenderSystem(ECSSystem system) {
        this.renderSystems.add(system);
    }

    public int createEntity(Component... components) {
        entities.put(nextId, new ArrayList<Component>(Arrays.asList(components)));
        return ++nextId;
    }

    public List<Integer> query(Class<? extends Component>... types) {
        List<Integer> result = new ArrayList<>();
        for (int entity : entities.keySet()) {
            boolean match = true;

            for (Class<? extends Component> type : types) {
                if (!entities.get(entity).contains(type)) {
                    match = false;
                    break;
                }
            }

            if (match) result.add(entity);
        }

        return result;
    }

    public List<Integer> query(Class<? extends Component> type) {
        List<Integer> result = new ArrayList<>();
        for (int entity : entities.keySet()) {
            if (!entities.get(entity).contains(type))
                result.add(entity);
        }

        return result;
    }

    public <T extends Component> T getEntityComponent(int id, Class<T> type) {
        List<Component> entity = entities.get(id);
        if (entity.contains(type)) {
            for (Component element : entity) {
                if (type.isInstance(element)) {
                    return (T) element;
                }
            }
        }
        return null;
    }

    public void update() {
        for (ECSSystem system : updateSystems) {
            system.process(this);
        }

        for (ECSSystem system : renderSystems) {
            system.process(this);
        }
    }
}
