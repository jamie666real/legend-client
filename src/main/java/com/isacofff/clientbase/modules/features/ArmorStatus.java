package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;

public class ArmorStatus extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();

    public ArmorStatus() {
        super("ArmorStatus", Category.Render);
        this.description = "Shows armor durability info on screen.";
    }

    @Override
    public void onRender() {
        if (mc == null || mc.player == null || mc.player.inventory == null) {
            return;
        }
        ScaledResolution resolution = new ScaledResolution(mc);
        int y = 6;
        for (ItemStack stack : mc.player.inventory.armorInventory) {
            if (stack != null && !stack.func_190926_b()) {
                String itemName = stack.getDisplayName();
                String durability = stack.isItemStackDamageable()
                        ? " " + (stack.getMaxDamage() - stack.getItemDamage()) + "/" + stack.getMaxDamage()
                        : "";
                String line = itemName + durability;
                int x = resolution.getScaledWidth() - mc.fontRendererObj.getStringWidth(line) - 6;
                mc.fontRendererObj.drawStringWithShadow(line, (float) x, (float) y, 0xFFFFFFFF);
                y += 11;
            }
        }
    }
}
