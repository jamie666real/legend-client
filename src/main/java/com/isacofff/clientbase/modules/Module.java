package com.isacofff.clientbase.modules;

import com.isacofff.clientbase.Client;
import com.isacofff.clientbase.settings.Setting;
import com.isacofff.clientbase.Category;

import java.util.ArrayList;

public abstract class Module {

    private String name;
    private Category category;
    private boolean enabled;

    public boolean open = false;

    public ArrayList<Setting<?>> settings = new ArrayList<>();

    public Module(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    public String getName() {return this.name;
    }

    public Category getCategory() { return category; }

    public boolean isEnabled() { return enabled; }

    public void toggle() {
        this.enabled = !this.enabled;
        if (this.enabled) onEnable();
        else onDisable();
        if (Client.manager != null) {
            ModulePersistence.save(Client.manager);
        }
    }

    void setEnabledWithoutSaving(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            onEnable();
        }
    }



    public ArrayList<Setting<?>> getSettings() {
        return settings;
    }

    public String description = "- - -";



    public String getDescription() {
        return this.description;
    }

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public void onEnable() {}
    public void onDisable() {}
    public void onUpdate() {}

}
