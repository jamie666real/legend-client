package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;
import net.minecraft.client.Minecraft;

public class DirectionHud extends Module {

    private static final String[] DIRECTIONS = { "S", "W", "N", "E" };
    private final Minecraft mc = Minecraft.getMinecraft();

    public DirectionHud() {
        super("Direction", Category.Render);
        this.description = "Displays your current compass direction.";
        setHudPosition(6, 96);
    }

    @Override
    public boolean isHudModule() {
        return true;
    }

    @Override
    public String getHudDisplayText() {
        if (mc == null || mc.player == null) {
            return "Direction";
        }
        int direction = (int) Math.floor(mc.player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        int heading = ((int) Math.floor(mc.player.rotationYaw) % 360 + 360) % 360;
        return DIRECTIONS[direction] + " (" + heading + "\u00b0)";
    }

    @Override
    public void onRender() {
        if (mc == null || mc.player == null || mc.fontRendererObj == null) {
            return;
        }

        mc.fontRendererObj.drawStringWithShadow(getHudDisplayText(), (float) getHudX(), (float) getHudY(),
                0xFFFFD21F);
    }
}
