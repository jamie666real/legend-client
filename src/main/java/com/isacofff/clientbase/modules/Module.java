package com.isacofff.clientbase.modules;

import com.isacofff.clientbase.Client;
import com.isacofff.clientbase.settings.Setting;
import com.isacofff.clientbase.Category;

import java.util.ArrayList;

public abstract class Module {

    private String name;
    private Category category;
    private boolean enabled;
    private int hudX = 6;
    private int hudY = 6;

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

    public boolean isAvailable() { return true; }

    public boolean isHudModule() { return false; }

    public int getHudX() { return hudX; }

    public int getHudY() { return hudY; }

    public void setHudPosition(int x, int y) {
        hudX = Math.max(0, x);
        hudY = Math.max(0, y);
    }

    public String getHudDisplayText() { return getName(); }

    public void toggle() {
        if (!isAvailable() && !enabled) {
            return;
        }
        this.enabled = !this.enabled;
        if (this.enabled) onEnable();
        else onDisable();
        if (Client.manager != null) {
            ModulePersistence.save(Client.manager);
        }
    }

    void setEnabledWithoutSaving(boolean enabled) {
        if (enabled && !isAvailable()) {
            return;
        }
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
    public void onRender() {}
    public void onSettingChanged(Setting<?> setting) {}
    public boolean shouldPersist() { return true; }

}
