package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;
import net.minecraft.client.Minecraft;

public class TimeChanger extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();

    public TimeChanger() {
        super("TimeChanger", Category.Render);
        this.description = "Lets you preview a custom sky/time setting.";
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc != null && mc.world != null) {
            mc.world.setWorldTime(6000L);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc != null && mc.world != null) {
            mc.world.setWorldTime(0L);
        }
    }
}
