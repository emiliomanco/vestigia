package com.emiliomanco.vestigia.ritual;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record VestigeRitualInput(List<ItemStack> ingredients, BlockPos pos) implements RecipeInput {

    public static final int INGREDIENT_COUNT = 4;

    public static final int SLOT_FIRST_INGREDIENT = 0;

    public VestigeRitualInput {
        if (ingredients.size() != INGREDIENT_COUNT) {
            throw new IllegalArgumentException(
                    "A Vestige Table has exactly " + INGREDIENT_COUNT + " slots, got " + ingredients.size());
        }
        ingredients = List.copyOf(ingredients);
    }

    @Override
    public ItemStack getItem(int index) {
        if (index < 0 || index >= INGREDIENT_COUNT) {
            throw new IllegalArgumentException("No slot " + index + " on a Vestige Table");
        }
        return ingredients.get(index);
    }

    @Override
    public int size() {
        return INGREDIENT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack ingredient : ingredients) {
            if (!ingredient.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
