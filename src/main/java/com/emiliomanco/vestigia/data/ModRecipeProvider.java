package com.emiliomanco.vestigia.data;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.registry.ModBlocks;
import com.emiliomanco.vestigia.registry.ModItemTags;
import com.emiliomanco.vestigia.item.god.pachamama.BendingBranch;
import com.emiliomanco.vestigia.registry.ModDataComponents;
import com.emiliomanco.vestigia.item.artifact.CurareDart;
import com.emiliomanco.vestigia.registry.ModItems;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Items;

public final class ModRecipeProvider extends RecipeProvider {

    private ModRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes() {
        shaped(RecipeCategory.DECORATIONS, ModBlocks.VESTIGE_TABLE.get())
                .define('S', Items.CHISELED_STONE_BRICKS)
                .define('G', Items.GOLD_INGOT)
                .define('F', ModItems.OBSIDIAN_FRAGMENT.get())
                .pattern("SGS")
                .pattern("GFG")
                .pattern("SGS")
                .unlockedBy("has_obsidian_fragment", has(ModItems.OBSIDIAN_FRAGMENT.get()))
                .save(output, ResourceKey.create(Registries.RECIPE, Vestigia.id("vestige_table")));

        shaped(RecipeCategory.MISC, ModItems.OFFERING.get(), 6)
                .define('G', Items.GOLD_INGOT)
                .define('W', Items.WHEAT)
                .pattern(" W ")
                .pattern("WGW")
                .pattern(" W ")
                .unlockedBy("has_gold_ingot", has(Items.GOLD_INGOT))
                .save(output, ResourceKey.create(Registries.RECIPE, Vestigia.id("offering")));

        craftingRecipes();
        ritualRecipes();
    }

    private static final int DARTS_PER_CRAFT = 16;

    private void craftingRecipes() {
        shapeless(RecipeCategory.MISC, ModItems.OBSIDIAN_FRAGMENT.get(), 4)
                .requires(Items.OBSIDIAN)
                .unlockedBy("has_obsidian", has(Items.OBSIDIAN))
                .save(output, ResourceKey.create(Registries.RECIPE, Vestigia.id("obsidian_fragment")));

        shaped(RecipeCategory.COMBAT, ModItems.MACUAHUITL.get())
                .define('M', ItemTags.PLANKS)
                .define('F', ModItems.OBSIDIAN_FRAGMENT.get())
                .define('P', Items.STICK)
                .pattern(" FM")
                .pattern("FMF")
                .pattern("PF ")
                .unlockedBy("has_obsidian_fragment", has(ModItems.OBSIDIAN_FRAGMENT.get()))
                .save(output, ResourceKey.create(Registries.RECIPE, Vestigia.id("macuahuitl")));

        shaped(RecipeCategory.COMBAT, ModItems.INCAN_CLUB.get())
                .define('P', Items.STICK)
                .define('A', Items.POLISHED_ANDESITE)
                .pattern(" AA")
                .pattern(" PA")
                .pattern("P  ")
                .unlockedBy("has_polished_andesite", has(Items.POLISHED_ANDESITE))
                .save(output, ResourceKey.create(Registries.RECIPE, Vestigia.id("incan_club")));

        shaped(RecipeCategory.COMBAT, ModItems.OBSIDIAN_SPEAR.get())
                .define('P', Items.STICK)
                .define('F', ModItems.OBSIDIAN_FRAGMENT.get())
                .define('S', Items.STRING)
                .pattern(" SF")
                .pattern(" PS")
                .pattern("P  ")
                .unlockedBy("has_obsidian_fragment", has(ModItems.OBSIDIAN_FRAGMENT.get()))
                .save(output, ResourceKey.create(Registries.RECIPE, Vestigia.id("obsidian_spear")));

        shaped(RecipeCategory.COMBAT, ModItems.BLOWGUN.get())
                .define('B', Items.BAMBOO)
                .pattern("  B")
                .pattern(" B ")
                .pattern("B  ")
                .unlockedBy("has_bamboo", has(Items.BAMBOO))
                .save(output, ResourceKey.create(Registries.RECIPE, Vestigia.id("blowgun")));

        dart(CurareDart.POISON, Items.SPIDER_EYE, "has_spider_eye");
        dart(CurareDart.MARKER, Items.GLOWSTONE_DUST, "has_glowstone_dust");
        dart(CurareDart.PARALYTIC, Items.CORNFLOWER, "has_cornflower");
        dart(CurareDart.SLEEP, Items.ALLIUM, "has_allium");
    }

    private void dart(CurareDart dart, ItemLike payload, String criterion) {
        shaped(RecipeCategory.COMBAT, ModItems.curareDart(dart).get(), DARTS_PER_CRAFT)
                .define('F', Items.FEATHER)
                .define('P', Items.STICK)
                .define('X', payload)
                .pattern("  F")
                .pattern(" P ")
                .pattern("X  ")
                .unlockedBy(criterion, has(payload))
                .save(output, ResourceKey.create(Registries.RECIPE, Vestigia.id(dart.id() + "_dart")));
    }

    private void ritualRecipes() {
        VestigeRitualRecipeBuilder.ritual(ModItems.SUN_DISC_OF_INTI)
                .ingredient(ModItems.PUNCHAO)
                .ingredient(Items.GOLD_INGOT)
                .ingredient(Items.GOLD_BLOCK)
                .ingredient(ModItems.OFFERING)
                .experience(20.0F)
                .save(output, "ritual/sun_disc_of_inti");

        VestigeRitualRecipeBuilder.ritual(ModItems.LUNAR_MIRROR)
                .ingredient(ModItems.LINE_TABLET)
                .ingredient(Items.IRON_INGOT)
                .ingredient(Items.AMETHYST_SHARD)
                .ingredient(Items.PRISMARINE_CRYSTALS)
                .experience(20.0F)
                .save(output, "ritual/lunar_mirror");

        VestigeRitualRecipeBuilder.ritual(ModItems.OTORONGO_HELM)
                .ingredient(ModItems.OTORONGO_FANG, 2)
                .ingredient(Items.GOLD_INGOT)
                .ingredient(ModItems.MACUAHUITL)
                .experience(12.0F)
                .save(output, "ritual/otorongo_helm");

        VestigeRitualRecipeBuilder.ritual(ModItems.CROWN_OF_PACHAMAMA)
                .ingredient(ModItems.OBSIDIAN_FRAGMENT)
                .ingredient(Items.EMERALD_BLOCK)
                .ingredient(Items.WHEAT)
                .experience(24.0F)
                .save(output, "ritual/corona_pachamama");

        VestigeRitualRecipeBuilder.ritual(ModItems.MANTLE_OF_KUKULKAN)
                .ingredient(ModItems.JADE_MASK)
                .ingredient(Items.FEATHER)
                .ingredient(Items.PHANTOM_MEMBRANE)
                .experience(24.0F)
                .save(output, "ritual/manto_kukulkan");

        VestigeRitualRecipeBuilder.ritual(ModItems.SUPREME_CROWN)
                .ingredient(crownOf(BendingBranch.EARTH))
                .ingredient(crownOf(BendingBranch.WATER))
                .ingredient(crownOf(BendingBranch.AIR))
                .ingredient(crownOf(BendingBranch.FIRE))
                .experience(40.0F)
                .save(output, "ritual/supreme_crown");
    }

    private static Ingredient crownOf(BendingBranch branch) {
        return DataComponentIngredient.of(true, ModDataComponents.BENDING_BRANCH.get(), branch,
                ModItems.CROWN_OF_PACHAMAMA.get());
    }

    public static final class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new ModRecipeProvider(registries, output);
        }

        @Override
        public String getName() {
            return "Vestigia Recipes";
        }
    }
}
