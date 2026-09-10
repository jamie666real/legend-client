package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;

public class NoSlow extends Module {

    public NoSlow() {
        super("NoSlow", Category.Player);
        this.description = "Reduces slowdown while using items or blocking.";
    }
}
