package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.enchantment.EnchantmentHelper;

public class AutoElytra extends Module {

    private static final int CHEST_SLOT = 6;
    private int equipDelayTicks;
    private int flightRequestCooldown;

    public AutoElytra() {
        super("Auto Elytra", Category.Movement);
        description = "Automatically equips a usable elytra and deploys it while falling.";
    }

    @Override
    public void onUpdate() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.player == null || mc.world == null) {
            resetTimers();
            return;
        }

        EntityPlayerSP player = mc.player;
        if (player.onGround || player.motionY >= 0.0D || player.isElytraFlying()
                || player.capabilities.isFlying || player.isSpectator()) {
            equipDelayTicks = 0;
            flightRequestCooldown = 0;
            return;
        }

        if (flightRequestCooldown > 0) {
            --flightRequestCooldown;
        }
        if (equipDelayTicks > 0) {
            --equipDelayTicks;
            return;
        }

        ItemStack chestStack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (!isUsableElytra(chestStack)) {
            if (equipElytra(mc, player)) {
                equipDelayTicks = 2;
            }
            return;
        }

        if (flightRequestCooldown == 0) {
            player.connection.sendPacket(new CPacketEntityAction(player,
                    CPacketEntityAction.Action.START_FALL_FLYING));
            flightRequestCooldown = 5;
        }
    }

    @Override
    public void onDisable() {
        resetTimers();
    }

    private static boolean isUsableElytra(ItemStack stack) {
        return stack.getItem() == Items.ELYTRA && ItemElytra.isBroken(stack);
    }

    private static boolean equipElytra(Minecraft mc, EntityPlayerSP player) {
        if (mc.currentScreen != null || player.openContainer != player.inventoryContainer
                || !player.inventory.getItemStack().func_190926_b()) {
            return false;
        }

        ItemStack chestStack = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        if (!player.isCreative() && !chestStack.func_190926_b()
                && EnchantmentHelper.func_190938_b(chestStack)) {
            return false;
        }

        int inventoryIndex = findUsableElytra(player);
        if (inventoryIndex < 0) {
            return false;
        }

        Container inventory = player.inventoryContainer;
        int windowId = inventory.windowId;
        if (inventoryIndex < 9) {
            mc.playerController.windowClick(windowId, CHEST_SLOT, inventoryIndex, ClickType.SWAP, player);
        } else {
            int inventorySlot = inventoryIndex;
            mc.playerController.windowClick(windowId, CHEST_SLOT, 0, ClickType.PICKUP, player);
            mc.playerController.windowClick(windowId, inventorySlot, 0, ClickType.PICKUP, player);
            mc.playerController.windowClick(windowId, CHEST_SLOT, 0, ClickType.PICKUP, player);
        }
        return true;
    }

    private static int findUsableElytra(EntityPlayerSP player) {
        for (int slot = 0; slot < player.inventory.mainInventory.size(); ++slot) {
            ItemStack stack = player.inventory.mainInventory.get(slot);
            if (isUsableElytra(stack)) {
                return slot;
            }
        }
        return -1;
    }

    private void resetTimers() {
        equipDelayTicks = 0;
        flightRequestCooldown = 0;
    }
}
