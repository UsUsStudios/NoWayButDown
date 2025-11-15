package com.ususstudios.noway.rendering;

// Record for storing map data
public record Map(String name, int width, int height, int spawnX, int spawnY,
                  int[][] layer1, int[][] layer2, int[][] layer3) {}