package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.settings.Setting;

public class ViaEntities extends ViaPluginModule {

    public ViaEntities() {
        super("ViaEntities", Category.Client);
        description = "Unavailable: this 1.12 client has no extended-entity data handler.";
        settings.add(new Setting.ModeSetting("Target", "1.21.11", "1.13", "1.14", "1.15", "1.16", "1.17", "1.18", "1.19", "1.20", "1.21.11"));
    }
}