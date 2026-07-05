package com.ususstudios.noway.rendering;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

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
    public static record ParticleConfiguration(int maxParticles, int duration, int lifetime, int lifetimeVariation,
            int emissionTicks, int emissionTicksVariation, int emissionCount, float angle, float angleVariation,
            float velocity, float velocityVariation, float startPositionVariationX, float startPositionVariationY,
            float gravityX, float gravityY,

            float startSize, float startSizeVariation, float endSize, float endSizeVariation, float sourceImage) {}

    public static class Particle {
        ParticleConfiguration config;
        float posX;
        float posY;
        float velocityX;
        float velocityY;
        public Particle(ParticleConfiguration config) {
            this.config = config;
            posX = ((float) Math.random() - 0.5f) * config.startPositionVariationX() * 2;
            posY = ((float) Math.random() - 0.5f) * config.startPositionVariationY() * 2;

            float startingAngle = config.angle() + ((float) Math.random() - 0.5f) * config.angleVariation() * 2;
            float velocity = config.velocity() + ((float) Math.random() - 0.5f) * config.velocityVariation() * 2;
            velocityX = (float) Math.cos(startingAngle) * velocity;
            velocityY = (float) Math.sin(startingAngle) * velocity;
        }

        public void tick(double delta) {
            velocityX += config.gravityX() * delta;
            velocityY += config.gravityY() * delta;
            posX += velocityX * delta;
            posY += velocityY * delta;
        }

        public void draw(ShapeRenderer renderer, float worldPosX, float worldPosY) {
            renderer.circle(posX + worldPosX, posY + worldPosY, 2f);

        }
    }
}
