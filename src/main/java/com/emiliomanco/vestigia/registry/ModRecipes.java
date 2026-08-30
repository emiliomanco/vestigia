package com.emiliomanco.vestigia.registry;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.ritual.VestigeRitualRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModRecipes {
    private ModRecipes() {}

    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, Vestigia.MODID);

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Vestigia.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<VestigeRitualRecipe>> VESTIGE_RITUAL_TYPE =
            TYPES.register("vestige_ritual", id -> RecipeType.<VestigeRitualRecipe>simple(id));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<VestigeRitualRecipe>> VESTIGE_RITUAL_SERIALIZER =
            SERIALIZERS.register("vestige_ritual",
                    () -> new RecipeSerializer<>(VestigeRitualRecipe.MAP_CODEC, VestigeRitualRecipe.STREAM_CODEC));

    public static void register(IEventBus modEventBus) {
        TYPES.register(modEventBus);
        SERIALIZERS.register(modEventBus);
    }
}
