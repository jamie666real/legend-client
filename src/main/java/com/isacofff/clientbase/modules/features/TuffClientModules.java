package com.isacofff.clientbase.modules.features;

import com.isacofff.clientbase.Category;
import com.isacofff.clientbase.modules.Manager;
import com.isacofff.clientbase.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.init.Items;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.renderer.entity.RenderManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

public final class TuffClientModules {
    private static final Map<String, Module> MODULES = new HashMap<>();
    private static final long[] LEFT_CLICKS = new long[32];
    private static final long[] RIGHT_CLICKS = new long[32];
    private static int leftClickIndex;
    private static int rightClickIndex;
    private static Entity lastAttackedEntity;
    private static long lastAttackTime;
    private static int attackCount;
    private static int comboAttempts;
    private static int nextHudRow;
    private static long lastAutoGgTime;

    private TuffClientModules() {
    }

    public static void recordIncomingChat(String message) {
        if (!isEnabled("Auto GG") || message == null) {
            return;
        }
        String lower = message.toLowerCase(java.util.Locale.ROOT);
        if (!(lower.contains("won the game") || lower.contains("wins the game")
                || lower.contains("game has ended") || lower.contains("victory"))) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        long now = System.currentTimeMillis();
        if (mc != null && mc.player != null && now - lastAutoGgTime >= 30000L) {
            lastAutoGgTime = now;
            mc.player.sendChatMessage("gg");
        }
    }

    private static void drawPotions(TuffModule module) {
        int y = module.getHudY();
        Collection<PotionEffect> effects = module.MC.player.getActivePotionEffects();
        if (effects.isEmpty()) {
            module.MC.fontRendererObj.drawStringWithShadow("No active effects", module.getHudX(), y, 0xFFAAAAAA);
            return;
        }
        for (PotionEffect effect : effects) {
            Potion potion = effect.getPotion();
            String text = potion.getName() + " " + (effect.getAmplifier() + 1) + " "
                    + Potion.getPotionDurationString(effect, 1.0F);
            int color = potion.getLiquidColor() | 0xFF000000;
            module.MC.fontRendererObj.drawStringWithShadow(text, module.getHudX(), y, color);
            y += 10;
        }
    }

    private static void drawMinimap(TuffModule module) {
        int centerX = module.getHudX() + 42;
        int centerY = module.getHudY() + 42;
        Gui.drawRect(module.getHudX(), module.getHudY(), module.getHudX() + 84, module.getHudY() + 84, 0x990A0A0A);
        Gui.drawRect(centerX - 1, centerY - 1, centerX + 2, centerY + 2, 0xFFFFAA00);
        for (net.minecraft.entity.player.EntityPlayer player : module.MC.world.playerEntities) {
            if (player == module.MC.player || player.isDead) {
                continue;
            }
            double dx = player.posX - module.MC.player.posX;
            double dz = player.posZ - module.MC.player.posZ;
            int x = centerX + (int) Math.round(dx * 2.0D);
            int y = centerY + (int) Math.round(dz * 2.0D);
            if (x > module.getHudX() + 2 && x < module.getHudX() + 82
                    && y > module.getHudY() + 2 && y < module.getHudY() + 82) {
                Gui.drawRect(x - 1, y - 1, x + 2, y + 2, 0xFFFF5555);
            }
        }
    }

    private static void drawCrosshair(TuffModule module, int color) {
        int centerX = module.MC.scaledResolution.getScaledWidth() / 2;
        int centerY = module.MC.scaledResolution.getScaledHeight() / 2;
        Gui.drawRect(centerX - 5, centerY, centerX - 2, centerY + 1, color);
        Gui.drawRect(centerX + 2, centerY, centerX + 5, centerY + 1, color);
        Gui.drawRect(centerX, centerY - 5, centerX + 1, centerY - 2, color);
        Gui.drawRect(centerX, centerY + 2, centerX + 1, centerY + 5, color);
    }

    private static boolean isServerPluginFeatureAvailable() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null || mc.player == null) {
            return false;
        }
        String serverBrand = mc.player.getServerBrand();
        return serverBrand != null && !serverBrand.trim().isEmpty();
    }

    public static void register(Manager manager) {
        nextHudRow = 0;
        add(manager, "Quick Elytra", Category.Movement, "Uses a held/off-hand firework while elytra gliding.", Kind.QUICK_ELYTRA);
        add(manager, "Fast Crystals", Category.Combat, "Removes the local placement delay while placing end crystals.", Kind.FAST_CRYSTALS);
        add(manager, "Anchor Optimizer", Category.Combat, "Shows respawn-anchor charge and explosion information.", Kind.NONE);
        add(manager, "Hotbar Optimizer", Category.Player, "Keeps hotbar selection synchronized for normal actions.", Kind.NONE);
        add(manager, "Hotbar Switcher", Category.Player, "Shift + number swaps that hotbar slot with the next inventory row.", Kind.HOTBAR_SWITCHER);
        add(manager, "Fullbright", Category.Render, "Raises world brightness while enabled.", Kind.FULLBRIGHT);
        add(manager, "TNT Timer", Category.Render, "Shows the fuse remaining on the targeted primed TNT.", Kind.TNT);
        add(manager, "Anti Pickup", Category.Player, "Drops configured unwanted items automatically.", Kind.NONE);
        add(manager, "Zoom", Category.Render, "Hold Z to zoom the camera.", Kind.ZOOM);
        add(manager, "Glowing Ores", Category.Render, "Highlights the ore block currently under your crosshair.", Kind.ORE_HIGHLIGHT);
        add(manager, "Range Crosshair", Category.Combat, "Colors the aiming marker by targeted entity distance.", Kind.RANGE_CROSSHAIR);
        add(manager, "Better Hitboxes", Category.Combat, "Shows entity hitboxes using the vanilla debug renderer.", Kind.HITBOXES);
        add(manager, "Minimap", Category.Render, "Maps nearby players relative to your position.", Kind.MINIMAP);

        add(manager, "CPvP Mode", Category.Render, "Hides rain and explosion particles for clearer combat visuals.", Kind.CPVP);
        add(manager, "No Explosion Particles", Category.Render, "Hides explosion particles.", Kind.NO_EXPLOSION);
        add(manager, "No Rain", Category.Render, "Hides rain and snow rendering.", Kind.NO_RAIN);
        add(manager, "No Glint", Category.Render, "Hides enchantment glint.", Kind.NO_GLINT);
        add(manager, "No Dynamic FOV", Category.Render, "Keeps movement from changing camera FOV.", Kind.NO_DYNAMIC_FOV);
        add(manager, "Small Tools", Category.Render, "Renders held items smaller in first person.", Kind.SMALL_ITEMS);
        add(manager, "No Effect", Category.Render, "Reduces rendered particle effects.", Kind.NO_EFFECT);
        add(manager, "No Death Animation", Category.Render, "Removes the local camera death tilt.", Kind.NO_DEATH);
        add(manager, "Chat Clear", Category.Render, "Removes the chat background.", Kind.CHAT_CLEAR);
        add(manager, "No Background Tint", Category.Render, "Hides the dark damage vignette.", Kind.NO_BACKGROUND);
        add(manager, "Low on Fire", Category.Render, "Lowers the first-person fire overlay.", Kind.LOW_FIRE);

        add(manager, "W Tap Trainer", Category.Combat, "Shows time since your last attack as a movement-timing practice cue.", Kind.WTAP);
        add(manager, "PVP Tracker", Category.Combat, "Displays client-observed attacks and current target.", Kind.PVP_TRACKER);
        add(manager, "CPS", Category.Render, "Displays recent left and right clicks per second.", Kind.CPS);
        add(manager, "Speed", Category.Render, "Displays horizontal movement speed.", Kind.SPEED);
        add(manager, "Reach", Category.Render, "Displays distance to the targeted entity.", Kind.REACH);
        add(manager, "Combo Counter", Category.Combat, "Counts consecutive attack attempts on the current target.", Kind.COMBO);
        add(manager, "Shield Status", Category.Render, "Displays off-hand shield durability.", Kind.SHIELD);
        add(manager, "Totem Counter", Category.Render, "Displays totems in your inventory.", Kind.TOTEMS);
        add(manager, "FPS", Category.Render, "Displays current client FPS.", Kind.FPS);
        add(manager, "Armor HUD", Category.Render, "Displays equipped armor with durability percentages.", Kind.ARMOR);
        add(manager, "Speedrun Timer", Category.Render, "Displays elapsed time in the current world.", Kind.TIMER);

        add(manager, "Sprint Toggle", Category.Movement, "Automatically sprints while moving forward.", Kind.SPRINT);
        add(manager, "Shift Toggle", Category.Movement, "Automatically sneaks while moving.", Kind.SNEAK);
        add(manager, "WorldEdit CUI", Category.Render, "Displays WorldEdit selection data when available.", Kind.NONE);
        add(manager, "Auto GG", Category.Player, "Sends gg once when a server announces a clear match win.", Kind.AUTO_GG);
        add(manager, "Fancy Hover Block", Category.Render, "Displays the selected block name and coordinates.", Kind.FANCY_HOVER);

        add(manager, "Client Brander", Category.Player, "Displays client brand information when provided by peers.", Kind.NONE);
        add(manager, "AppleSkin", Category.Render, "Displays food and saturation information.", Kind.HUNGER);
        add(manager, "Compass", Category.Render, "Displays compass direction and heading.", Kind.COMPASS);
        add(manager, "Inventory HUD", Category.Render, "Shows your main inventory contents on screen.", Kind.INVENTORY);
        add(manager, "WAILA", Category.Render, "Displays information about the block under your crosshair.", Kind.WAILA);
        add(manager, "Potions", Category.Render, "Shows active potion effects and durations.", Kind.POTIONS);
        add(manager, "Shulker", Category.Render, "Previews the inventory of the shulker box under your crosshair.", Kind.SHULKER);
        add(manager, "Chat Heads", Category.Render, "Displays player heads beside chat messages.", Kind.NONE);

        add(manager, "Crosshair", Category.Render, "Draws a compact orange aiming marker.", Kind.CROSSHAIR);
        add(manager, "Wavey Capes", Category.Render, "Wave cape rendering.", Kind.NONE);
        add(manager, "Mace 3D", Category.Render, "Renders the mace with a custom 3D model.", Kind.NONE);
        add(manager, "Brays Bow", Category.Render, "Custom bow and arrow models.", Kind.NONE);

        add(manager, "Via Viewer", Category.Client, "Requires the matching TuffXPlus server plugin; unavailable in vanilla.", Kind.NONE);
        add(manager, "Via Blocks", Category.Client, "Requires the matching TuffXPlus server plugin; unavailable in vanilla.", Kind.NONE);
        add(manager, "Below Y0", Category.Client, "Requires server protocol/world support; this client is Minecraft 1.12.", Kind.NONE);
        add(manager, "Via Viewer Entity", Category.Client, "Requires the matching TuffXPlus server plugin; unavailable in vanilla.", Kind.NONE);
        add(manager, "VanillaFix Chunk", Category.Render, "Experimental chunk-unload optimization.", Kind.NONE);
        add(manager, "LDM", Category.Render, "Temporarily applies low-detail graphics settings.", Kind.LDM);

        add(manager, "List Layout", Category.Client, "Switches supported lists between list and grid layouts.", Kind.NONE);
        add(manager, "Teto Mode", Category.Client, "Alternative client theme.", Kind.NONE);
        add(manager, "Debug", Category.Client, "Toggles the vanilla debug overlay.", Kind.DEBUG);
        add(manager, "Sodium UI", Category.Client, "Alternative video settings layout.", Kind.NONE);
        add(manager, "Minecraft GUI", Category.Client, "Switches between client and Minecraft GUI styling.", Kind.NONE);
        add(manager, "Moving Background", Category.Client, "Moves the main-menu background with the pointer.", Kind.NONE);

        add(manager, "Small Totems", Category.Render, "Renders held totems smaller in first person.", Kind.SMALL_TOTEM);
        add(manager, "Durability", Category.Render, "Displays armor durability in the HUD.", Kind.ARMOR);
        add(manager, "Colorful Containers", Category.Client, "Alternative container GUI colors.", Kind.NONE);
        add(manager, "Enhanced Hotbar", Category.Render, "Alternative hotbar status-bar styling.", Kind.NONE);
        add(manager, "Health Bar", Category.Render, "Displays the targeted entity health.", Kind.ENTITY_HEALTH);
        add(manager, "Streamer", Category.Client, "Hides the coordinates module from the HUD.", Kind.STREAMER);
        add(manager, "Widgets", Category.Client, "Enables additional configurable HUD widgets.", Kind.NONE);
        add(manager, "Fast Math", Category.Render, "Uses conservative client-side math optimizations.", Kind.NONE);
    }

    private static void add(Manager manager, String name, Category category, String description, Kind kind) {
        Module module = new TuffModule(name, category, description, kind);
        manager.modules.add(module);
        MODULES.put(name, module);
    }

    public static boolean isEnabled(String moduleName) {
        Module module = MODULES.get(moduleName);
        return module != null && module.isEnabled();
    }

    public static void recordClick(boolean left) {
        if (left) {
            LEFT_CLICKS[leftClickIndex++ % LEFT_CLICKS.length] = System.currentTimeMillis();
        } else {
            RIGHT_CLICKS[rightClickIndex++ % RIGHT_CLICKS.length] = System.currentTimeMillis();
        }
    }

    public static void recordAttack(Entity target) {
        long now = System.currentTimeMillis();
        ++attackCount;
        if (target == lastAttackedEntity && now - lastAttackTime <= 2000L) {
            ++comboAttempts;
        } else {
            comboAttempts = 1;
        }
        lastAttackedEntity = target;
        lastAttackTime = now;
    }

    private static int clicksPerSecond(long[] clicks) {
        long cutoff = System.currentTimeMillis() - 1000L;
        int count = 0;
        for (long click : clicks) {
            if (click >= cutoff) {
                ++count;
            }
        }
        return count;
    }

    private enum Kind {
        NONE, TNT, CPS, SPEED, REACH, SHIELD, TOTEMS, FPS, ARMOR, TIMER, HUNGER, COMPASS, INVENTORY, WAILA,
        POTIONS, ENTITY_HEALTH, NO_RAIN, NO_EXPLOSION, NO_EFFECT, NO_GLINT, NO_DYNAMIC_FOV, NO_DEATH,
        NO_BACKGROUND, LOW_FIRE, ZOOM, FULLBRIGHT, MINIMAP, HITBOXES, SPRINT, SNEAK, CROSSHAIR, QUICK_ELYTRA,
        RANGE_CROSSHAIR, PVP_TRACKER, COMBO, LDM, CPVP, WTAP, FANCY_HOVER, ORE_HIGHLIGHT, SMALL_ITEMS,
        SMALL_TOTEM, CHAT_CLEAR, AUTO_GG, SHULKER, DEBUG, STREAMER, HOTBAR_SWITCHER, FAST_CRYSTALS
    }

    private static final class TuffModule extends Module {
        private static final Minecraft MC = Minecraft.getMinecraft();
        private final Kind kind;
        private double previousX;
        private double previousZ;
        private long worldStartTime = -1L;
        private int speedTenths;
        private float previousGamma;
        private boolean hadDebugHitboxes;
        private int fireworkCooldown;
        private int savedRenderDistance;
        private int savedParticleSetting;
        private int savedCloudSetting;
        private boolean savedFancyGraphics;
        private boolean previousDebugInfo;

        private TuffModule(String name, Category category, String description, Kind kind) {
            super(name, category);
            this.description = description;
            this.kind = kind;
            if (kind == Kind.ARMOR) {
                setHudPosition(8, 24);
            } else if (kind == Kind.INVENTORY) {
                setHudPosition(8, 72);
            } else if (kind == Kind.MINIMAP) {
                setHudPosition(220, 8);
            } else if (isHudKind(kind)) {
                setHudPosition(8, 8 + nextHudRow++ * 11);
            }
        }

        private static boolean isHudKind(Kind kind) {
            switch (kind) {
            case TNT:
            case CPS:
            case SPEED:
            case REACH:
            case SHIELD:
            case TOTEMS:
            case FPS:
            case TIMER:
            case HUNGER:
            case COMPASS:
            case WAILA:
            case POTIONS:
            case ENTITY_HEALTH:
            case PVP_TRACKER:
            case COMBO:
            case WTAP:
            case FANCY_HOVER:
            case SHULKER:
                return true;
            default:
                return false;
            }
        }

        @Override
        public void toggle() {
            if (kind == Kind.NONE && !isServerPluginFeatureAvailable()) {
                if (MC != null && MC.ingameGUI != null) {
                    MC.ingameGUI.getChatGUI().printChatMessage(new TextComponentString(
                            getName() + " requires a compatible server plugin to be enabled here."));
                }
                return;
            }
            super.toggle();
        }

        @Override
        public boolean isAvailable() {
            return kind != Kind.NONE || isServerPluginFeatureAvailable();
        }

        @Override
        public boolean isHudModule() {
            switch (kind) {
            case TNT:
            case CPS:
            case SPEED:
            case REACH:
            case SHIELD:
            case TOTEMS:
            case FPS:
            case ARMOR:
            case TIMER:
            case HUNGER:
            case COMPASS:
            case INVENTORY:
            case WAILA:
            case POTIONS:
            case ENTITY_HEALTH:
            case MINIMAP:
            case PVP_TRACKER:
            case COMBO:
            case WTAP:
            case FANCY_HOVER:
            case SHULKER:
                return true;
            default:
                return false;
            }
        }

        @Override
        public String getHudDisplayText() {
            if (MC == null || MC.player == null || MC.world == null) {
                return isHudModule() ? getName() : "";
            }
            switch (kind) {
            case TNT:
                return tntText();
            case CPS:
                return "CPS: " + clicksPerSecond(LEFT_CLICKS) + " | " + clicksPerSecond(RIGHT_CLICKS);
            case SPEED:
                return "Speed: " + speedTenths / 10 + "." + speedTenths % 10 + " m/s";
            case REACH:
                return MC.pointedEntity == null ? "Reach: --"
                        : "Reach: " + format(MC.player.getDistanceToEntity(MC.pointedEntity)) + "m";
            case SHIELD:
                ItemStack offhand = MC.player.getHeldItemOffhand();
                return offhand.getItem() == Items.SHIELD ? "Shield: " + durability(offhand) + "%" : "Shield: --";
            case TOTEMS:
                return "Totems: " + countItems(Items.TOTEM_OF_UNDYING);
            case FPS:
                return MC.getDebugFPS() + " FPS";
            case ARMOR:
                return "Armor durability";
            case TIMER:
                return timerText();
            case HUNGER:
                return "Food: " + MC.player.getFoodStats().getFoodLevel() + "  Saturation: "
                        + format(MC.player.getFoodStats().getSaturationLevel());
            case COMPASS:
                return compassText();
            case INVENTORY:
                return "Inventory: " + inventoryUsed() + "/36 slots";
            case WAILA:
                return wailaText();
            case POTIONS:
                return "Potion effects: " + MC.player.getActivePotionEffects().size();
            case MINIMAP:
                return "Nearby players: " + Math.max(0, MC.world.playerEntities.size() - 1);
            case PVP_TRACKER:
                return "Attacks: " + attackCount + "  Target: "
                        + (MC.pointedEntity == null ? "--" : MC.pointedEntity.getName());
            case COMBO:
                return "Combo attempts: " + (System.currentTimeMillis() - lastAttackTime <= 2000L
                        ? comboAttempts : 0);
            case WTAP:
                return lastAttackTime == 0L ? "W-Tap: hit a target to start"
                        : "W-Tap cue: " + Math.max(0L, System.currentTimeMillis() - lastAttackTime) + " ms";
            case FANCY_HOVER:
                return wailaDetails();
            case ENTITY_HEALTH:
                return MC.pointedEntity instanceof net.minecraft.entity.EntityLivingBase
                        ? MC.pointedEntity.getName() + ": "
                                + (int) ((net.minecraft.entity.EntityLivingBase) MC.pointedEntity).getHealth() + " HP"
                        : "Entity health: --";
            default:
                return "";
            }
        }

        @Override
        public void onUpdate() {
            if (MC == null || MC.player == null || MC.world == null) {
                worldStartTime = -1L;
                return;
            }
            if (kind == Kind.SPEED) {
                double dx = MC.player.posX - previousX;
                double dz = MC.player.posZ - previousZ;
                speedTenths = (int) (Math.sqrt(dx * dx + dz * dz) * 200.0D + 0.5D);
                previousX = MC.player.posX;
                previousZ = MC.player.posZ;
            } else if (kind == Kind.TIMER && worldStartTime < 0L) {
                worldStartTime = System.currentTimeMillis();
            }
            if (kind == Kind.FULLBRIGHT && MC.gameSettings != null) {
                MC.gameSettings.gammaSetting = 100.0F;
            } else if (kind == Kind.LDM && MC.gameSettings != null) {
                applyLowDetailSettings();
            } else if (kind == Kind.SPRINT) {
                MC.player.setSprinting(MC.gameSettings.keyBindForward.isKeyDown()
                        && !MC.player.isSneaking() && !MC.player.isHandActive());
            } else if (kind == Kind.SNEAK) {
                MC.player.setSneaking(MC.gameSettings.keyBindForward.isKeyDown()
                        || MC.gameSettings.keyBindBack.isKeyDown()
                        || MC.gameSettings.keyBindLeft.isKeyDown()
                        || MC.gameSettings.keyBindRight.isKeyDown());
            } else if (kind == Kind.QUICK_ELYTRA) {
                if (fireworkCooldown > 0) {
                    --fireworkCooldown;
                }
                if (fireworkCooldown == 0 && MC.player.isElytraFlying()
                        && (MC.player.getHeldItemMainhand().getItem() == Items.FIREWORKS
                                || MC.player.getHeldItemOffhand().getItem() == Items.FIREWORKS)) {
                    KeyBinding.onTick(MC.gameSettings.keyBindUseItem.getKeyCode());
                    fireworkCooldown = 20;
                }
            }
        }

        @Override
        public void onEnable() {
            if (MC != null && MC.player != null) {
                previousX = MC.player.posX;
                previousZ = MC.player.posZ;
            }
            if (kind == Kind.TIMER) {
                worldStartTime = MC == null || MC.world == null ? -1L : System.currentTimeMillis();
            }
            if (kind == Kind.FULLBRIGHT && MC != null && MC.gameSettings != null) {
                previousGamma = MC.gameSettings.gammaSetting;
                MC.gameSettings.gammaSetting = 100.0F;
            }
            if (kind == Kind.HITBOXES && MC != null && MC.getRenderManager() != null) {
                hadDebugHitboxes = MC.getRenderManager().isDebugBoundingBox();
                MC.getRenderManager().setDebugBoundingBox(true);
            }
            if (kind == Kind.DEBUG && MC != null && MC.gameSettings != null) {
                previousDebugInfo = MC.gameSettings.showDebugInfo;
                MC.gameSettings.showDebugInfo = true;
            }
            if (kind == Kind.LDM && MC != null && MC.gameSettings != null) {
                savedRenderDistance = MC.gameSettings.renderDistanceChunks;
                savedParticleSetting = MC.gameSettings.particleSetting;
                savedCloudSetting = MC.gameSettings.clouds;
                savedFancyGraphics = MC.gameSettings.fancyGraphics;
                applyLowDetailSettings();
            }
        }

        @Override
        public void onDisable() {
            if (kind == Kind.FULLBRIGHT && MC != null && MC.gameSettings != null) {
                MC.gameSettings.gammaSetting = previousGamma;
            }
            if (kind == Kind.HITBOXES && MC != null && MC.getRenderManager() != null) {
                MC.getRenderManager().setDebugBoundingBox(hadDebugHitboxes);
            }
            if (kind == Kind.DEBUG && MC != null && MC.gameSettings != null) {
                MC.gameSettings.showDebugInfo = previousDebugInfo;
            }
            if (kind == Kind.LDM && MC != null && MC.gameSettings != null) {
                MC.gameSettings.renderDistanceChunks = savedRenderDistance;
                MC.gameSettings.particleSetting = savedParticleSetting;
                MC.gameSettings.clouds = savedCloudSetting;
                MC.gameSettings.fancyGraphics = savedFancyGraphics;
                MC.renderGlobal.loadRenderers();
            }
        }

        private void applyLowDetailSettings() {
            MC.gameSettings.renderDistanceChunks = Math.min(MC.gameSettings.renderDistanceChunks, 4);
            MC.gameSettings.particleSetting = 2;
            MC.gameSettings.clouds = 0;
            MC.gameSettings.fancyGraphics = false;
        }

        private void drawShulkerPreview() {
            if (MC.objectMouseOver == null || MC.objectMouseOver.typeOfHit != RayTraceResult.Type.BLOCK) {
                return;
            }
            net.minecraft.tileentity.TileEntity tile = MC.world.getTileEntity(MC.objectMouseOver.getBlockPos());
            if (!(tile instanceof net.minecraft.tileentity.TileEntityShulkerBox)) {
                return;
            }
            net.minecraft.tileentity.TileEntityShulkerBox box = (net.minecraft.tileentity.TileEntityShulkerBox) tile;
            int x = getHudX();
            int y = getHudY();
            Gui.drawRect(x - 3, y - 13, x + 9 * 18 + 3, y + 3 * 18 + 3, 0xCC101010);
            MC.fontRendererObj.drawStringWithShadow("Shulker contents", x, y - 11, 0xFFFFD21F);
            for (int slot = 0; slot < box.getSizeInventory(); ++slot) {
                ItemStack stack = box.getStackInSlot(slot);
                if (!stack.func_190926_b()) {
                    int itemX = x + slot % 9 * 18;
                    int itemY = y + slot / 9 * 18;
                    MC.getRenderItem().renderItemAndEffectIntoGUI(stack, itemX, itemY);
                    MC.getRenderItem().renderItemOverlayIntoGUI(MC.fontRendererObj, stack, itemX, itemY, null);
                }
            }
        }

        @Override
        public void onRender() {
            if (MC == null || MC.fontRendererObj == null) {
                return;
            }
            if (kind == Kind.ARMOR && MC.player != null && MC.scaledResolution != null) {
                drawArmor();
                return;
            }
            if (kind == Kind.INVENTORY && MC.player != null) {
                drawInventory();
                return;
            }
            if (kind == Kind.POTIONS && MC.player != null) {
                drawPotions(this);
                return;
            }
            if (kind == Kind.MINIMAP && MC.player != null && MC.world != null) {
                drawMinimap(this);
                return;
            }
            if (kind == Kind.SHULKER && MC.player != null && MC.world != null) {
                drawShulkerPreview();
                return;
            }
            if (kind == Kind.CROSSHAIR && MC.scaledResolution != null) {
                drawCrosshair(this, 0xFFFFA500);
                return;
            }
            if (kind == Kind.RANGE_CROSSHAIR && MC.scaledResolution != null) {
                double distance = MC.pointedEntity == null ? Double.POSITIVE_INFINITY
                        : MC.player.getDistanceToEntity(MC.pointedEntity);
                drawCrosshair(this,
                        distance <= 3.0D ? 0xFF55FF55 : distance <= 4.0D ? 0xFFFFFF55 : 0xFFFF5555);
                return;
            }
            String text = getHudDisplayText();
            if (text != null && !text.isEmpty()) {
                MC.fontRendererObj.drawStringWithShadow(text, (float) getHudX(), (float) getHudY(), 0xFFFFFFFF);
            }
        }

        private String tntText() {
            Entity target = MC.pointedEntity;
            if (!(target instanceof EntityTNTPrimed)) {
                return "TNT: --";
            }
            int ticks = ((EntityTNTPrimed) target).getFuse();
            return "TNT: " + format(ticks / 20.0D) + "s";
        }

        private String timerText() {
            if (worldStartTime < 0L) {
                return "Speedrun: 00:00";
            }
            long seconds = (System.currentTimeMillis() - worldStartTime) / 1000L;
            return String.format(java.util.Locale.ROOT, "Speedrun: %02d:%02d:%02d",
                    seconds / 3600L, seconds / 60L % 60L, seconds % 60L);
        }

        private String compassText() {
            String[] directions = { "S", "W", "N", "E" };
            int index = (int) Math.floor(MC.player.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
            return "Compass: " + directions[index] + " " + ((int) MC.player.rotationYaw + 360) % 360 + "\u00b0";
        }

        private String wailaText() {
            if (MC.objectMouseOver == null || MC.objectMouseOver.typeOfHit != RayTraceResult.Type.BLOCK) {
                return "WAILA: --";
            }
            return "WAILA: " + MC.world.getBlockState(MC.objectMouseOver.getBlockPos()).getBlock().getLocalizedName();
        }

        private String wailaDetails() {
            if (MC.objectMouseOver == null || MC.objectMouseOver.typeOfHit != RayTraceResult.Type.BLOCK) {
                return "Block: --";
            }
            BlockPos pos = MC.objectMouseOver.getBlockPos();
            String name = MC.world.getBlockState(pos).getBlock().getLocalizedName();
            return name + " (" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")";
        }

        private int countItems(net.minecraft.item.Item item) {
            int count = 0;
            for (ItemStack stack : MC.player.inventory.mainInventory) {
                if (!stack.func_190926_b() && stack.getItem() == item) {
                    count += stack.func_190916_E();
                }
            }
            return count;
        }

        private int inventoryUsed() {
            int count = 0;
            for (ItemStack stack : MC.player.inventory.mainInventory) {
                if (!stack.func_190926_b()) {
                    ++count;
                }
            }
            return count;
        }

        private int durability(ItemStack stack) {
            return !stack.func_190926_b() && stack.isItemStackDamageable() && stack.getMaxDamage() > 0
                    ? Math.max(0, stack.getMaxDamage() - stack.getItemDamage()) * 100 / stack.getMaxDamage() : 100;
        }

        private void drawArmor() {
            int baseX = getHudX();
            int slotY = getHudY();
            for (int slot = 0; slot < 4; ++slot) {
                ItemStack stack = MC.player.inventory.armorItemInSlot(3 - slot);
                if (stack.func_190926_b()) {
                    continue;
                }
                int x = baseX + slot * 20;
                Gui.drawRect(x, slotY, x + 18, slotY + 18, 0xAA101010);
                MC.getRenderItem().renderItemAndEffectIntoGUI(MC.player, stack, x + 1, slotY + 1);
                String value = durability(stack) + "%";
                int textX = x + (18 - MC.fontRendererObj.getStringWidth(value)) / 2;
                MC.fontRendererObj.drawStringWithShadow(value, textX, slotY - 9,
                        durability(stack) <= 25 ? 0xFFFF5555 : 0xFFFFFFFF);
            }
        }

        private void drawInventory() {
            ScaledResolution resolution = MC.scaledResolution;
            int x = Math.min(getHudX(), resolution.getScaledWidth() - 18);
            int y = Math.min(getHudY(), resolution.getScaledHeight() - 18 * 9);
            for (int index = 9; index < 36; ++index) {
                int row = (index - 9) / 9;
                int column = (index - 9) % 9;
                ItemStack stack = MC.player.inventory.mainInventory.get(index);
                if (!stack.func_190926_b()) {
                    MC.getRenderItem().renderItemAndEffectIntoGUI(stack, x + column * 18, y + row * 18);
                    MC.getRenderItem().renderItemOverlayIntoGUI(MC.fontRendererObj, stack, x + column * 18,
                            y + row * 18, null);
                }
            }
        }

        private static String format(double value) {
            return String.format(java.util.Locale.ROOT, "%.1f", value);
        }
    }
}
