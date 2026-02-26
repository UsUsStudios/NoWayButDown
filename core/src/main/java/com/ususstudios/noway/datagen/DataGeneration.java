package com.ususstudios.noway.datagen;

/**
 * DataGeneration generates JSON data programmatically.
 */
public class DataGeneration {
    /** Runs all the data generators. Is run automatically by the {@code genData} Gradle task. */
    public static void main(String[] args) {
        MapGeneration.generate();
    }
}
