package com.emiliomanco.vestigia.worldgen;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.registry.ModEntities;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ModBiomeModifiers {
    private ModBiomeModifiers() {}

    public static final ResourceKey<BiomeModifier> JAGUARS_IN_JUNGLE =
            ResourceKey.create(NeoForgeRegistries.Keys.BIOME_MODIFIERS, Vestigia.id("jaguars_in_jungle"));

    private static final int WEIGHT = 8;
    private static final int MIN_GROUP = 1;
    private static final int MAX_GROUP = 2;

    public static void bootstrap(BootstrapContext<BiomeModifier> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);

        context.register(JAGUARS_IN_JUNGLE, BiomeModifiers.AddSpawnsBiomeModifier.singleSpawn(
                biomes.getOrThrow(BiomeTags.IS_JUNGLE),
                new Weighted<>(new MobSpawnSettings.SpawnerData(
                        ModEntities.JAGUAR.get(), MIN_GROUP, MAX_GROUP), WEIGHT)));
    }
}
