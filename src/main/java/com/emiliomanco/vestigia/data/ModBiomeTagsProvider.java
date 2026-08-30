package com.emiliomanco.vestigia.data;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.registry.ModBiomeTags;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;

public class ModBiomeTagsProvider extends KeyTagProvider<Biome> {

    public ModBiomeTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Registries.BIOME, registries, Vestigia.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        tag(ModBiomeTags.HAS_MAYA_PYRAMID)
                .add(Biomes.JUNGLE)
                .add(Biomes.BAMBOO_JUNGLE)
                .add(Biomes.SPARSE_JUNGLE);

        tag(ModBiomeTags.HAS_INCAN_LUNAR_STONE)
                .add(Biomes.MEADOW);

        tag(ModBiomeTags.HAS_INCAN_TEMPLE_OF_INTI)
                .add(Biomes.DESERT);
    }
}
