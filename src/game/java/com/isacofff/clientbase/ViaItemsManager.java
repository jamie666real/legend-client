package com.isacofff.clientbase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import com.isacofff.clientbase.modules.features.ViaItems;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

/** Resolves server-provided ViaItems identifiers to client-baked item models. */
public final class ViaItemsManager {
    private static final ResourceLocation ITEM_LIST = new ResourceLocation("minecraft", "via_items.txt");
    private static final Pattern FORMATTING_CODE = Pattern.compile("(?i)§[0-9A-FK-OR]");

    public static final List<String> ITEM_IDS = new ArrayList<String>();
    public static final Map<String, IBakedModel> BAKED_MODELS = new HashMap<String, IBakedModel>();

    private ViaItemsManager() {
    }

    /** Reload the ViaItems identifier list from the active resource pack. */
    public static void loadItemList() {
        ITEM_IDS.clear();
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft == null || minecraft.getResourceManager() == null) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                minecraft.getResourceManager().getResource(ITEM_LIST).getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String id = line.trim();
                if (!id.isEmpty() && !id.startsWith("#") && !ITEM_IDS.contains(id)) {
                    ITEM_IDS.add(id);
                }
            }
        } catch (IOException ignored) {
            // A resource pack may intentionally omit ViaItems support.
        }
    }

    public static boolean isEnabled() {
        return com.isacofff.clientbase.Client.manager != null
                && com.isacofff.clientbase.Client.manager.isModuleEnabled(ViaItems.class);
    }

    /** Return a matching ViaItems baked model, or null to use normal rendering. */
    public static IBakedModel getViaModel(ItemStack stack) {
        if (!isEnabled() || stack == null || stack.func_190926_b()) {
            return null;
        }

        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            return null;
        }

        for (String key : tag.getKeySet()) {
            if (!key.startsWith("VV|Protocol")) {
                continue;
            }
            NBTBase value = tag.getTag(key);
            String contents = value == null ? "" : value.toString();
            for (String id : ITEM_IDS) {
                if (contents.contains("minecraft:" + id) || contents.contains("\"" + id + "\"")) {
                    IBakedModel model = BAKED_MODELS.get(id);
                    if (model != null) {
                        return model;
                    }
                }
            }
        }

        String displayName = stack.getDisplayName();
        if (displayName != null) {
            String id = FORMATTING_CODE.matcher(displayName).replaceAll("")
                    .toLowerCase(Locale.ROOT).replace(' ', '_');
            return BAKED_MODELS.get(id);
        }
        return null;
    }
}
