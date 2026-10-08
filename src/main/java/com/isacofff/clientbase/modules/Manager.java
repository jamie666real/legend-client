package com.isacofff.clientbase.modules;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.features.ArmorStatus;
import com.isacofff.clientbase.modules.features.AutoJump;
import com.isacofff.clientbase.modules.features.AutoSprint;
import com.isacofff.clientbase.modules.features.BiomeHud;
import com.isacofff.clientbase.modules.features.ClickGui;
import com.isacofff.clientbase.modules.features.DirectionHud;
import com.isacofff.clientbase.modules.features.FPSBoost;
import com.isacofff.clientbase.modules.features.FPSCounter;
import com.isacofff.clientbase.modules.features.FullBright;
import com.isacofff.clientbase.modules.features.Keystrokes;
import com.isacofff.clientbase.modules.features.NoHurtCam;
import com.isacofff.clientbase.modules.features.NoSlow;
import com.isacofff.clientbase.modules.features.PotionStatus;
import com.isacofff.clientbase.modules.features.ShowCoordinates;
import com.isacofff.clientbase.modules.features.Speedometer;
import com.isacofff.clientbase.modules.features.TuffClientModules;
import com.isacofff.clientbase.modules.features.TimeChanger;
import com.isacofff.clientbase.modules.features.ViaEntities;
import com.isacofff.clientbase.modules.features.ViaItems;
import com.isacofff.clientbase.modules.features.XYZ;

import java.util.ArrayList;

public class Manager {

    public final ArrayList<Module> modules = new ArrayList<>();

    public void init() {
        modules.add(new ClickGui());
        modules.add(new NoHurtCam());
        modules.add(new NoSlow());
        modules.add(new AutoSprint());
        modules.add(new AutoJump());
        modules.add(new TimeChanger());
        modules.add(new FullBright());
        modules.add(new FPSBoost());
        modules.add(new FPSCounter());
        modules.add(new DirectionHud());
        modules.add(new BiomeHud());
        modules.add(new ShowCoordinates());
        modules.add(new Speedometer());
        modules.add(new ArmorStatus());
        modules.add(new PotionStatus());
        modules.add(new Keystrokes());
        modules.add(new ViaEntities());
        modules.add(new ViaItems());
        modules.add(new XYZ());
        TuffClientModules.register(this);
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

    public void save() {
        ModulePersistence.save(this);
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
