package com.ususstudios.noway.rendering.particles;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;
import com.ususstudios.noway.Main;
import com.ususstudios.noway.components.PositionComponent;

public class ParticleInstance {
    ParticleConfiguration config;
    ArrayList<Particle> particles = new ArrayList<>();
    int age = 0;
    int nextSpawnAge = 1;
    Thread thread;
    int particlesToAdd = 0;
    private final ReentrantLock particleLock = new ReentrantLock();
    PositionComponent position;

    public ParticleInstance(ParticleConfiguration config, PositionComponent position) {
        this.config = config;
        this.position = position;
        thread = new Thread(() -> {
            while (true) {
                int ticksSinceLast = 0;
                try {
                    ticksSinceLast++;
                    Thread.sleep(16);
                    if (tick(ticksSinceLast)) ticksSinceLast = 0;
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
        thread.start();
    }

    // returns whether it ticked (otherwise it skipped this tick)
    private boolean tick(int ticks) {
        boolean locked = particleLock.tryLock();
        if (!locked) return false;
        age++;

        if (!locked) particleLock.lock();

        try {
            Iterator<Particle> it = particles.iterator();

            while (it.hasNext()) {
                Particle p = it.next();

                p.tick(ticks);  // do each particle only every other tick
                if (p.lifeTicks >= p.lifetime) {
                    it.remove();
                }
            }
        } finally {
            particleLock.unlock();
        }

        if (age >= nextSpawnAge && (age <= config.duration() || config.duration() == -1)) {
            nextSpawnAge = age + Math.round(config.emissionTicks() + ((float) Math.random() - 0.5f) * config.emissionTicksVariation() * 2);
            particlesToAdd += config.emissionCount();
        }

        return true;
    }

    public void draw() {
        particleLock.lock();
        try {
            while (particlesToAdd > 0) {
                if (particles.size() < config.maxParticles() && (age <= config.duration() || config.duration() == -1)) {
                    particles.add(new Particle(config));
                }
                particlesToAdd--;
            }

            for (Particle p : particles) {
                float screenX = position.x - Main.cameraX + Main.screenWidth / 2f;
                // flip the y axis because we flip the projection (I think)
                float screenY = Main.cameraY - position.y + Main.screenHeight / 2f;
                p.draw(Main.shapes, screenX, screenY);
            }
        } finally {
            particleLock.unlock();
        }
    }
}
