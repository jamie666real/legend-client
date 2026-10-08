package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;

public abstract class ViaPluginModule extends Module {

    protected ViaPluginModule(String name, Category category) {
        super(name, category);
    }

    @Override
    public boolean isAvailable() {
        return false;
    }
}
