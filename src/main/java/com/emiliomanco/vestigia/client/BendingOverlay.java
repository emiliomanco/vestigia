package com.emiliomanco.vestigia.client;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.item.god.pachamama.Bending;
import com.emiliomanco.vestigia.item.god.pachamama.BendingBranch;
import com.emiliomanco.vestigia.registry.ModDataComponents;
import com.emiliomanco.vestigia.registry.ModItems;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = Vestigia.MODID, value = Dist.CLIENT)
public final class BendingOverlay {
    private BendingOverlay() {}

    private static final int MARGIN = 6;

    private static final int GAP = 2;

    @SubscribeEvent
    static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, Vestigia.id("bending_elements"),
                (graphics, delta) -> render(graphics));
    }

    private static void render(GuiGraphicsExtractor graphics) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null || minecraft.options.hideGui) {
            return;
        }

        List<BendingBranch> available = available(player);
        if (available.isEmpty()) {
            return;
        }
        BendingBranch active = active(player);

        int right = minecraft.getWindow().getGuiScaledWidth() - MARGIN - ElementIcons.HUD_SIZE;
        int height = available.size() * ElementIcons.HUD_SIZE + (available.size() - 1) * GAP;
        int top = minecraft.getWindow().getGuiScaledHeight() - MARGIN - height;

        for (int row = 0; row < available.size(); row++) {
            BendingBranch branch = available.get(row);
            int y = top + row * (ElementIcons.HUD_SIZE + GAP);
            ElementIcons.draw(graphics, ElementIcons.of(branch), right, y, ElementIcons.HUD_SIZE);
            if (branch == active) {
                ElementIcons.draw(graphics, ElementIcons.SELECTED, right, y, ElementIcons.HUD_SIZE);
            }
        }
    }

    private static List<BendingBranch> available(LocalPlayer player) {
        List<BendingBranch> found = new ArrayList<>();
        if (hasSupreme(player)) {
            for (BendingBranch branch : BendingBranch.values()) {
                found.add(branch);
            }
            return found;
        }
        for (BendingBranch branch : BendingBranch.values()) {
            if (carries(player, branch)) {
                found.add(branch);
            }
        }
        return found;
    }

    private static boolean hasSupreme(LocalPlayer player) {
        if (player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.SUPREME_CROWN.get())) {
            return true;
        }
        for (ItemStack stack : player.getInventory()) {
            if (stack.is(ModItems.SUPREME_CROWN.get())) {
                return true;
            }
        }
        return false;
    }

    private static boolean carries(LocalPlayer player, BendingBranch branch) {
        if (matches(player.getItemBySlot(EquipmentSlot.HEAD), branch)) {
            return true;
        }
        for (ItemStack stack : player.getInventory()) {
            if (matches(stack, branch)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matches(ItemStack stack, BendingBranch branch) {
        return stack.is(ModItems.CROWN_OF_PACHAMAMA.get())
                && stack.get(ModDataComponents.BENDING_BRANCH.get()) == branch;
    }

    private static BendingBranch active(LocalPlayer player) {
        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        return head.is(ModItems.CROWN_OF_PACHAMAMA.get()) || head.is(ModItems.SUPREME_CROWN.get())
                ? head.get(ModDataComponents.BENDING_BRANCH.get())
                : null;
    }
}
