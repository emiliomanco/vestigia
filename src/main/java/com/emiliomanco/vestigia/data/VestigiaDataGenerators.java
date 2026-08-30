package com.emiliomanco.vestigia.data;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.worldgen.ModBiomeModifiers;
import com.emiliomanco.vestigia.worldgen.ModWorldgen;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = Vestigia.MODID)
public final class VestigiaDataGenerators {
    private VestigiaDataGenerators() {}

    @SubscribeEvent
    static void onGatherData(GatherDataEvent.Client event) {
        event.createDatapackRegistryObjects(new RegistrySetBuilder()
                .add(Registries.STRUCTURE, ModWorldgen::bootstrapStructures)
                .add(Registries.STRUCTURE_SET, ModWorldgen::bootstrapStructureSets)
                .add(net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.BIOME_MODIFIERS,
                        ModBiomeModifiers::bootstrap));

        event.createProvider(ModLootTableProvider::create);
        event.createProvider(ModRecipeProvider.Runner::new);
        event.createProvider(ModBlockTagsProvider::new);
        event.createProvider(ModItemTagsProvider::new);
        event.createProvider(ModBiomeTagsProvider::new);
        event.createProvider(ModStructureTagsProvider::new);
    }
}
