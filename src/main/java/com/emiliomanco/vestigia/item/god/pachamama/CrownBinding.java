package com.emiliomanco.vestigia.item.god.pachamama;

import com.emiliomanco.vestigia.registry.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public final class CrownBinding {
    private CrownBinding() {}

    public static boolean hasUnboundCrown(ServerPlayer player) {
        return unbound(player) != null;
    }

    public static void choose(ServerPlayer player, BendingBranch branch) {
        ItemStack crown = unbound(player);
        if (crown == null) {
            return;
        }
        crown.set(ModDataComponents.BENDING_BRANCH.get(), branch);

        player.level().playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE,
                SoundSource.PLAYERS, 0.8F, 1.2F);
        player.sendSystemMessage(Component.translatable(
                        "item.vestigia.corona_pachamama.bound",
                        Component.translatable(branch.nameKey()).withStyle(branch.colour()))
                .withStyle(ChatFormatting.GRAY), true);
    }

    private static ItemStack unbound(ServerPlayer player) {
        ItemStack worn = player.getItemBySlot(EquipmentSlot.HEAD);
        if (isUnbound(worn)) {
            return worn;
        }
        for (ItemStack stack : player.getInventory()) {
            if (isUnbound(stack)) {
                return stack;
            }
        }
        return null;
    }

    private static boolean isUnbound(ItemStack stack) {
        return stack.is(com.emiliomanco.vestigia.registry.ModItems.CROWN_OF_PACHAMAMA.get())
                && stack.get(ModDataComponents.BENDING_BRANCH.get()) == null;
    }
}
