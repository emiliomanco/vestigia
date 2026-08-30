package com.emiliomanco.vestigia.data;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.registry.ModStructureTags;
import com.emiliomanco.vestigia.worldgen.ModWorldgen;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import net.minecraft.world.level.levelgen.structure.Structure;

public class ModStructureTagsProvider extends KeyTagProvider<Structure> {

    public ModStructureTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Registries.STRUCTURE, registries, Vestigia.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        tag(ModStructureTags.VESTIGIA)
                .add(ModWorldgen.MAYA_PYRAMID)
                .add(ModWorldgen.INCAN_LUNAR_STONE)
                .add(ModWorldgen.INCAN_TEMPLE_OF_INTI);
    }
}
