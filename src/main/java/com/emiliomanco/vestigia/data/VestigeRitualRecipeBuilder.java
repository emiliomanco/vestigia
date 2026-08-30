package com.emiliomanco.vestigia.data;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.ritual.VestigeRitualInput;
import com.emiliomanco.vestigia.ritual.VestigeRitualRecipe;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;

public final class VestigeRitualRecipeBuilder {

    private final ItemStackTemplate result;
    private final List<Ingredient> ingredients = new ArrayList<>();
    private float experience;
    private boolean showNotification = true;

    private VestigeRitualRecipeBuilder(ItemStackTemplate result) {
        this.result = result;
    }

    public static VestigeRitualRecipeBuilder ritual(ItemLike result) {
        return new VestigeRitualRecipeBuilder(new ItemStackTemplate(result.asItem()));
    }

    public static VestigeRitualRecipeBuilder ritual(ItemLike result, int count) {
        return new VestigeRitualRecipeBuilder(new ItemStackTemplate(result.asItem(), count));
    }

    public VestigeRitualRecipeBuilder ingredient(ItemLike item) {
        return ingredient(Ingredient.of(item));
    }

    public VestigeRitualRecipeBuilder ingredient(Ingredient ingredient) {
        if (ingredients.size() >= VestigeRitualInput.INGREDIENT_COUNT) {
            throw new IllegalStateException(
                    "A Vestige Table has only " + VestigeRitualInput.INGREDIENT_COUNT + " slots");
        }
        ingredients.add(ingredient);
        return this;
    }

    public VestigeRitualRecipeBuilder ingredient(ItemLike item, int times) {
        for (int i = 0; i < times; i++) {
            ingredient(item);
        }
        return this;
    }

    public VestigeRitualRecipeBuilder experience(float experience) {
        this.experience = experience;
        return this;
    }

    public VestigeRitualRecipeBuilder noNotification() {
        this.showNotification = false;
        return this;
    }

    public void save(RecipeOutput output, String name) {
        if (ingredients.isEmpty()) {
            throw new IllegalStateException("Ritual '" + name + "' has no ingredients");
        }

        VestigeRitualRecipe recipe = new VestigeRitualRecipe(
                new Recipe.CommonInfo(showNotification),
                List.copyOf(ingredients),
                result,
                experience);

        Identifier id = Vestigia.id(name);
        ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, id);
        output.accept(key, recipe, null);
    }
}
