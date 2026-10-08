package net.minecraft.client.gui;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.Client;
import com.isacofff.clientbase.modules.Module;
import com.isacofff.clientbase.modules.features.TuffClientModules;
import com.isacofff.clientbase.settings.Setting;
import com.isacofff.clientbase.settings.Setting.BooleanSetting;
import com.isacofff.clientbase.settings.Setting.ModeSetting;
import com.isacofff.clientbase.settings.Setting.NumberSetting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;
import net.lax1dude.eaglercraft.Mouse;

public class ClickGuiScreen extends GuiScreen {

    private int BACKDROP = 0xB8000000;
    private int WINDOW = 0xFF10131C;
    private int SIDEBAR = 0xFF151A26;
    private int CARD = 0xFF1B2230;
    private int CARD_SELECTED = 0xFF253247;
    private int OUTLINE = 0xFF293448;
    private int ACCENT = 0xFFFF8A00;
    private int BRAND_YELLOW = 0xFFFFD21F;
    private int TEXT = 0xFFF1F5F9;
    private int MUTED = 0xFF99A8BC;
    private int GREEN = 0xFF68E0A0;

    private Category category = Category.Movement;
    private final ArrayList<Category> visibleCategories = new ArrayList<>();
    private Module selectedModule;
    private Module draggingHudModule;
    private NumberSetting draggingSlider;
    private GuiTextField moduleSearchField;
    private int moduleScrollOffset;
    private int hudDragOffsetX;
    private int hudDragOffsetY;
    private boolean hudEditMode;
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
        for (Category item : Category.values()) {
            if (!Client.manager.getModulesByCategory(item).isEmpty()) {
                visibleCategories.add(item);
            }
        }
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
        contentY = panelY + 72;
        contentBottom = panelY + panelHeight - 14;

        int contentX = panelX + sidebarWidth + 14;
        int contentWidth = panelWidth - sidebarWidth - 28;
        listWidth = Math.min(218, Math.max(100, contentWidth * 2 / 5));
        listX = contentX;
        detailsX = listX + listWidth + 14;
        detailsWidth = Math.max(70, panelX + panelWidth - 14 - detailsX);
    }

    @Override
    public void initGui() {
        calculateLayout();
        moduleSearchField = new GuiTextField(0, fontRendererObj, listX + 5, panelY + 46,
                Math.max(1, listWidth - 12), 18);
        moduleSearchField.setMaxStringLength(64);
        moduleSearchField.setEnableBackgroundDrawing(false);
        moduleSearchField.setTextColor(TEXT);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        calculateLayout();
        applyTheme();
        drawRect(0, 0, width, height, BACKDROP);
        drawRect(panelX, panelY, panelX + panelWidth, panelY + panelHeight, WINDOW);
        drawRect(panelX, panelY, panelX + panelWidth, panelY + 42, SIDEBAR);
        drawRect(panelX, panelY + 41, panelX + panelWidth, panelY + 42, OUTLINE);
        drawRect(panelX, panelY + 42, panelX + sidebarWidth, panelY + panelHeight, SIDEBAR);
        drawRect(panelX + sidebarWidth, panelY + 42, panelX + sidebarWidth + 1,
                panelY + panelHeight, OUTLINE);

        fontRendererObj.drawStringWithShadow("LEGEND", panelX + 16, panelY + 10, ACCENT);
        fontRendererObj.drawStringWithShadow("CLIENT", panelX + 68, panelY + 10, BRAND_YELLOW);
        fontRendererObj.drawString("MODULES", panelX + 16, panelY + 29, MUTED);
        drawRect(panelX + panelWidth - 190, panelY + 8, panelX + panelWidth - 96, panelY + 34,
                hudEditMode ? CARD_SELECTED : CARD);
        fontRendererObj.drawStringWithShadow(hudEditMode ? "HUD EDIT: ON" : "MOVE HUD",
                panelX + panelWidth - 184, panelY + 17, hudEditMode ? BRAND_YELLOW : TEXT);
        fontRendererObj.drawStringWithShadow("RSHIFT", panelX + panelWidth - 88, panelY + 17, MUTED);

        drawCategorySidebar(mouseX, mouseY);
        drawModuleList(mouseX, mouseY);
        drawModuleDetails();
        if (hudEditMode) {
            drawHudEditor(mouseX, mouseY);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void applyTheme() {
        if (TuffClientModules.isEnabled("Teto Mode")) {
            BACKDROP = 0xB8100915;
            WINDOW = 0xFF18121F;
            SIDEBAR = 0xFF211728;
            CARD = 0xFF2B1D2A;
            CARD_SELECTED = 0xFF42304A;
            OUTLINE = 0xFF654056;
            ACCENT = 0xFFFF5C9E;
            BRAND_YELLOW = 0xFF63E8E8;
            TEXT = 0xFFFFF2FA;
            MUTED = 0xFFC5AABD;
            GREEN = 0xFF71E6BC;
        } else {
            BACKDROP = 0xB8000000;
            WINDOW = 0xFF10131C;
            SIDEBAR = 0xFF151A26;
            CARD = 0xFF1B2230;
            CARD_SELECTED = 0xFF253247;
            OUTLINE = 0xFF293448;
            ACCENT = 0xFFFF8A00;
            BRAND_YELLOW = 0xFFFFD21F;
            TEXT = 0xFFF1F5F9;
            MUTED = 0xFF99A8BC;
            GREEN = 0xFF68E0A0;
        }
    }

    private void drawCategorySidebar(int mouseX, int mouseY) {
        int y = panelY + 56;
        for (Category item : visibleCategories) {
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
        drawRect(listX, panelY + 44, listX + listWidth - 1, panelY + 65,
                moduleSearchField.isFocused() ? CARD_SELECTED : CARD);
        drawRect(listX, panelY + 44, listX + listWidth - 1, panelY + 45,
                moduleSearchField.isFocused() ? ACCENT : OUTLINE);
        moduleSearchField.drawTextBox();
        if (moduleSearchField.getText().isEmpty() && !moduleSearchField.isFocused()) {
            fontRendererObj.drawString("Search mods...", listX + 5, panelY + 50, MUTED);
        }

        ArrayList<Module> modules = getFilteredModules();
        int visibleCount = getVisibleModuleCount();
        clampModuleScroll(modules.size(), visibleCount);
        int end = Math.min(modules.size(), moduleScrollOffset + visibleCount);
        if (modules.isEmpty()) {
            fontRendererObj.drawString("No matching mods.", listX + 6, contentY + 8, MUTED);
        }
        for (int index = moduleScrollOffset; index < end; ++index) {
            int y = contentY + (index - moduleScrollOffset) * 40;
            if (y + 35 > contentBottom) {
                break;
            }
            Module module = modules.get(index);
            boolean selected = module == selectedModule;
            boolean hovered = isHovered(mouseX, mouseY, listX, y, listWidth, 35);
            drawRect(listX, y, listX + listWidth, y + 35,
                    selected ? CARD_SELECTED : hovered ? CARD : WINDOW);
            drawRect(listX, y, listX + 2, y + 35, module.isEnabled() ? GREEN : OUTLINE);
            fontRendererObj.drawString(module.getName(), listX + 10, y + 6,
                    selected ? TEXT : MUTED);

            int toggleX = listX + listWidth - 37;
            int toggleColor = !module.isAvailable() ? 0xFF252A33
                    : module.isEnabled() ? 0xFF28694F : 0xFF3A4352;
            drawRect(toggleX, y + 11, toggleX + 27, y + 24, toggleColor);
            if (module.isAvailable()) {
                int knobX = module.isEnabled() ? toggleX + 17 : toggleX + 3;
                drawRect(knobX, y + 13, knobX + 9, y + 22, TEXT);
            } else {
                fontRendererObj.drawString("!", toggleX + 10, y + 13, MUTED);
            }
        }

        if (modules.size() > visibleCount) {
            int trackX = listX + listWidth - 2;
            int trackHeight = Math.max(1, contentBottom - contentY);
            drawRect(trackX, contentY, trackX + 2, contentBottom, OUTLINE);
            int thumbHeight = Math.max(12, trackHeight * visibleCount / modules.size());
            int maxOffset = modules.size() - visibleCount;
            int thumbY = contentY + (trackHeight - thumbHeight) * moduleScrollOffset / maxOffset;
            drawRect(trackX, thumbY, trackX + 2, thumbY + thumbHeight, ACCENT);
        }
    }

    private ArrayList<Module> getFilteredModules() {
        String query = moduleSearchField == null ? "" : moduleSearchField.getText().trim().toLowerCase(Locale.ROOT);
        ArrayList<Module> modules = new ArrayList<>();
        for (Module module : Client.manager.getModules()) {
            if (query.isEmpty() && module.getCategory() != category) {
                continue;
            }
            if (!query.isEmpty()) {
                String searchable = module.getName() + " " + module.getCategory().name() + " "
                        + (module.getDescription() == null ? "" : module.getDescription());
                if (!searchable.toLowerCase(Locale.ROOT).contains(query)) {
                    continue;
                }
            }
            modules.add(module);
        }
        return modules;
    }

    private void updateSearchResults() {
        ArrayList<Module> modules = getFilteredModules();
        moduleScrollOffset = 0;
        if (!modules.contains(selectedModule)) {
            selectedModule = modules.isEmpty() ? null : modules.get(0);
        }
    }

    private void drawModuleDetails() {
        drawRect(detailsX - 8, panelY + 48, detailsX - 7, contentBottom, OUTLINE);
        if (selectedModule == null) {
            fontRendererObj.drawString("Select a module to view its settings.", detailsX, contentY, MUTED);
            return;
        }

        fontRendererObj.drawString(selectedModule.getName(), detailsX, panelY + 47, TEXT);
        String state = !selectedModule.isAvailable() ? "UNAVAILABLE"
                : selectedModule.isEnabled() ? "ENABLED" : "DISABLED";
        fontRendererObj.drawString(state, detailsX, panelY + 62,
                selectedModule.isAvailable() && selectedModule.isEnabled() ? GREEN : MUTED);

        int descriptionY = panelY + 80;
        drawWrappedDescription(selectedModule.getDescription(), detailsX, descriptionY, Math.max(30, detailsWidth));

        int settingsY = descriptionY + 28;
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

    private void drawWrappedDescription(String description, int x, int y, int width) {
        if (description == null || description.trim().isEmpty()) {
            return;
        }

        StringTokenizer tokenizer = new StringTokenizer(description, " \t\n\r", true);
        StringBuilder currentLine = new StringBuilder();
        int currentY = y;
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token == null || token.isEmpty()) {
                continue;
            }
            StringBuilder candidate = new StringBuilder(currentLine);
            if (candidate.length() > 0) {
                candidate.append(' ');
            }
            candidate.append(token.trim());
            if (fontRendererObj.getStringWidth(candidate.toString()) > width) {
                if (currentLine.length() > 0) {
                    fontRendererObj.drawString(currentLine.toString(), x, currentY, MUTED);
                    currentY += 10;
                    if (currentY > contentBottom - 10) {
                        return;
                    }
                    currentLine.setLength(0);
                    currentLine.append(token.trim());
                } else {
                    fontRendererObj.drawString(token.trim(), x, currentY, MUTED);
                    currentY += 10;
                    if (currentY > contentBottom - 10) {
                        return;
                    }
                }
            } else {
                currentLine = candidate;
            }
        }
        if (currentLine.length() > 0) {
            fontRendererObj.drawString(currentLine.toString(), x, currentY, MUTED);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        calculateLayout();
        if (mouseButton != 0) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
            return;
        }

        moduleSearchField.mouseClicked(mouseX, mouseY, mouseButton);
        if (isHovered(mouseX, mouseY, listX, panelY + 44, listWidth - 1, 21)) {
            return;
        }

        if (isHovered(mouseX, mouseY, panelX + panelWidth - 90, panelY, 90, 42)) {
            closeGui();
            return;
        }

        if (isHovered(mouseX, mouseY, panelX + panelWidth - 190, panelY + 8, 94, 26)) {
            hudEditMode = !hudEditMode;
            draggingHudModule = null;
            return;
        }

        if (hudEditMode) {
            Module hudModule = findHudModule(mouseX, mouseY);
            if (hudModule != null) {
                draggingHudModule = hudModule;
                hudDragOffsetX = mouseX - hudModule.getHudX();
                hudDragOffsetY = mouseY - hudModule.getHudY();
                return;
            }
        }

        int categoryY = panelY + 56;
        for (Category item : visibleCategories) {
            if (isHovered(mouseX, mouseY, panelX + 9, categoryY, sidebarWidth - 18, 27)) {
                category = item;
                moduleSearchField.setText("");
                moduleScrollOffset = 0;
                ArrayList<Module> modules = getFilteredModules();
                selectedModule = modules.isEmpty() ? null : modules.get(0);
                return;
            }
            categoryY += 32;
        }

        int moduleY = contentY;
        ArrayList<Module> modules = getFilteredModules();
        int end = Math.min(modules.size(), moduleScrollOffset + getVisibleModuleCount());
        for (int index = moduleScrollOffset; index < end; ++index) {
            Module module = modules.get(index);
            moduleY = contentY + (index - moduleScrollOffset) * 40;
            if (isHovered(mouseX, mouseY, listX, moduleY, listWidth, 35)) {
                selectedModule = module;
                int toggleX = listX + listWidth - 37;
                if (mouseX >= toggleX && module.isAvailable()) {
                    module.toggle();
                }
                return;
            }
        }

        if (selectedModule != null) {
            clickSetting(mouseX, mouseY);
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int wheel = Mouse.getEventDWheel();
        int mouseX = (int) (Mouse.getX() * (float) width / mc.displayWidth);
        int mouseY = height - (int) (Mouse.getY() * (float) height / mc.displayHeight) - 1;
        if (wheel != 0 && isHovered(mouseX, mouseY,
                listX, contentY, listWidth, contentBottom - contentY)) {
            ArrayList<Module> modules = getFilteredModules();
            int direction = wheel > 0 ? -1 : 1;
            moduleScrollOffset += direction;
            clampModuleScroll(modules.size(), getVisibleModuleCount());
        }
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
        if (draggingHudModule != null && clickedMouseButton == 0) {
            moveHudModule(draggingHudModule, mouseX - hudDragOffsetX, mouseY - hudDragOffsetY);
        }
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        draggingSlider = null;
        if (draggingHudModule != null) {
            Client.manager.save();
        }
        draggingHudModule = null;
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

    private int getVisibleModuleCount() {
        return Math.max(1, (contentBottom - contentY + 5) / 40);
    }

    private void drawHudEditor(int mouseX, int mouseY) {
        fontRendererObj.drawStringWithShadow("Drag enabled HUD items to reposition them.",
                panelX + 16, panelY + panelHeight - 12, BRAND_YELLOW);
        int count = 0;
        for (Module module : Client.manager.getModules()) {
            if (!module.isEnabled() || !module.isHudModule()) {
                continue;
            }
            String text = module.getHudDisplayText();
            if (text == null || text.isEmpty()) {
                text = module.getName();
            }
            int x = module.getHudX();
            int y = module.getHudY();
            int boxWidth = Math.max(36, fontRendererObj.getStringWidth(text) + 8);
            boolean hovered = isHovered(mouseX, mouseY, x - 3, y - 3, boxWidth, fontRendererObj.FONT_HEIGHT + 6);
            drawRect(x - 3, y - 3, x + boxWidth, y + fontRendererObj.FONT_HEIGHT + 3,
                    hovered || module == draggingHudModule ? CARD_SELECTED : WINDOW);
            drawRect(x - 3, y - 3, x - 1, y + fontRendererObj.FONT_HEIGHT + 3, ACCENT);
            fontRendererObj.drawStringWithShadow(text, (float) x, (float) y, TEXT);
            ++count;
        }
        if (count == 0) {
            fontRendererObj.drawStringWithShadow("Enable a HUD module, then drag its preview here.",
                    panelX + 16, panelY + 58, MUTED);
        }
    }

    private Module findHudModule(int mouseX, int mouseY) {
        ArrayList<Module> modules = Client.manager.getModules();
        for (int i = modules.size() - 1; i >= 0; --i) {
            Module module = modules.get(i);
            if (!module.isEnabled() || !module.isHudModule()) {
                continue;
            }
            String text = module.getHudDisplayText();
            int boxWidth = Math.max(36, fontRendererObj.getStringWidth(text == null ? module.getName() : text) + 8);
            if (isHovered(mouseX, mouseY, module.getHudX() - 3, module.getHudY() - 3,
                    boxWidth, fontRendererObj.FONT_HEIGHT + 6)) {
                return module;
            }
        }
        return null;
    }

    private void moveHudModule(Module module, int x, int y) {
        int maxX = Math.max(0, width - fontRendererObj.getStringWidth(module.getHudDisplayText()));
        int maxY = Math.max(0, height - fontRendererObj.FONT_HEIGHT);
        module.setHudPosition(Math.min(x, maxX), Math.min(y, maxY));
    }

    private void clampModuleScroll(int moduleCount, int visibleCount) {
        moduleScrollOffset = Math.max(0, Math.min(moduleScrollOffset, moduleCount - visibleCount));
    }

    private void closeGui() {
        mc.displayGuiScreen(null);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1) {
            closeGui();
        } else if (moduleSearchField.textboxKeyTyped(typedChar, keyCode)) {
            updateSearchResults();
        }
    }

    @Override
    public void updateScreen() {
        moduleSearchField.updateCursorCounter();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
