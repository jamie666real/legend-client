package net.minecraft.client.gui;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.Client;
import com.isacofff.clientbase.modules.Module;
import com.isacofff.clientbase.modules.features.ClickGui;
import com.isacofff.clientbase.settings.Setting;
import com.isacofff.clientbase.settings.Setting.BooleanSetting;
import com.isacofff.clientbase.settings.Setting.ModeSetting;
import com.isacofff.clientbase.settings.Setting.NumberSetting;

import java.io.IOException;
import java.util.ArrayList;

public class ClickGuiScreen extends GuiScreen {

    private static final int BACKDROP = 0xB8000000;
    private static final int WINDOW = 0xFF10131C;
    private static final int SIDEBAR = 0xFF151A26;
    private static final int CARD = 0xFF1B2230;
    private static final int CARD_SELECTED = 0xFF253247;
    private static final int OUTLINE = 0xFF293448;
    private static final int ACCENT = 0xFF56C8F5;
    private static final int TEXT = 0xFFF1F5F9;
    private static final int MUTED = 0xFF99A8BC;
    private static final int GREEN = 0xFF68E0A0;

    private Category category = Category.Movement;
    private Module selectedModule;
    private NumberSetting draggingSlider;
    private int panelX;
    private int panelY;
    private int panelWidth;
    private int panelHeight;
    private int sidebarWidth;
    private int listX;
    private int listWidth;
    private int detailsX;
    private int detailsWidth;
    private int contentY;
    private int contentBottom;

    public ClickGuiScreen() {
        ArrayList<Module> modules = Client.manager.getModulesByCategory(category);
        if (!modules.isEmpty()) {
            selectedModule = modules.get(0);
        }
    }

    private void calculateLayout() {
        panelWidth = Math.min(760, Math.max(300, width - 24));
        panelHeight = Math.min(430, Math.max(220, height - 24));
        panelX = (width - panelWidth) / 2;
        panelY = (height - panelHeight) / 2;
        sidebarWidth = 118;
        contentY = panelY + 58;
        contentBottom = panelY + panelHeight - 14;

        int contentX = panelX + sidebarWidth + 14;
        int contentWidth = panelWidth - sidebarWidth - 28;
        listWidth = Math.min(218, Math.max(100, contentWidth * 2 / 5));
        listX = contentX;
        detailsX = listX + listWidth + 14;
        detailsWidth = Math.max(70, panelX + panelWidth - 14 - detailsX);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        calculateLayout();
        drawRect(0, 0, width, height, BACKDROP);
        drawRect(panelX, panelY, panelX + panelWidth, panelY + panelHeight, WINDOW);
        drawRect(panelX, panelY, panelX + panelWidth, panelY + 42, SIDEBAR);
        drawRect(panelX, panelY + 41, panelX + panelWidth, panelY + 42, OUTLINE);
        drawRect(panelX, panelY + 42, panelX + sidebarWidth, panelY + panelHeight, SIDEBAR);
        drawRect(panelX + sidebarWidth, panelY + 42, panelX + sidebarWidth + 1,
                panelY + panelHeight, OUTLINE);

        fontRendererObj.drawStringWithShadow("LEGEND", panelX + 16, panelY + 10, ACCENT);
        fontRendererObj.drawStringWithShadow("CLIENT", panelX + 68, panelY + 10, TEXT);
        fontRendererObj.drawString("MODULES", panelX + 16, panelY + 29, MUTED);
        fontRendererObj.drawStringWithShadow("Right Shift", panelX + panelWidth - 82, panelY + 16, MUTED);

        drawCategorySidebar(mouseX, mouseY);
        drawModuleList(mouseX, mouseY);
        drawModuleDetails();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void drawCategorySidebar(int mouseX, int mouseY) {
        int y = panelY + 56;
        for (Category item : Category.values()) {
            boolean active = category == item;
            boolean hovered = isHovered(mouseX, mouseY, panelX + 9, y, sidebarWidth - 18, 27);
            if (active) {
                drawRect(panelX + 9, y, panelX + sidebarWidth - 9, y + 27, CARD_SELECTED);
                drawRect(panelX + 9, y, panelX + 12, y + 27, ACCENT);
            } else if (hovered) {
                drawRect(panelX + 9, y, panelX + sidebarWidth - 9, y + 27, CARD);
            }
            fontRendererObj.drawString(item.name(), panelX + 20, y + 9, active ? TEXT : MUTED);
            y += 32;
        }
    }

    private void drawModuleList(int mouseX, int mouseY) {
        fontRendererObj.drawString("YOUR MODULES", listX, panelY + 47, MUTED);
        int y = contentY;
        for (Module module : Client.manager.getModulesByCategory(category)) {
            if (y + 35 > contentBottom) {
                break;
            }
            boolean selected = module == selectedModule;
            boolean hovered = isHovered(mouseX, mouseY, listX, y, listWidth, 35);
            drawRect(listX, y, listX + listWidth, y + 35,
                    selected ? CARD_SELECTED : hovered ? CARD : WINDOW);
            drawRect(listX, y, listX + 2, y + 35, module.isEnabled() ? GREEN : OUTLINE);
            fontRendererObj.drawString(module.getName(), listX + 10, y + 6,
                    selected ? TEXT : MUTED);

            int toggleX = listX + listWidth - 37;
            int toggleColor = module.isEnabled() ? 0xFF28694F : 0xFF3A4352;
            drawRect(toggleX, y + 11, toggleX + 27, y + 24, toggleColor);
            int knobX = module.isEnabled() ? toggleX + 17 : toggleX + 3;
            drawRect(knobX, y + 13, knobX + 9, y + 22, TEXT);
            y += 40;
        }
    }

    private void drawModuleDetails() {
        drawRect(detailsX - 8, panelY + 48, detailsX - 7, contentBottom, OUTLINE);
        if (selectedModule == null) {
            fontRendererObj.drawString("Select a module to view its settings.", detailsX, contentY, MUTED);
            return;
        }

        fontRendererObj.drawString(selectedModule.getName(), detailsX, panelY + 47, TEXT);
        String state = selectedModule.isEnabled() ? "ENABLED" : "DISABLED";
        fontRendererObj.drawString(state, detailsX, panelY + 62,
                selectedModule.isEnabled() ? GREEN : MUTED);

        String description = fontRendererObj.trimStringToWidth(selectedModule.getDescription(),
                Math.max(30, detailsWidth));
        fontRendererObj.drawString(description, detailsX, panelY + 80, MUTED);

        int settingsY = panelY + 108;
        fontRendererObj.drawString("SETTINGS", detailsX, settingsY, MUTED);
        settingsY += 17;
        for (Setting<?> setting : selectedModule.getSettings()) {
            if (settingsY + 36 > contentBottom) {
                break;
            }
            if (setting instanceof BooleanSetting) {
                BooleanSetting booleanSetting = (BooleanSetting) setting;
                fontRendererObj.drawString(booleanSetting.getName(), detailsX, settingsY + 7, TEXT);
                String value = booleanSetting.getValue() ? "ON" : "OFF";
                fontRendererObj.drawString(value, detailsX + detailsWidth
                        - fontRendererObj.getStringWidth(value), settingsY + 7,
                        booleanSetting.getValue() ? GREEN : MUTED);
            } else if (setting instanceof ModeSetting) {
                ModeSetting modeSetting = (ModeSetting) setting;
                fontRendererObj.drawString(modeSetting.getName(), detailsX, settingsY + 2, MUTED);
                drawRect(detailsX, settingsY + 14, detailsX + detailsWidth, settingsY + 33, CARD);
                String value = modeSetting.getValue() + "  >";
                fontRendererObj.drawString(value, detailsX + 7, settingsY + 20, TEXT);
            } else if (setting instanceof NumberSetting) {
                NumberSetting numberSetting = (NumberSetting) setting;
                String label = numberSetting.getName();
                String value = formatNumber(numberSetting.getValue());
                fontRendererObj.drawString(label, detailsX, settingsY + 2, MUTED);
                fontRendererObj.drawString(value,
                        detailsX + detailsWidth - fontRendererObj.getStringWidth(value),
                        settingsY + 2, TEXT);
                int barY = settingsY + 22;
                drawRect(detailsX, barY, detailsX + detailsWidth, barY + 4, CARD);
                int fill = getSliderFill(numberSetting, detailsWidth);
                drawRect(detailsX, barY, detailsX + fill, barY + 4, ACCENT);
                drawRect(detailsX + fill - 2, barY - 2, detailsX + fill + 2, barY + 6, TEXT);
            }
            settingsY += 39;
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        calculateLayout();
        if (mouseButton != 0) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
            return;
        }

        if (isHovered(mouseX, mouseY, panelX + panelWidth - 90, panelY, 90, 42)) {
            closeGui();
            return;
        }

        int categoryY = panelY + 56;
        for (Category item : Category.values()) {
            if (isHovered(mouseX, mouseY, panelX + 9, categoryY, sidebarWidth - 18, 27)) {
                category = item;
                ArrayList<Module> modules = Client.manager.getModulesByCategory(category);
                selectedModule = modules.isEmpty() ? null : modules.get(0);
                return;
            }
            categoryY += 32;
        }

        int moduleY = contentY;
        for (Module module : Client.manager.getModulesByCategory(category)) {
            if (moduleY + 35 > contentBottom) {
                break;
            }
            if (isHovered(mouseX, mouseY, listX, moduleY, listWidth, 35)) {
                selectedModule = module;
                int toggleX = listX + listWidth - 37;
                if (mouseX >= toggleX) {
                    module.toggle();
                }
                return;
            }
            moduleY += 40;
        }

        if (selectedModule != null) {
            clickSetting(mouseX, mouseY);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    private void clickSetting(int mouseX, int mouseY) {
        int settingsY = panelY + 125;
        for (Setting<?> setting : selectedModule.getSettings()) {
            if (settingsY + 36 > contentBottom) {
                break;
            }
            if (isHovered(mouseX, mouseY, detailsX, settingsY, detailsWidth, 36)) {
                if (setting instanceof BooleanSetting) {
                    ((BooleanSetting) setting).toggle();
                    notifySettingChanged(setting);
                } else if (setting instanceof ModeSetting) {
                    ((ModeSetting) setting).cycle();
                    notifySettingChanged(setting);
                } else if (setting instanceof NumberSetting) {
                    draggingSlider = (NumberSetting) setting;
                    updateSlider(mouseX);
                }
                return;
            }
            settingsY += 39;
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (draggingSlider != null && clickedMouseButton == 0) {
            updateSlider(mouseX);
        }
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        draggingSlider = null;
        super.mouseReleased(mouseX, mouseY, state);
    }

    private void updateSlider(int mouseX) {
        double min = draggingSlider.getMin();
        double max = draggingSlider.getMax();
        double percent = Math.max(0.0, Math.min(1.0, (mouseX - detailsX) / (double) Math.max(1, detailsWidth)));
        double value = min + percent * (max - min);
        double increment = draggingSlider.getIncrement();
        if (increment > 0.0) {
            value = min + Math.round((value - min) / increment) * increment;
            value = Math.round(value * 10000.0) / 10000.0;
        }
        value = Math.max(min, Math.min(max, value));
        draggingSlider.setValue(value);
        notifySettingChanged(draggingSlider);
    }

    private int getSliderFill(NumberSetting setting, int width) {
        double range = setting.getMax() - setting.getMin();
        if (range <= 0.0) {
            return 0;
        }
        double value = (setting.getValue() - setting.getMin()) / range;
        return (int) (Math.max(0.0, Math.min(1.0, value)) * width);
    }

    private String formatNumber(double value) {
        return value == Math.rint(value) ? Integer.toString((int) value) : Double.toString(value);
    }

    private void notifySettingChanged(Setting<?> setting) {
        selectedModule.onSettingChanged(setting);
    }

    private boolean isHovered(int mouseX, int mouseY, int x, int y, int boxWidth, int boxHeight) {
        return mouseX >= x && mouseX <= x + boxWidth && mouseY >= y && mouseY <= y + boxHeight;
    }

    private void closeGui() {
        ClickGui clickGui = Client.manager.getModule(ClickGui.class);
        if (clickGui != null && clickGui.isEnabled()) {
            clickGui.toggle();
        } else {
            mc.displayGuiScreen(null);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) {
            closeGui();
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
