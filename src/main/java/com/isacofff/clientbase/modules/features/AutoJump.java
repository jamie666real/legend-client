package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;
import net.minecraft.client.Minecraft;

public class AutoJump extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();
    private boolean previousAutoJump;

    public AutoJump() {
        super("AutoJump", Category.Movement);
        this.description = "Temporarily enables Minecraft's automatic jumping option.";
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc != null && mc.gameSettings != null) {
            previousAutoJump = mc.gameSettings.autoJump;
            mc.gameSettings.autoJump = true;
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc != null && mc.gameSettings != null) {
            mc.gameSettings.autoJump = previousAutoJump;
        }
    }
}
