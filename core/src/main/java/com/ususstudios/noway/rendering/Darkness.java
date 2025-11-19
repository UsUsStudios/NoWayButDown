package com.ususstudios.noway.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.MathUtils;
import com.ususstudios.noway.Main;
import com.ususstudios.noway.entity.Entity;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Darkness {
	/// Add entities that emit light here. These entities must have the "light_radius" and "light_intensity"
	/// properties in the {@code properties} variable set to proper float values.
	private final ArrayList<Entity> lightSources = new ArrayList<>();
	public float ambientDarkness = 0.9f;  // How dark is it without lights (0.0 = no darkness, 1.0 = complete darkness)
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

    public void addLightSource(Entity entity) {
		lightSources.add(entity);
		if (entity.properties.containsKey("light_radius")) {
			try {
				float radius = (float) (entity.properties.get("light_radius"));
				entity.properties.put("light_radius", radius);
			} catch (NumberFormatException e) {
				Main.LOGGER.warn("Entity {} light_radius property is not a number. Setting default.", entity.name);
				entity.properties.put("light_radius", 100.0f); // Default radius
			}
		} else {
            Main.LOGGER.warn("Entity {} missing light_radius property. Setting default.", entity.name);
			entity.properties.put("light_radius", 100.0f); // Default radius
		}
		if (entity.properties.containsKey("light_intensity")) {
			try {
				float radius = (float) (entity.properties.get("light_intensity"));
				entity.properties.put("light_intensity", radius);
			} catch (NumberFormatException e) {
				Main.LOGGER.warn("Entity {} light_intensity property is not a number. Setting default.", entity.name);
				entity.properties.put("light_intensity", 1.0f); // Default radius
			}
		} else {
			Main.LOGGER.warn("Entity {} missing light_intensity property. Setting default.", entity.name);
			entity.properties.put("light_intensity", 1.0f); // Default radius
		}
	}

	public void removeLightSource(Entity entity) {
		lightSources.remove(entity);
	}

	public ArrayList<Entity> getLightSources() {
		return lightSources;
	}

	// Draws the darkness overlay
    public void draw() {
        // 1. Create a framebuffer to draw darkness layer
        FrameBuffer darknessFbo = new FrameBuffer(Pixmap.Format.RGBA8888, Main.screenWidth, Main.screenHeight, false);
        darknessFbo.begin();

        // 2. Clear framebuffer with ambient darkness
        Gdx.gl.glClearColor(0f, 0f, 0f, ambientDarkness);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 3. Draw radial lights
        Main.batch.begin();
        for (Entity lightSource : lightSources) {
            int radius = Math.round((float) lightSource.properties.get("light_radius"));
            float intensity = ((Number) lightSource.properties.get("light_intensity")).floatValue();
            intensity = Math.max(0f, Math.min(1f, intensity));

            // Calculate position relative to camera
            float x = lightSource.x - Main.player.cameraX + Main.screenWidth / 2f + 24 - radius;
            float y = lightSource.y - Main.player.cameraY + Main.screenHeight / 2f + 24 - radius;

            // Assume you have a white radial gradient texture with alpha falloff
            Texture lightTex = radialLightTexture;
            Main.batch.setColor(1f, 1f, 1f, intensity);
            Main.batch.draw(lightTex, x, y, radius*2, radius*2);
        }
        Main.batch.end();

        darknessFbo.end();

        // 4. Draw darkness layer over the scene
        Main.batch.begin();
        TextureRegion darknessRegion = new TextureRegion(darknessFbo.getColorBufferTexture());
        darknessRegion.flip(false, true); // FBO textures are flipped vertically
        Main.batch.setColor(1f, 1f, 1f, 1f);
        Main.batch.draw(darknessRegion, 0, 0, Main.screenWidth, Main.screenHeight);
        Main.batch.end();

        darknessFbo.dispose();
    }

}
