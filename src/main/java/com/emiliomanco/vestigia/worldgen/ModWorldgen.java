package com.emiliomanco.vestigia.worldgen;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.registry.ModBiomeTags;
import com.emiliomanco.vestigia.registry.ModEntities;
import com.emiliomanco.vestigia.worldgen.structure.IncanLunarStoneStructure;
import com.emiliomanco.vestigia.worldgen.structure.IncanTempleOfIntiStructure;
import com.emiliomanco.vestigia.worldgen.structure.MayaPyramidStructure;
import java.util.Map;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;

public final class ModWorldgen {
    private ModWorldgen() {}

    public static final ResourceKey<Structure> MAYA_PYRAMID = structure("maya_pyramid");
    public static final ResourceKey<StructureSet> MAYA_PYRAMID_SET = structureSet("maya_pyramid");

    public static final ResourceKey<Structure> INCAN_LUNAR_STONE = structure("incan_lunar_stone");
    public static final ResourceKey<StructureSet> INCAN_LUNAR_STONE_SET = structureSet("incan_lunar_stone");

    public static final ResourceKey<Structure> INCAN_TEMPLE_OF_INTI = structure("incan_temple_of_inti");
    public static final ResourceKey<StructureSet> INCAN_TEMPLE_OF_INTI_SET = structureSet("incan_temple_of_inti");

    private static ResourceKey<Structure> structure(String path) {
        return ResourceKey.create(Registries.STRUCTURE, Vestigia.id(path));
    }

    private static ResourceKey<StructureSet> structureSet(String path) {
        return ResourceKey.create(Registries.STRUCTURE_SET, Vestigia.id(path));
    }

    public static void bootstrapStructures(BootstrapContext<Structure> context) {
        HolderGetter<Biome> biomes = context.lookup(Registries.BIOME);

        context.register(MAYA_PYRAMID, new MayaPyramidStructure(
                new Structure.StructureSettings(
                        biomes.getOrThrow(ModBiomeTags.HAS_MAYA_PYRAMID),
                        Map.of(MobCategory.MONSTER, new StructureSpawnOverride(
                                StructureSpawnOverride.BoundingBoxType.STRUCTURE,
                                WeightedList.of(
                                        new Weighted<>(new MobSpawnSettings.SpawnerData(
                                                ModEntities.MAYAN_WARRIOR.get(), 1, 3), 5),
                                        new Weighted<>(new MobSpawnSettings.SpawnerData(
                                                ModEntities.MAYAN_SHAMAN.get(), 1, 1), 1)))),
                        GenerationStep.Decoration.SURFACE_STRUCTURES,
                        TerrainAdjustment.NONE)));

        context.register(INCAN_LUNAR_STONE, new IncanLunarStoneStructure(
                new Structure.StructureSettings(
                        biomes.getOrThrow(ModBiomeTags.HAS_INCAN_LUNAR_STONE),
                        Map.of(),
                        GenerationStep.Decoration.SURFACE_STRUCTURES,
                        TerrainAdjustment.BEARD_THIN)));

        context.register(INCAN_TEMPLE_OF_INTI, new IncanTempleOfIntiStructure(
                new Structure.StructureSettings(
                        biomes.getOrThrow(ModBiomeTags.HAS_INCAN_TEMPLE_OF_INTI),
                        Map.of(MobCategory.MONSTER, new StructureSpawnOverride(
                                StructureSpawnOverride.BoundingBoxType.STRUCTURE,
                                WeightedList.of(
                                        new Weighted<>(new MobSpawnSettings.SpawnerData(
                                                ModEntities.INCAN_WARRIOR.get(), 1, 3), 5),
                                        new Weighted<>(new MobSpawnSettings.SpawnerData(
                                                ModEntities.INCAN_PRIEST.get(), 1, 1), 1)))),
                        GenerationStep.Decoration.SURFACE_STRUCTURES,
                        TerrainAdjustment.NONE)));
    }

    public static void bootstrapStructureSets(BootstrapContext<StructureSet> context) {
        HolderGetter<Structure> structures = context.lookup(Registries.STRUCTURE);

        context.register(MAYA_PYRAMID_SET, new StructureSet(
                structures.getOrThrow(MAYA_PYRAMID),
                new RandomSpreadStructurePlacement(
                         32,
                         12,
                        RandomSpreadType.LINEAR,
                         8830471)));

        context.register(INCAN_LUNAR_STONE_SET, new StructureSet(
                structures.getOrThrow(INCAN_LUNAR_STONE),
                new RandomSpreadStructurePlacement(
                         48,
                         20,
                        RandomSpreadType.LINEAR,
                         5471903)));

        context.register(INCAN_TEMPLE_OF_INTI_SET, new StructureSet(
                structures.getOrThrow(INCAN_TEMPLE_OF_INTI),
                new RandomSpreadStructurePlacement(
                        Vec3i.ZERO,
                        StructurePlacement.FrequencyReductionMethod.DEFAULT,
                         0.35F,
                         1471822,
                        java.util.Optional.empty(),
                         64,
                         28,
                        RandomSpreadType.LINEAR)));
    }
}
