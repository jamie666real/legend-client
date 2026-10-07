package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.modules.Module;
import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.settings.Setting;
import net.minecraft.client.Minecraft;

public class FullBright extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();
    private float previousGamma = 1.0F;
    public Setting.NumberSetting gamma = new Setting.NumberSetting("Gamma", 10, 1, 10, 0.5);


    public FullBright() {
        super("Fullbright", Category.Render);

        this.description = "Changes the brightness of the world.";

        settings.add(gamma);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc != null && mc.gameSettings != null) {
            previousGamma = mc.gameSettings.gammaSetting;
            applyGamma();
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc != null && mc.gameSettings != null) {
            mc.gameSettings.gammaSetting = previousGamma;
        }
    }

    @Override
    public void onUpdate() {
        applyGamma();
    }

    @Override
    public void onSettingChanged(com.isacofff.clientbase.settings.Setting<?> setting) {
        if (setting == gamma) {
            applyGamma();
        }
    }

    private void applyGamma() {
        if (mc != null && mc.gameSettings != null) {
            mc.gameSettings.gammaSetting = gamma.getValue().floatValue();
        }
    }
}
