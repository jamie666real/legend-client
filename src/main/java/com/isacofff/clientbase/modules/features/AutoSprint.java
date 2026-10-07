package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;
import net.minecraft.client.Minecraft;

public class AutoSprint extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();

    public AutoSprint() {
        super("AutoSprint", Category.Movement);
        this.description = "Automatically holds sprint while you are moving.";
    }

    @Override
    public void onUpdate() {
        if (mc == null || mc.gameSettings == null || mc.player == null || mc.player.movementInput == null) {
            return;
        }

        boolean canSprint = mc.player.getFoodStats().getFoodLevel() > 6 || mc.player.capabilities.allowFlying;
        boolean shouldSprint = mc.player.movementInput.field_192832_b >= 0.8F && canSprint
                && !mc.player.isHandActive() && !mc.player.isSneaking() && !mc.player.isCollidedHorizontally;
        mc.player.setSprinting(shouldSprint);
    }
}
