package com.isacofff.clientbase.modules;

import java.nio.charset.StandardCharsets;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.Filesystem;
import net.lax1dude.eaglercraft.internal.IEaglerFilesystem;
import net.lax1dude.eaglercraft.internal.buffer.ByteBuffer;

final class ModulePersistence {

    private static final String DATABASE = "legend-client";
    private static final String PATH = "config/enabled-modules.txt";
    private static final String HUD_PATH = "config/hud-positions.txt";

    private ModulePersistence() {
    }

    static void restore(Manager manager) {
        IEaglerFilesystem filesystem = null;
        try {
            filesystem = Filesystem.getHandleFor(DATABASE);
            if (filesystem.eaglerExists(PATH)) {
                ByteBuffer data = filesystem.eaglerRead(PATH);
                byte[] bytes = new byte[data.remaining()];
                data.get(bytes);
                String[] names = new String(bytes, StandardCharsets.UTF_8).split("\\n");
                for (String name : names) {
                    Module module = manager.getModuleByName(name.trim());
                    if (module != null && module.shouldPersist()
                            && !"ClickGUI".equalsIgnoreCase(module.getName())) {
                        module.setEnabledWithoutSaving(true);
                    }
                }
            }

            if (filesystem.eaglerExists(HUD_PATH)) {
                ByteBuffer data = filesystem.eaglerRead(HUD_PATH);
                byte[] bytes = new byte[data.remaining()];
                data.get(bytes);
                String[] positions = new String(bytes, StandardCharsets.UTF_8).split("\\n");
                for (String position : positions) {
                    int lastSeparator = position.lastIndexOf(',');
                    int secondLastSeparator = lastSeparator < 0 ? -1 : position.lastIndexOf(',', lastSeparator - 1);
                    if (secondLastSeparator <= 0 || lastSeparator <= secondLastSeparator) {
                        continue;
                    }
                    Module module = manager.getModuleByName(position.substring(0, secondLastSeparator));
                    if (module != null && module.isHudModule()) {
                        try {
                            int x = Integer.parseInt(position.substring(secondLastSeparator + 1, lastSeparator));
                            int y = Integer.parseInt(position.substring(lastSeparator + 1));
                            module.setHudPosition(x, y);
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }
        } catch (Throwable ignored) {
        } finally {
            if (filesystem != null) {
                filesystem.closeHandle();
            }
        }
    }

    static void save(Manager manager) {
        IEaglerFilesystem filesystem = null;
        try {
            StringBuilder enabled = new StringBuilder();
            for (Module module : manager.getModules()) {
                if (module.isEnabled() && module.shouldPersist()
                        && !"ClickGUI".equalsIgnoreCase(module.getName())) {
                    enabled.append(module.getName()).append('\n');
                }
            }

            byte[] bytes = enabled.toString().getBytes(StandardCharsets.UTF_8);
            ByteBuffer data = EagRuntime.allocateByteBuffer(bytes.length);
            data.put(bytes).flip();
            filesystem = Filesystem.getHandleFor(DATABASE);
            filesystem.eaglerWrite(PATH, data);

            StringBuilder positions = new StringBuilder();
            for (Module module : manager.getModules()) {
                if (module.isHudModule()) {
                    positions.append(module.getName()).append(',')
                            .append(module.getHudX()).append(',')
                            .append(module.getHudY()).append('\n');
                }
            }
            byte[] positionBytes = positions.toString().getBytes(StandardCharsets.UTF_8);
            ByteBuffer positionData = EagRuntime.allocateByteBuffer(positionBytes.length);
            positionData.put(positionBytes).flip();
            filesystem.eaglerWrite(HUD_PATH, positionData);
        } catch (Throwable ignored) {
        } finally {
            if (filesystem != null) {
                filesystem.closeHandle();
            }
        }
    }
}