package com.emiliomanco.vestigia.item.god.pachamama;

import com.emiliomanco.vestigia.registry.ModDataComponents;
import com.emiliomanco.vestigia.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.Nullable;

public final class CrownCycle {
    private CrownCycle() {}

    public static void cycle(ServerPlayer player, int step) {
        ItemStack supreme = supremeCrown(player);
        if (supreme != null) {
            cycleSupreme(player, supreme, step);
            return;
        }
        cycleSeparateCrowns(player, step);
    }

    private static @Nullable ItemStack supremeCrown(ServerPlayer player) {
        ItemStack worn = player.getItemBySlot(EquipmentSlot.HEAD);
        if (worn.is(ModItems.SUPREME_CROWN.get())) {
            return worn;
        }
        for (ItemStack stack : player.getInventory()) {
            if (stack.is(ModItems.SUPREME_CROWN.get())) {
                return stack;
            }
        }
        return null;
    }

    private static void cycleSupreme(ServerPlayer player, ItemStack crown, int step) {
        BendingBranch current = crown.get(ModDataComponents.BENDING_BRANCH.get());
        BendingBranch next = step(current, step);
        crown.set(ModDataComponents.BENDING_BRANCH.get(), next);
        announce(player, next);
    }

    private static void cycleSeparateCrowns(ServerPlayer player, int step) {
        List<BendingBranch> owned = new ArrayList<>();
        for (BendingBranch branch : BendingBranch.values()) {
            if (findCrown(player, branch) != null) {
                owned.add(branch);
            }
        }
        if (owned.size() < 2) {
            return;
        }

        BendingBranch worn = Bending.branchOf(player);
        int index = worn == null ? -1 : owned.indexOf(worn);
        BendingBranch target = owned.get(Math.floorMod(index + step, owned.size()));
        if (target == worn) {
            return;
        }

        ItemStack next = findCrown(player, target);
        if (next == null) {
            return;
        }
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack incoming = next.copy();
        next.setCount(0);

        player.setItemSlot(EquipmentSlot.HEAD, incoming);
        if (!head.isEmpty() && !player.getInventory().add(head)) {
            player.drop(head, false);
        }
        announce(player, target);
    }

    private static @Nullable ItemStack findCrown(ServerPlayer player, BendingBranch branch) {
        ItemStack worn = player.getItemBySlot(EquipmentSlot.HEAD);
        if (isCrownOf(worn, branch)) {
            return worn;
        }
        for (ItemStack stack : player.getInventory()) {
            if (isCrownOf(stack, branch)) {
                return stack;
            }
        }
        return null;
    }

    private static boolean isCrownOf(ItemStack stack, BendingBranch branch) {
        return stack.is(ModItems.CROWN_OF_PACHAMAMA.get())
                && stack.get(ModDataComponents.BENDING_BRANCH.get()) == branch;
    }

    private static BendingBranch step(@Nullable BendingBranch from, int step) {
        BendingBranch[] all = BendingBranch.values();
        int index = from == null ? -1 : from.ordinal();
        return all[Math.floorMod(index + step, all.length)];
    }

    private static void announce(ServerPlayer player, BendingBranch branch) {
        player.sendSystemMessage(Component.translatable(branch.nameKey())
                .withStyle(branch.colour(), ChatFormatting.BOLD), true);
        player.level().playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME,
                SoundSource.PLAYERS, 0.6F, 1.0F + branch.ordinal() * 0.12F);
    }
}
