package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;
import com.isacofff.clientbase.settings.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;

public class TimeChanger extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final Setting.ModeSetting time = new Setting.ModeSetting("Time", "Day", "Sunrise", "Day", "Sunset", "Night");
    private WorldClient capturedWorld;
    private long previousWorldTime;

    public TimeChanger() {
        super("TimeChanger", Category.Render);
        this.description = "Changes the local world's displayed time.";
        settings.add(time);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        captureWorldTime();
        applyTime();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc != null && mc.world != null && mc.world == capturedWorld) {
            mc.world.setWorldTime(previousWorldTime);
        }
        capturedWorld = null;
    }

    @Override
    public void onUpdate() {
        if (mc != null && mc.world != capturedWorld) {
            captureWorldTime();
        }
        applyTime();
    }

    @Override
    public void onSettingChanged(Setting<?> setting) {
        if (setting == time) {
            applyTime();
        }
    }

    private void captureWorldTime() {
        if (mc != null && mc.world != null) {
            capturedWorld = mc.world;
            previousWorldTime = mc.world.getWorldTime();
        }
    }

    private void applyTime() {
        if (mc == null || mc.world == null) {
            return;
        }
        String selected = time.getValue();
        long worldTime = "Sunrise".equals(selected) ? 0L
                : "Day".equals(selected) ? 6000L
                : "Sunset".equals(selected) ? 12000L : 18000L;
        mc.world.setWorldTime(worldTime);
    }
}
