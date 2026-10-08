package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

public class Keystrokes extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();

    public Keystrokes() {
        super("Keystrokes", Category.Render);
        this.description = "Displays movement, jump, and sneak key states.";
    }

    @Override
    public void onRender() {
        if (mc == null || mc.gameSettings == null || mc.scaledResolution == null) {
            return;
        }

        ScaledResolution resolution = mc.scaledResolution;
        int left = 8;
        int top = resolution.getScaledHeight() - 88;
        int keySize = 20;
        int gap = 2;
        int middle = left + keySize + gap;
        int right = middle + keySize + gap;

        drawKey(mc.gameSettings.keyBindForward, middle, top, keySize, keySize);
        drawKey(mc.gameSettings.keyBindLeft, left, top + keySize + gap, keySize, keySize);
        drawKey(mc.gameSettings.keyBindBack, middle, top + keySize + gap, keySize, keySize);
        drawKey(mc.gameSettings.keyBindRight, right, top + keySize + gap, keySize, keySize);
        drawKey(mc.gameSettings.keyBindJump, left, top + (keySize + gap) * 2, keySize * 3 + gap * 2, 14);
        drawKey(mc.gameSettings.keyBindSneak, left, top + (keySize + gap) * 2 + 16, keySize * 3 + gap * 2, 14);
    }

    private void drawKey(KeyBinding binding, int x, int y, int width, int height) {
        boolean pressed = GameSettings.isKeyDown(binding);
        Gui.drawRect(x, y, x + width, y + height, pressed ? 0xDDA45B00 : 0xAA101010);
        String label = GameSettings.getKeyDisplayString(binding.getKeyCode());
        int textX = x + (width - mc.fontRendererObj.getStringWidth(label)) / 2;
        int textY = y + (height - mc.fontRendererObj.FONT_HEIGHT) / 2;
        mc.fontRendererObj.drawStringWithShadow(label, textX, textY, 0xFFFFFFFF);
    }
}
