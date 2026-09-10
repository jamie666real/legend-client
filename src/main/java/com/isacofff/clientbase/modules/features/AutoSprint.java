package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

public class AutoSprint extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();

    public AutoSprint() {
        super("AutoSprint", Category.Movement);
        this.description = "Automatically holds sprint while you are moving.";
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc != null && mc.gameSettings != null) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc != null && mc.gameSettings != null) {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), false);
        }
    }

    @Override
    public void onUpdate() {
        if (mc == null || mc.gameSettings == null || mc.player == null) {
            return;
        }

        boolean shouldSprint = mc.player.moveForward > 0.0F || mc.player.moveStrafing != 0.0F;
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), shouldSprint);
    }
}
