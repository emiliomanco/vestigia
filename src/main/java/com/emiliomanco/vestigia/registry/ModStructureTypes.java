package com.emiliomanco.vestigia.registry;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.worldgen.structure.IncanLunarStoneStructure;
import com.emiliomanco.vestigia.worldgen.structure.IncanTempleOfIntiStructure;
import com.emiliomanco.vestigia.worldgen.structure.MayaPyramidStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModStructureTypes {
    private ModStructureTypes() {}

    public static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_TYPE, Vestigia.MODID);

    public static final DeferredHolder<StructureType<?>, StructureType<MayaPyramidStructure>> MAYA_PYRAMID =
            STRUCTURE_TYPES.register("maya_pyramid", () -> () -> MayaPyramidStructure.CODEC);

    public static final DeferredHolder<StructureType<?>, StructureType<IncanLunarStoneStructure>> INCAN_LUNAR_STONE =
            STRUCTURE_TYPES.register("incan_lunar_stone", () -> () -> IncanLunarStoneStructure.CODEC);

    public static final DeferredHolder<StructureType<?>, StructureType<IncanTempleOfIntiStructure>> INCAN_TEMPLE_OF_INTI =
            STRUCTURE_TYPES.register("incan_temple_of_inti", () -> () -> IncanTempleOfIntiStructure.CODEC);

    public static void register(IEventBus modEventBus) {
        STRUCTURE_TYPES.register(modEventBus);
    }
}
