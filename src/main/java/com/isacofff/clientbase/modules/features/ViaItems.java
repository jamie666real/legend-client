package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.ViaItemsManager;
import com.isacofff.clientbase.settings.Setting;

/** Enables client-side rendering of models for items identified by ViaItems NBT. */
public class ViaItems extends ViaPluginModule {

    public ViaItems() {
        super("ViaItems", Category.Client);
        description = "Displays custom item models when a server supplies ViaItems identifiers.";
        settings.add(new Setting.ModeSetting("Target", "1.21.11", "1.13", "1.14", "1.15", "1.16", "1.17", "1.18", "1.19", "1.20", "1.21.11"));
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void onEnable() {
        ViaItemsManager.loadItemList();
    }

    @Override
    public void onDisable() {
        // Module state controls model selection; baked models remain available for the next enable.
    }
}
