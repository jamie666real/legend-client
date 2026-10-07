package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;

public class FPSBoost extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();
    private int previousRenderDistance;
    private int previousParticles;
    private int previousClouds;
    private int previousAmbientOcclusion;
    private boolean previousFancyGraphics;
    private boolean previousEntityShadows;
    private boolean settingsCaptured;

    public FPSBoost() {
        super("FPS Boost", Category.Render);
        this.description = "Uses lower visual settings to reduce rendering load. Restores your settings when disabled.";
    }

    @Override
    public boolean shouldPersist() {
        return false;
    }

    @Override
    public void onEnable() {
        if (mc == null || mc.gameSettings == null) {
            return;
        }

        GameSettings settings = mc.gameSettings;
        previousRenderDistance = settings.renderDistanceChunks;
        previousParticles = settings.particleSetting;
        previousClouds = settings.clouds;
        previousAmbientOcclusion = settings.ambientOcclusion;
        previousFancyGraphics = settings.fancyGraphics;
        previousEntityShadows = settings.entityShadows;
        settingsCaptured = true;

        settings.renderDistanceChunks = previousRenderDistance > 0 ? Math.min(previousRenderDistance, 6) : 6;
        settings.particleSetting = 2;
        settings.clouds = 0;
        settings.ambientOcclusion = 0;
        settings.fancyGraphics = false;
        settings.entityShadows = false;
        reloadRenderers();
        settings.saveOptions();
    }

    @Override
    public void onDisable() {
        if (!settingsCaptured || mc == null || mc.gameSettings == null) {
            return;
        }

        GameSettings settings = mc.gameSettings;
        settings.renderDistanceChunks = previousRenderDistance;
        settings.particleSetting = previousParticles;
        settings.clouds = previousClouds;
        settings.ambientOcclusion = previousAmbientOcclusion;
        settings.fancyGraphics = previousFancyGraphics;
        settings.entityShadows = previousEntityShadows;
        reloadRenderers();
        settings.saveOptions();
        settingsCaptured = false;
    }

    private void reloadRenderers() {
        if (mc.renderGlobal != null) {
            mc.renderGlobal.loadRenderers();
        }
    }
}
