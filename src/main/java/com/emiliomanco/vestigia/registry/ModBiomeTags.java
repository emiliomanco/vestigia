package com.emiliomanco.vestigia.registry;

import com.emiliomanco.vestigia.Vestigia;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public final class ModBiomeTags {
    private ModBiomeTags() {}

    public static final TagKey<Biome> HAS_MAYA_PYRAMID = create("has_structure/maya_pyramid");

    public static final TagKey<Biome> HAS_INCAN_LUNAR_STONE = create("has_structure/incan_lunar_stone");

    public static final TagKey<Biome> HAS_INCAN_TEMPLE_OF_INTI = create("has_structure/incan_temple_of_inti");

    private static TagKey<Biome> create(String path) {
        return TagKey.create(Registries.BIOME, Vestigia.id(path));
    }
}
