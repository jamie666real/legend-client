package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;

public class NoHurtCam extends Module {

    public NoHurtCam() {
        super("NoHurtCam", Category.Combat);
        this.description = "Removes the camera shake when taking damage.";
    }
}
