package com.ususstudios.noway.main;

import com.ususstudios.noway.components.*;
import com.ususstudios.noway.systems.*;
import java.util.*;
import com.ususstudios.noway.Main;

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
        // Return all the entities that have all the components in the list
        return entities.keySet().stream().filter(e -> {
                // Test if the list has all the components in the list
                return entities.get(e).stream().filter(c ->
                        Arrays.asList(types).contains(c.getClass()))
                    .distinct().count() == types.length;
            }).toList();
    }

    public <T extends Component> Optional<T> getEntityComponent(int id, Class<T> type) {
        List<Component> entity = entities.get(id);
        for (Component element : entity) {
            if (type.isInstance(element)) {
                return Optional.of((T) element);
            }
        }
        return Optional.empty();
    }

    public void update() {
        // Main.LOGGER.info(java.util.stream.Stream.of(updateSystems, renderSystems)
        //             .flatMap(List::stream)
        //             .collect(java.util.stream.Collectors.toList()).toString());
        for (ECSSystem system : updateSystems) {
            system.process(this);
        }

        for (ECSSystem system : renderSystems) {
            system.process(this);
        }
    }
}
