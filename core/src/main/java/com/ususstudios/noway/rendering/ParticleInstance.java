package com.ususstudios.noway.rendering;

public class ParticleInstance {
    /**
     * A record that contains all the information that a {@link com.ususstudios.noway.rendering.ParticleInstance} to generate all the particles.
     * @param maxParticles How many particles are allowed to exist at a time
     * @param duration How many ticks to spawn particles for before new particles cannot spawn
     * @param lifetime How many ticks should particles live for
     * @param lifetimeVariation By how much the particle lifetime value can vary randomly in either direction
     * @param emissionTicks Every how many ticks should new particles try to spawn
     * @param emissionTicksVariation By how much the emissionTicks value can vary randomly in either direction
     * @param emissionCount How many particles should try to spawn every time particles are supposed to spawn
     * @param angle The angle at which a particle's starting velocity should be pointed
     * @param angleVariation By how much the starting angle value can vary randomly
     * @param velocity The velocity at which a particle moves in the starting angle when it is spawned
     * @param velocityVariation By how much the starting velocity value can vary randomly in either direction
     * @param startPositionVariationX By how much much the starting postion can vary randomly from the center in the x axis
     * @param startPositionVariationY By how much much the starting postion can vary randomly from the center in the y axis
     * @param gravityX By how much to accelerate particles in the x axis every tick
     * @param gravityY By how much to accelerate particles in the y axis every tick
     *
     * @param startSize The size at which particles spawn
     * @param startSizeVariation By how much the starting size value can vary randomly in either direction
     * @param endSize The size that particles will reach when they despawn
     * @param endSizeVariation By how much the ending size value can vary randomly in either direction
     * @param sourceImage Each particle's colour will be a colour selected randomly from this image
     */
    public record ParticleConfiguration(int maxParticles, int duration, int lifetime, int lifetimeVariation,
            int emissionTicks, int emissionTicksVariation, int emissionCount, double angle, double angleVariation,
            double velocity, double velocityVariation, double startPositionVariationX, double startPositionVariationY,
            double gravityX, double gravityY,

            double startSize, double startSizeVariation, double endSize, double endSizeVariation, Image sourceImage) {}

}
