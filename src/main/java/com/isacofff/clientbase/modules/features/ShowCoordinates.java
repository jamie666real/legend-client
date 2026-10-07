package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;
import net.minecraft.client.Minecraft;

public class ShowCoordinates extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();

    public ShowCoordinates() {
        super("Coordinates", Category.Render);
        this.description = "Shows your current position in the HUD.";
    }

    @Override
    public void onRender() {
        if (mc == null || mc.player == null || mc.world == null) {
            return;
        }
        String coordinates = "XYZ: " + Math.floor(mc.player.posX) + ", "
                + Math.floor(mc.player.getEntityBoundingBox().minY) + ", " + Math.floor(mc.player.posZ);
        mc.fontRendererObj.drawStringWithShadow(coordinates, 6.0F, 6.0F, 0xFFFFFFFF);
        String dimension = "Dimension: " + mc.player.dimension;
        mc.fontRendererObj.drawStringWithShadow(dimension, 6.0F, 17.0F, 0xFFB8C7D9);
    }
}
