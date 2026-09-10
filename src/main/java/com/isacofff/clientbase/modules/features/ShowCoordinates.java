package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Module;

public class ShowCoordinates extends Module {

    public ShowCoordinates() {
        super("Coordinates", Category.Render);
        this.description = "Shows your current position in the HUD.";
    }
}
