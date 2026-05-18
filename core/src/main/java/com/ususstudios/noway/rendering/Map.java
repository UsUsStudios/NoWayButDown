package com.ususstudios.noway.rendering;

import com.ususstudios.noway.components.*;
import java.util.List;

/**
 * Record for storing map data
 * @param name The name of the map
 * @param width How many tiles across is the map
 * @param height How many tiles tall is the map
 * @param spawnX The x position where the player spawns (in tiles)
 * @param spawnY The y position where the player spawns (in tiles)
 * @param layer1 The first layer of the map: ground tiles, no collision.
 * @param layer2 The second layer of the map: colliding objects and stuff.
 * @param layer3 The third layer of the map: should be drawn above entities.
 * @param songs The list of song names from the {@link com.ususstudios.noway.main.SoundManager} sound Library that should be played ambiently in the map
 * @param entities The list of entities in the structure: {@code [{"ComponentName": ["arg1", "arg2"], "Component2": []}, {"Component3": []}]}
 */
public record Map(String name, int width, int height, int spawnX, int spawnY,
                  short[][] layer1, short[][] layer2, short[][] layer3, List<Object> songs,
                  List<List<Component>> entities) {}
