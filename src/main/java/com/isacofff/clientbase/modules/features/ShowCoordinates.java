package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;
import net.minecraft.client.Minecraft;

public class ShowCoordinates extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();

    public ShowCoordinates() {
        super("Coordinates", Category.Render);
        this.description = "Shows your current position in the HUD.";
        setHudPosition(6, 6);
    }

    @Override
    public boolean isHudModule() {
        return true;
    }

    @Override
    public String getHudDisplayText() {
        if (TuffClientModules.isEnabled("Streamer")) {
            return "Coordinates hidden";
        }
        return mc == null || mc.player == null ? "Coordinates" : "XYZ: "
                + Math.floor(mc.player.posX) + ", " + Math.floor(mc.player.getEntityBoundingBox().minY)
                + ", " + Math.floor(mc.player.posZ);
    }

    @Override
    public void onRender() {
        if (mc == null || mc.player == null || mc.world == null
                || TuffClientModules.isEnabled("Streamer")) {
            return;
        }
        String coordinates = getHudDisplayText();
        mc.fontRendererObj.drawStringWithShadow(coordinates, (float) getHudX(), (float) getHudY(), 0xFFFFFFFF);
        String dimension = "Dimension: " + mc.player.dimension;
        mc.fontRendererObj.drawStringWithShadow(dimension, (float) getHudX(),
                (float) (getHudY() + mc.fontRendererObj.FONT_HEIGHT + 2), 0xFFB8C7D9);
    }
}
