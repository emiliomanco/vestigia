package com.emiliomanco.vestigia.ritual;

import com.emiliomanco.vestigia.registry.ModRecipes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public final class VestigeRitualRecipe implements Recipe<VestigeRitualInput> {

    private final Recipe.CommonInfo commonInfo;
    private final List<Ingredient> ingredients;
    private final ItemStackTemplate result;
    private final float experience;

    public VestigeRitualRecipe(Recipe.CommonInfo commonInfo,
                               List<Ingredient> ingredients,
                               ItemStackTemplate result,
                               float experience) {
        this.commonInfo = commonInfo;
        this.ingredients = List.copyOf(ingredients);
        this.result = result;
        this.experience = experience;
    }

    public static final MapCodec<VestigeRitualRecipe> MAP_CODEC = RecordCodecBuilder.mapCodec(
            i -> i.group(
                    Recipe.CommonInfo.MAP_CODEC.forGetter(o -> o.commonInfo),
                    Ingredient.CODEC.listOf(1, VestigeRitualInput.INGREDIENT_COUNT)
                            .fieldOf("ingredients").forGetter(o -> o.ingredients),
                    ItemStackTemplate.CODEC.fieldOf("result").forGetter(o -> o.result),
                    com.mojang.serialization.Codec.FLOAT.optionalFieldOf("experience", 0.0F)
                            .forGetter(o -> o.experience)
            ).apply(i, VestigeRitualRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, VestigeRitualRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    Recipe.CommonInfo.STREAM_CODEC, o -> o.commonInfo,
                    Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), o -> o.ingredients,
                    ItemStackTemplate.STREAM_CODEC, o -> o.result,
                    ByteBufCodecs.FLOAT, o -> o.experience,
                    VestigeRitualRecipe::new);

    public boolean matchesItems(VestigeRitualInput input) {
        return ingredientsMatch(input);
    }

    @Override
    public boolean matches(VestigeRitualInput input, Level level) {
        return matchesItems(input);
    }

    private boolean ingredientsMatch(VestigeRitualInput input) {
        List<ItemStack> present = new ArrayList<>(VestigeRitualInput.INGREDIENT_COUNT);
        for (ItemStack stack : input.ingredients()) {
            if (!stack.isEmpty()) {
                present.add(stack);
            }
        }
        if (present.size() != ingredients.size()) {
            return false;
        }
        boolean[] used = new boolean[present.size()];
        for (Ingredient required : ingredients) {
            int matchedAt = -1;
            for (int slot = 0; slot < present.size(); slot++) {
                if (!used[slot] && required.test(present.get(slot))) {
                    matchedAt = slot;
                    break;
                }
            }
            if (matchedAt < 0) {
                return false;
            }
            used[matchedAt] = true;
        }
        return true;
    }

    @Override
    public ItemStack assemble(VestigeRitualInput input) {
        return result.create();
    }

    public float experience() {
        return experience;
    }

    public ItemStack previewResult() {
        return result.create();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public boolean showNotification() {
        return commonInfo.showNotification();
    }

    @Override
    public String group() {
        return "";
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }

    @Override
    public RecipeBookCategory recipeBookCategory() {
        return RecipeBookCategories.CRAFTING_MISC;
    }

    @Override
    public RecipeSerializer<VestigeRitualRecipe> getSerializer() {
        return ModRecipes.VESTIGE_RITUAL_SERIALIZER.get();
    }

    @Override
    public RecipeType<VestigeRitualRecipe> getType() {
        return ModRecipes.VESTIGE_RITUAL_TYPE.get();
    }

    @Override
    public String toString() {
        return "VestigeRitualRecipe[result=" + result.create().getItem() + "]";
    }

    @Override
    public boolean equals(@Nullable Object other) {
        return this == other;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }
}
