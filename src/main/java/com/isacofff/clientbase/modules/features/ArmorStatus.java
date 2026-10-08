package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;

public class ArmorStatus extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();

    public ArmorStatus() {
        super("ArmorStatus", Category.Render);
        this.description = "Shows armor durability info on screen.";
        setHudPosition(8, 8);
    }

    @Override
    public boolean isHudModule() {
        return true;
    }

    @Override
    public String getHudDisplayText() {
        return "Armor Status";
    }

    @Override
    public void onRender() {
        if (mc == null || mc.player == null || mc.player.inventory == null) {
            return;
        }
        ScaledResolution resolution = mc.scaledResolution;
        if (resolution == null) {
            return;
        }

        int baseX = getHudX();
        int slotY = getHudY();
        for (int displaySlot = 0; displaySlot < 4; ++displaySlot) {
            ItemStack stack = mc.player.inventory.armorInventory.get(3 - displaySlot);
            if (stack == null || stack.func_190926_b()) {
                continue;
            }

            int x = baseX + displaySlot * 20;
            Gui.drawRect(x, slotY, x + 18, slotY + 18, 0xAA101010);
            Gui.drawRect(x, slotY, x + 18, slotY + 1, 0xFF555555);
            Gui.drawRect(x, slotY + 17, x + 18, slotY + 18, 0xFF000000);
            mc.getRenderItem().renderItemAndEffectIntoGUI(mc.player, stack, x + 1, slotY + 1);

            if (stack.isItemStackDamageable() && stack.getMaxDamage() > 0) {
                int remaining = Math.max(0, stack.getMaxDamage() - stack.getItemDamage());
                int percent = remaining * 100 / stack.getMaxDamage();
                String durability = percent + "%";
                int color = percent <= 25 ? 0xFFFF5555 : percent <= 50 ? 0xFFFFFF55 : 0xFFFFFFFF;
                int textX = x + (18 - mc.fontRendererObj.getStringWidth(durability)) / 2;
                mc.fontRendererObj.drawStringWithShadow(durability, textX, slotY - 9, color);
            }
        }
    }
}
