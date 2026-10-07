package com.isacofff.clientbase.modules;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.features.ArmorStatus;
import com.isacofff.clientbase.modules.features.AutoJump;
import com.isacofff.clientbase.modules.features.AutoSprint;
import com.isacofff.clientbase.modules.features.ClickGui;
import com.isacofff.clientbase.modules.features.FPSBoost;
import com.isacofff.clientbase.modules.features.FullBright;
import com.isacofff.clientbase.modules.features.NoSlow;
import com.isacofff.clientbase.modules.features.ShowCoordinates;
import com.isacofff.clientbase.modules.features.TimeChanger;

import java.util.ArrayList;

public class Manager {

    public final ArrayList<Module> modules = new ArrayList<>();

    public void init() {
        modules.add(new ClickGui());
        modules.add(new FullBright());
        modules.add(new AutoSprint());
        modules.add(new AutoJump());
        modules.add(new NoSlow());
        modules.add(new ShowCoordinates());
        modules.add(new ArmorStatus());
        modules.add(new TimeChanger());
        modules.add(new FPSBoost());
        ModulePersistence.restore(this);
    }

    public void onTick() {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onUpdate();
            }
        }
    }

    public void onRender() {
        for (Module module : modules) {
            if (module.isEnabled()) {
                module.onRender();
            }
        }
    }

    public ArrayList<Module> getModules() {
        return modules;
    }

    public <T extends Module> T getModule(Class<T> classs) {
        for (Module module : modules) {
            if (classs.isInstance(module)) {
                return classs.cast(module);
            }
        }
        return null;
    }

    public Module getModuleByName(String name) {
        for (Module module : modules) {
            if (module.getName().equalsIgnoreCase(name)) {
                return module;
            }
        }
        return null;
    }

    public boolean isModuleEnabled(Class<? extends Module> moduleClass) {
        Module module = getModule(moduleClass);
        return module != null && module.isEnabled();
    }

    public ArrayList<Module> getModulesByCategory(Category category) {
        ArrayList<Module> result = new ArrayList<>();
        for (Module module : modules) {
            if (module.getCategory() == category) {
                result.add(module);
            }
        }
        return result;
    }
}
