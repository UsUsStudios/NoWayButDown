package com.ususstudios.noway.rendering;

import com.ususstudios.noway.components.*;
import java.util.List;

// Record for storing map data
public record Map(String name, int width, int height, int spawnX, int spawnY,
                  String[][] layer1, String[][] layer2, String[][] layer3, List<Object> songs,
                  List<List<Component>> entities) {}
