package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;

public class ArmorStatus extends Module {

    public ArmorStatus() {
        super("ArmorStatus", Category.Render);
        this.description = "Shows armor durability info on screen.";
    }
}
