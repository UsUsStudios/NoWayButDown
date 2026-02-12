package com.ususstudios.noway.systems;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.ususstudios.noway.Main;
import com.ususstudios.noway.components.LightSourceComponent;
import com.ususstudios.noway.components.PositionComponent;
import com.ususstudios.noway.main.World;

public class DarknessSystem implements ECSSystem {
	public float ambientDarkness = 0.92f;  // How dark is it without lights (0.0 = no darkness, 1.0 = complete darkness)
    Texture radialLightTexture = createRadialLight(128);

    public static Texture createRadialLight(int radius) {
        int size = radius * 2;

        // Create a Pixmap with RGBA8888 format
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);

        // Draw radial gradient
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                float dx = x - radius;
                float dy = y - radius;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                float alpha = MathUtils.clamp(1f - (distance / radius), 0f, 1f);

                // Set pixel color: white with alpha
                pixmap.setColor(1f, 1f, 1f, alpha);
                pixmap.drawPixel(x, y);
            }
        }

        // Create texture from pixmap
        Texture texture = new Texture(pixmap);
        pixmap.dispose(); // Dispose Pixmap, texture keeps the data

        return texture;
    }

    @Override
	// Draws the darkness overlay
    public void process(World world) {
        // 1. Create FBO for darkness
        FrameBuffer darknessFbo = new FrameBuffer(Pixmap.Format.RGBA8888, Main.screenWidth, Main.screenHeight, false);
        darknessFbo.begin();

        // Clear with black at full opacity
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Start batch for drawing lights
        Main.batch.begin();

        // Use blending that allows lights to "cut holes" in the darkness
        Main.batch.setBlendFunction(GL20.GL_ZERO, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Draw all light sources
        for (int entity : world.query(LightSourceComponent.class, PositionComponent.class)) {
            LightSourceComponent component = world.getEntityComponent(entity, LightSourceComponent.class).get();
            PositionComponent pc = world.getEntityComponent(entity, PositionComponent.class).get();
            float radius = component.lightRadius;
            float intensity = component.lightIntensity;
            intensity = Math.max(0f, Math.min(1f, intensity)) + (Main.random.nextFloat()-.5f) * component.lightFlickering;

            float x = pc.x - Main.cameraX + Main.screenWidth / 2f + 24 - radius + component.offX;
            float y = pc.y - Main.cameraY + Main.screenHeight / 2f + 24 - radius + component.offY;

            Main.batch.setColor(1f, 1f, 1f, intensity);
            Main.batch.draw(radialLightTexture, x, y, radius * 2, radius * 2);
        }


        Main.batch.end();
        darknessFbo.end();

        // 2. Draw the darkness overlay to the main scene
        Main.batch.begin();

        // Use normal alpha blending but control overall darkness with color alpha
        Main.batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        Main.batch.setColor(1f, 1f, 1f, ambientDarkness);

        TextureRegion darknessRegion = new TextureRegion(darknessFbo.getColorBufferTexture());
        Main.batch.draw(darknessRegion, 0, 0);

        // Reset to normal settings
        Main.batch.setColor(1f, 1f, 1f, 1f);

        Main.batch.end();

        darknessFbo.dispose();
    }
}
