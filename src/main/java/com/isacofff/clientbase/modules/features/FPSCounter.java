package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class FPSCounter extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();

    public FPSCounter() {
        super("FPS", Category.Render);
        this.description = "Shows the current client frame rate.";
        setHudPosition(6, 42);
    }

    @Override
    public boolean isHudModule() {
        return true;
    }

    @Override
    public String getHudDisplayText() {
        return Minecraft.getDebugFPS() + " FPS";
    }

    @Override
    public void onRender() {
        if (mc == null || mc.scaledResolution == null || mc.fontRendererObj == null) {
            return;
        }

        mc.fontRendererObj.drawStringWithShadow(getHudDisplayText(), (float) getHudX(), (float) getHudY(),
                0xFFFFD21F);
    }
}
