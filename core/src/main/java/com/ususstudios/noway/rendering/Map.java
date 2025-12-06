package com.ususstudios.noway.rendering;

import com.ususstudios.noway.main.UtilityTool.Tuple;
import java.util.List;
import java.util.Properties;

// Record for storing map data
public record Map(String name, int width, int height, int spawnX, int spawnY,
                  int[][] layer1, int[][] layer2, int[][] layer3, List<Object> songs,
                  List<String> objectNames, List<Tuple<Float, Float>> objectPos, List<Properties> objectProperties) {}
