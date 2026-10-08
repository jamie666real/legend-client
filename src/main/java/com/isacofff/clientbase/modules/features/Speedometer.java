package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

public class Speedometer extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();
    private World previousWorld;
    private double previousX;
    private double previousZ;
    private int speedTenths;

    public Speedometer() {
        super("Speedometer", Category.Render);
        this.description = "Displays horizontal movement speed in blocks per second.";
        setHudPosition(6, 114);
    }

    @Override
    public boolean isHudModule() {
        return true;
    }

    @Override
    public String getHudDisplayText() {
        return "Speed: " + speedTenths / 10 + "." + speedTenths % 10 + " m/s";
    }

    @Override
    public void onUpdate() {
        if (mc == null || mc.player == null || mc.world == null) {
            previousWorld = null;
            speedTenths = 0;
            return;
        }

        if (previousWorld != mc.world) {
            previousWorld = mc.world;
            previousX = mc.player.posX;
            previousZ = mc.player.posZ;
            speedTenths = 0;
            return;
        }

        double deltaX = mc.player.posX - previousX;
        double deltaZ = mc.player.posZ - previousZ;
        speedTenths = (int) (Math.sqrt(deltaX * deltaX + deltaZ * deltaZ) * 200.0D + 0.5D);
        previousX = mc.player.posX;
        previousZ = mc.player.posZ;
    }

    @Override
    public void onRender() {
        if (mc == null || mc.fontRendererObj == null) {
            return;
        }

        mc.fontRendererObj.drawStringWithShadow(getHudDisplayText(), (float) getHudX(), (float) getHudY(),
                0xFFFFFFFF);
    }
}
