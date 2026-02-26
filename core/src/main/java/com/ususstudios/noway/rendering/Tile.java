package com.ususstudios.noway.rendering;

/**
 * Class for a tile type
 * @param image The image that should be drawn at the tile position
 * @param collision The 2D array with each boolean representing a collision point that either can or can't collide
 */
public record Tile (Image image, boolean[][] collision) {}
