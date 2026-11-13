package com.isacofff.clientbase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.ResourceLocation;

/** Resolves server-provided ViaItems identifiers to client-baked item models. */
public final class ViaItemsManager {
    private static final ResourceLocation ITEM_LIST = new ResourceLocation("minecraft", "via_items.txt");
    private static final Pattern FORMATTING_CODE = Pattern.compile("(?i)§[0-9A-FK-OR]");

    public static final List<String> ITEM_IDS = new ArrayList<String>();
    public static final Map<String, IBakedModel> BAKED_MODELS = new HashMap<String, IBakedModel>();
    private static boolean enabled;

    private ViaItemsManager() {
    }

    public static void setEnabled(boolean value) {
        enabled = value;
    }

    public static boolean isEnabled() {
        return enabled;
    }

    /** Reads the item identifiers from the active resource pack. */
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
                String itemId = line.trim();
                if (!itemId.isEmpty() && !itemId.startsWith("#") && !ITEM_IDS.contains(itemId)) {
                    ITEM_IDS.add(itemId);
                }
            }
        } catch (IOException ignored) {
            // Missing lists are allowed, for example when running with a custom resource pack.
        }
    }

    /**
     * Returns the model for a ViaItems stack, or null when the stack has no
     * recognized server identifier.
     */
    public static IBakedModel getViaModel(ItemStack stack) {
        if (!enabled || stack == null || stack.func_190926_b()) {
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
            String serializedValue = value == null ? "" : value.toString();
            for (String itemId : ITEM_IDS) {
                if (serializedValue.contains("minecraft:" + itemId)
                        || serializedValue.contains("\"" + itemId + "\"")
                        || key.contains("minecraft:" + itemId)
                        || key.contains("\"" + itemId + "\"")) {
                    IBakedModel model = BAKED_MODELS.get(itemId);
                    if (model != null) {
                        return model;
                    }
                }
            }
        }

        String displayName = stack.getDisplayName();
        if (displayName != null) {
            String normalizedName = FORMATTING_CODE.matcher(displayName).replaceAll("")
                    .toLowerCase(java.util.Locale.ROOT).replace(' ', '_');
            return BAKED_MODELS.get(normalizedName);
        }
        return null;
    }

    /** Store a baked model using its namespace-free ViaItems identifier. */
    public static void putBakedModel(String itemId, IBakedModel model) {
        if (itemId != null && model != null) {
            BAKED_MODELS.put(itemId, model);
        }
    }

    /** Clears stale model references before a new model-registry bake. */
    public static void clearBakedModels() {
        BAKED_MODELS.clear();
    }
}
