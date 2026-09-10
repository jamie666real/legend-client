package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;
import net.minecraft.client.Minecraft;

public class AutoJump extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();

    public AutoJump() {
        super("AutoJump", Category.Movement);
        this.description = "Automatically jumps when needed.";
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc != null && mc.gameSettings != null) {
            mc.gameSettings.autoJump = true;
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc != null && mc.gameSettings != null) {
            mc.gameSettings.autoJump = false;
        }
    }
}
