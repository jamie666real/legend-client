package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;
import com.isacofff.clientbase.settings.Setting;

public class ViaEntities extends Module {

    public ViaEntities() {
        super("ViaEntities", Category.Client);
        description = "Displays entity metadata available from supported 1.13 through 1.21.11 servers.";
        settings.add(new Setting.ModeSetting("Target", "1.21.11", "1.13", "1.14", "1.15", "1.16", "1.17", "1.18", "1.19", "1.20", "1.21.11"));
    }
}