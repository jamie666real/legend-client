package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class BiomeHud extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();

    public BiomeHud() {
        super("Biome", Category.Render);
        this.description = "Displays the biome at your current position.";
        setHudPosition(6, 78);
    }

    @Override
    public boolean isHudModule() {
        return true;
    }

    @Override
    public String getHudDisplayText() {
        return mc == null || mc.player == null || mc.world == null
                ? "Biome" : "Biome: " + mc.world.getBiome(mc.player.getPosition()).getBiomeName();
    }

    @Override
    public void onRender() {
        if (mc == null || mc.player == null || mc.world == null || mc.scaledResolution == null) {
            return;
        }

        mc.fontRendererObj.drawStringWithShadow(getHudDisplayText(), (float) getHudX(), (float) getHudY(),
                0xFFFFFFFF);
    }
}
