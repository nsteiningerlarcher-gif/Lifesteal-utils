package com.example.lifesteal;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;

public final class LifestealClient implements ClientModInitializer {
    public static final String MOD_ID = "lifestealutility";

    public static boolean visibleBlockEsp = true;
    public static boolean autoPlanter = false;
    public static boolean spectatorListEnabled = false;

    private static KeyMapping openMenuKey;

    private static String targetBlockName = "No block targeted";
    private static String targetCoordinates = "-";
    private static int planterCooldown = 0;

    @Override
    public void onInitializeClient() {
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(MOD_ID, "main")
        );

        openMenuKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.lifestealutility.open_menu",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_J,
                category
        ));
        ClientTickEvents.END_CLIENT_TICK.register(LifestealClient::onClientTick);
        HudElementRegistry.attachElementAfter(
                VanillaHudElements.HOTBAR,
                Identifier.fromNamespaceAndPath(MOD_ID, "utility_hud"),
                LifestealClient::renderHud
        );
    }

    private static void onClientTick(Minecraft client) {
        while (openMenuKey.consumeClick()) {
            client.setScreen(new UtilityScreen(client.screen));
        }
        updateVisibleTarget(client);
        runLocalAutoPlanter(client);
    }

    private static void updateVisibleTarget(Minecraft client) {
        targetBlockName = "No block targeted";
        targetCoordinates = "-";

        if (!visibleBlockEsp || client.player == null || client.level == null) {
            return;
        }
        if (client.hitResult instanceof BlockHitResult hit && hit.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = hit.getBlockPos();
            var state = client.level.getBlockState(pos);
            targetBlockName = state.getBlock().getName().getString();
            targetCoordinates = pos.getX() + ", " + pos.getY() + ", " + pos.getZ();
        }
    }

    /**
     * Planting automation is deliberately restricted to a locally hosted
     * singleplayer world. It never sends automated interactions to multiplayer servers.
     */
    private static void runLocalAutoPlanter(Minecraft client) {
        if (planterCooldown > 0) {
            planterCooldown--;
        }
        if (!autoPlanter || !client.hasSingleplayerServer() || client.player == null
                || client.level == null || client.gameMode == null || planterCooldown > 0) {
            return;
        }
        if (!(client.hitResult instanceof BlockHitResult hit) || hit.getType() != HitResult.Type.BLOCK) {
            return;
        }

        BlockPos farmlandPos = hit.getBlockPos();
        if (!client.level.getBlockState(farmlandPos).is(Blocks.FARMLAND)
                || !client.level.isEmptyBlock(farmlandPos.above())
                || !isPlantableCrop(client.player.getMainHandItem())) {
            return;
        }

        client.gameMode.useItemOn(client.player, InteractionHand.MAIN_HAND, hit);
        planterCooldown = 8;
    }

    private static boolean isPlantableCrop(ItemStack stack) {
        return stack.is(Items.WHEAT_SEEDS)
                || stack.is(Items.BEETROOT_SEEDS)
                || stack.is(Items.MELON_SEEDS)
                || stack.is(Items.PUMPKIN_SEEDS)
                || stack.is(Items.TORCHFLOWER_SEEDS)
                || stack.is(Items.PITCHER_POD)
                || stack.is(Items.CARROT)
                || stack.is(Items.POTATO);
    }

    /**
     * Reads only spectator game modes the server already publishes in the normal
     * player list. It cannot detect vanished/hidden players or override server privacy.
     */
    private static List<String> getPubliclyListedSpectators(Minecraft client) {
        if (client.getConnection() == null) {
            return List.of();
        }
        return client.getConnection().getListedOnlinePlayers().stream()
                .filter(info -> info.getGameMode() == GameType.SPECTATOR)
                .map(PlayerInfo::getProfile)
                .map(profile -> profile.getName())
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    private static void renderHud(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.options.hideGui) {
            return;
        }

        List<String> spectatorNames = spectatorListEnabled
                ? getPubliclyListedSpectators(client)
                : List.of();
        int left = 8;
        int top = 8;
        int right = 260;
        int baseBottom = visibleBlockEsp ? 55 : 37;
        int spectatorRows = 0;
        if (spectatorListEnabled) {
            spectatorRows = spectatorNames.isEmpty()
                    ? 1
                    : Math.min(spectatorNames.size(), 5) + (spectatorNames.size() > 5 ? 1 : 0);
        }
        int spectatorHeaderHeight = spectatorListEnabled ? 14 : 0;
        int bottom = baseBottom + spectatorHeaderHeight + spectatorRows * 12;
        graphics.fill(left, top, right, bottom, 0xD90A1730);
        graphics.fill(left, top, left + 3, bottom, 0xFF279DFF);
        graphics.text(client.font, "LIFESTEAL UTILITY", left + 10, top + 6, 0xFF8BCBFF, true);
        graphics.text(client.font, "Auto Planter: " + (autoPlanter ? "ON" : "OFF"),
                left + 10, top + 20, autoPlanter ? 0xFF70E6B0 : 0xFF9AAAC4, true);

        if (visibleBlockEsp) {
            graphics.text(client.font, "Block: " + targetBlockName, left + 10, top + 34, 0xFFFFFFFF, true);
            graphics.text(client.font, "XYZ: " + targetCoordinates, left + 10, top + 46, 0xFFB4C6E2, true);
        }

        if (spectatorListEnabled) {
            int y = baseBottom + 2;
            graphics.text(client.font, "Public spectators: " + spectatorNames.size(),
                    left + 10, y, 0xFF8BCBFF, true);
            y += 12;
            if (spectatorNames.isEmpty()) {
                graphics.text(client.font, "None listed", left + 10, y, 0xFF9AAAC4, true);
            } else {
                for (int i = 0; i < Math.min(spectatorNames.size(), 5); i++) {
                    graphics.text(client.font, spectatorNames.get(i), left + 10, y + i * 12,
                            0xFFFFFFFF, true);
                }
                if (spectatorNames.size() > 5) {
                    graphics.text(client.font, "+" + (spectatorNames.size() - 5) + " more",
                            left + 10, y + 5 * 12, 0xFF9AAAC4, true);
                }
            }
        }
    }
}
