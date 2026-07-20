package com.ususstudios.noway.rendering.particles;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.ususstudios.noway.Main;

public class ParticleInstance {
    ParticleConfiguration config;
    ArrayList<Particle> particles = new ArrayList<>();
    int age = 0;
    int nextSpawnAge = 1;
    boolean listLock = false;

    public ParticleInstance(ParticleConfiguration config) {
        this.config = config;
    }

    public void tick(double delta) {
        age += 1;
        if (age == nextSpawnAge && (age >= config.duration() || config.duration() == -1)) {
            nextSpawnAge = age + Math.round(config.emissionTicks() + ((float) Math.random() - 0.5f) * config.emissionTicksVariation() * 2);
            listLock = true;
            for (int i = 0; i < config.emissionCount(); i += 1) {
                if (particles.size() < config.maxParticles()) particles.add(new Particle(config));
            }
            listLock = false;
        }

        new Thread(() -> {
            ArrayList<Particle> toDelete = new ArrayList<>();
            for (Particle p : particles) {
                p.tick(delta);
                if (p.lifeTicks >= p.lifetime) {
                    toDelete.add(p);
                }
            }

            for (Particle p : toDelete) {
                try {
                    while (listLock) { Thread.sleep(10); }
                } catch (Exception e) {}

                particles.remove(p);
            }
        }).start();
    }

    public void draw() {
        Main.shapes.begin(ShapeType.Filled);
        listLock = true;
        for (Particle p : particles) {
            p.draw(Main.shapes, 500, 700);
        }
        listLock = false;
        Main.shapes.end();
    }
}
