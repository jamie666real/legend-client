package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class PotionStatus extends Module {

    private final Minecraft mc = Minecraft.getMinecraft();

    public PotionStatus() {
        super("PotionStatus", Category.Render);
        this.description = "Shows active potion effects and their remaining duration.";
        setHudPosition(6, 60);
    }

    @Override
    public boolean isHudModule() {
        return true;
    }

    @Override
    public String getHudDisplayText() {
        return mc != null && mc.player != null && !mc.player.getActivePotionEffects().isEmpty()
                ? "Potion effects: " + mc.player.getActivePotionEffects().size() : "Potion effects";
    }

    @Override
    public void onRender() {
        if (mc == null || mc.player == null || mc.fontRendererObj == null) {
            return;
        }

        int y = getHudY();
        for (PotionEffect effect : mc.player.getActivePotionEffects()) {
            Potion potion = effect.getPotion();
            String name = I18n.format(potion.getName());
            int amplifier = effect.getAmplifier();
            if (amplifier > 0) {
                name += " " + (amplifier + 1);
            }
            String line = name + " " + Potion.getPotionDurationString(effect, 1.0F);
            int color = potion.isBeneficial() ? 0xFF80FF80 : 0xFFFF8080;
            mc.fontRendererObj.drawStringWithShadow(line, (float) getHudX(), (float) y, color);
            y += mc.fontRendererObj.FONT_HEIGHT + 2;
        }
    }
}
