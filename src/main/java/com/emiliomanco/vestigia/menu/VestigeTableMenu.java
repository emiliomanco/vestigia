package com.emiliomanco.vestigia.menu;

import com.emiliomanco.vestigia.block.entity.VestigeTableBlockEntity;
import com.emiliomanco.vestigia.registry.ModMenus;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;

public class VestigeTableMenu extends AbstractContainerMenu {

    private static final int SLOT_COUNT = VestigeTableBlockEntity.CONTAINER_SIZE;
    private static final int INVENTORY_X = 8;
    private static final int INVENTORY_Y = 104;

    private static final int SLOT_PITCH = 18;
    private static final int INGREDIENT_COLUMNS = 2;
    private static final int INGREDIENT_X = 44;
    private static final int INGREDIENT_Y = 26;
    private static final int RESULT_X = 116;
    private static final int RESULT_Y = 36;

    private final Container table;
    private final ContainerData data;

    public VestigeTableMenu(int containerId, Inventory inventory) {
        this(containerId, inventory, new SimpleContainer(SLOT_COUNT), new SimpleContainerData(VestigeTableBlockEntity.DATA_COUNT));
    }

    public VestigeTableMenu(int containerId, Inventory inventory, Container table, ContainerData data) {
        super(ModMenus.VESTIGE_TABLE.get(), containerId);
        checkContainerSize(table, SLOT_COUNT);
        checkContainerDataCount(data, VestigeTableBlockEntity.DATA_COUNT);
        this.table = table;
        this.data = data;

        for (int i = 0; i < VestigeTableBlockEntity.INGREDIENT_COUNT; i++) {
            int col = i % INGREDIENT_COLUMNS;
            int row = i / INGREDIENT_COLUMNS;
            addSlot(new Slot(table, VestigeTableBlockEntity.SLOT_FIRST_INGREDIENT + i,
                    INGREDIENT_X + col * SLOT_PITCH, INGREDIENT_Y + row * SLOT_PITCH));
        }
        addSlot(new RitualResultSlot(table, VestigeTableBlockEntity.SLOT_RESULT, RESULT_X, RESULT_Y));

        addDataSlots(data);
        addStandardInventorySlots(inventory, INVENTORY_X, INVENTORY_Y);
    }

    public boolean hasRitual() {
        return data.get(VestigeTableBlockEntity.DATA_HAS_RITUAL) != 0;
    }

    @Override
    public boolean stillValid(Player player) {
        return table.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        Slot slot = slots.get(slotIndex);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (slotIndex < SLOT_COUNT) {
            if (!moveItemStackTo(stack, SLOT_COUNT, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(stack, original);
        } else {
            if (!moveItemStackTo(stack, 0, VestigeTableBlockEntity.SLOT_RESULT, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (stack.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, stack);
        return original;
    }

    private static class RitualResultSlot extends Slot {
        RitualResultSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return false;
        }

        @Override
        public boolean mayPickup(Player player) {
            return container instanceof VestigeTableBlockEntity table && table.canComplete();
        }

        @Override
        public void onTake(Player player, ItemStack taken) {
            if (container instanceof VestigeTableBlockEntity table) {
                float experience = table.consumeForCompletedRitual();
                if (experience > 0.0F && !player.level().isClientSide()) {
                    player.giveExperiencePoints(Math.round(experience));
                }
            }
            super.onTake(player, taken);
        }
    }
}
