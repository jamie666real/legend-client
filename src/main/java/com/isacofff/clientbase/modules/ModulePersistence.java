package com.isacofff.clientbase.modules;

import java.nio.charset.StandardCharsets;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.Filesystem;
import net.lax1dude.eaglercraft.internal.IEaglerFilesystem;
import net.lax1dude.eaglercraft.internal.buffer.ByteBuffer;

final class ModulePersistence {

    private static final String DATABASE = "legend-client";
    private static final String PATH = "config/enabled-modules.txt";

    private ModulePersistence() {
    }

    static void restore(Manager manager) {
        IEaglerFilesystem filesystem = null;
        try {
            filesystem = Filesystem.getHandleFor(DATABASE);
            if (!filesystem.eaglerExists(PATH)) {
                return;
            }

            ByteBuffer data = filesystem.eaglerRead(PATH);
            byte[] bytes = new byte[data.remaining()];
            data.get(bytes);
            String[] names = new String(bytes, StandardCharsets.UTF_8).split("\\n");
            for (String name : names) {
                Module module = manager.getModuleByName(name.trim());
                if (module != null && !"ClickGUI".equalsIgnoreCase(module.getName())) {
                    module.setEnabledWithoutSaving(true);
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
                if (module.isEnabled() && !"ClickGUI".equalsIgnoreCase(module.getName())) {
                    enabled.append(module.getName()).append('\n');
                }
            }

            byte[] bytes = enabled.toString().getBytes(StandardCharsets.UTF_8);
            ByteBuffer data = EagRuntime.allocateByteBuffer(bytes.length);
            data.put(bytes).flip();
            filesystem = Filesystem.getHandleFor(DATABASE);
            filesystem.eaglerWrite(PATH, data);
        } catch (Throwable ignored) {
        } finally {
            if (filesystem != null) {
                filesystem.closeHandle();
            }
        }
    }
}