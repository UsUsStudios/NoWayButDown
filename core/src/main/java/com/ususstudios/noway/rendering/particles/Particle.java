package com.ususstudios.noway.rendering.particles;

import java.util.Random;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ususstudios.noway.Main;

public class Particle {
    ParticleConfiguration config;

    float posX;
    float posY;

    float velocityX;
    float velocityY;

    float startSize;
    float endSize;

    float lifeTicks;
    float lifetime;

    Color colour;

    public static Color randomOpaqueColor(Texture texture) {
        FrameBuffer fbo = new FrameBuffer(Pixmap.Format.RGBA8888, texture.getWidth(), texture.getHeight(), false);
        SpriteBatch batch = new SpriteBatch();

        fbo.begin();
        batch.begin();
        batch.draw(texture, 0, 0);
        batch.end();

        Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, texture.getWidth(), texture.getHeight());
        fbo.end();

        Random random = new Random();
        Color color = new Color();
        int attempts = 0;
        do {
            int x = random.nextInt(pixmap.getWidth());
            int y = random.nextInt(pixmap.getHeight());
            Color.rgba8888ToColor(color, pixmap.getPixel(x, y));
            if (++attempts > 1000) { color = Color.WHITE; break; }
        } while (color.a == 0f);

        pixmap.dispose();
        fbo.dispose();
        batch.dispose();
        return color;
    }

    public Particle(ParticleConfiguration config) {
        this.config = config;
        posX = ((float) Math.random() - 0.5f) * config.startPositionVariationX() * 2;
        posY = ((float) Math.random() - 0.5f) * config.startPositionVariationY() * 2;

        float startingAngle = config.angle() + ((float) Math.random() - 0.5f) * config.angleVariation() * 2;
        float velocity = config.velocity() + ((float) Math.random() - 0.5f) * config.velocityVariation() * 2;
        velocityX = (float) Math.cos(startingAngle) * velocity;
        velocityY = (float) Math.sin(startingAngle) * velocity;

        startSize = config.startSize() + ((float) Math.random() - 0.5f) * config.startSizeVariation() * 2;
        endSize = config.endSize() + ((float) Math.random() - 0.5f) * config.endSizeVariation() * 2;

        lifetime = config.lifetime() + ((float) Math.random() - 0.5f) * config.lifetimeVariation() * 2;
        colour = randomOpaqueColor(config.sourceImage().getTexture());
    }

    public void tick(double delta) {
        lifeTicks++;
        velocityX += config.gravityX() * delta;
        velocityY += config.gravityY() * delta;
        posX += velocityX * delta;
        posY += velocityY * delta;
    }

    public void draw(ShapeRenderer renderer, float screenPosX, float screenPosY) {
        if (lifeTicks < lifetime) {
            float t = lifeTicks / lifetime;
            float size = startSize * (1 - t) + endSize * t;  // lerp the start and end size
            renderer.setColor(colour);

            float screenX = posX + screenPosX;
            float screenY = posY + screenPosY;
            if (screenX + 3 > 0 &&
                screenX - 3 < Main.screenWidth &&
                screenY + 3 > 0 &&
                screenY - 3 < Main.screenHeight) {
            renderer.circle(screenX, screenY, size);
                }
        }
    }
}
