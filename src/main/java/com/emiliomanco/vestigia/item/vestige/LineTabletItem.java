package com.emiliomanco.vestigia.item.vestige;

import com.emiliomanco.vestigia.config.VestigiaConfig;
import com.emiliomanco.vestigia.worldgen.ModWorldgen;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;

public class LineTabletItem extends VestigeItem {

    private static final List<ResourceKey<Structure>> KNOWN = List.of(
            ModWorldgen.MAYA_PYRAMID);

    private static final String[] COMPASS =
            {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};

    public LineTabletItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        int range = VestigiaConfig.LINE_TABLET_RANGE.get();
        int searchChunks = Math.max(1, range / 16 / 8);

        player.sendSystemMessage(Component.translatable("item.vestigia.line_tablet.header")
                .withStyle(ChatFormatting.GOLD));

        int found = 0;
        for (ResourceKey<Structure> key : KNOWN) {
            var lookup = serverLevel.registryAccess().lookupOrThrow(Registries.STRUCTURE).get(key);
            if (lookup.isEmpty()) {
                continue;
            }
            var hit = serverLevel.getChunkSource().getGenerator().findNearestMapStructure(
                    serverLevel, HolderSet.direct(lookup.get()), player.blockPosition(),
                    searchChunks, false);
            if (hit == null) {
                continue;
            }
            BlockPos pos = hit.getFirst();
            int distance = (int) Math.sqrt(pos.distSqr(player.blockPosition()));
            if (distance > range) {
                continue;
            }
            found++;
            player.sendSystemMessage(Component.translatable("item.vestigia.line_tablet.entry",
                    Component.translatable(key.identifier().toLanguageKey("structure")),
                    distance,
                    bearingTo(player, pos)));
        }

        if (found == 0) {
            player.sendSystemMessage(Component.translatable("item.vestigia.line_tablet.nothing")
                    .withStyle(ChatFormatting.GRAY));
        }

        serverLevel.playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_RESONATE,
                SoundSource.PLAYERS, 0.7F, 1.4F);
        player.getCooldowns().addCooldown(stack, VestigiaConfig.LINE_TABLET_COOLDOWN_TICKS.get());
        return InteractionResult.CONSUME;
    }

    private static String bearingTo(Player player, BlockPos target) {
        double dx = target.getX() - player.getX();
        double dz = target.getZ() - player.getZ();
        double degrees = Math.toDegrees(Math.atan2(dx, -dz));
        int index = (int) Math.round(((degrees % 360) + 360) % 360 / 45.0) % COMPASS.length;
        return COMPASS[index];
    }
}
