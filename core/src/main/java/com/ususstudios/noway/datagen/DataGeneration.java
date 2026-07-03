package com.ususstudios.noway.datagen;

/**
 * DataGeneration generates JSON data programmatically.
 * It's currently completely unused because MapGeneration is deprecated.
 */
public class DataGeneration {
    /**
     * Runs all the data generators. Is run automatically by the {@code genData} Gradle task.
     * @param args Arguments. unused.
    */
    public static void main(String[] args) {
        MapGeneration.generate();
    }
}
