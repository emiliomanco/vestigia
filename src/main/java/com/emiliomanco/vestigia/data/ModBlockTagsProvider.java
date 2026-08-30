package com.emiliomanco.vestigia.data;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.registry.ModBlocks;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

public final class ModBlockTagsProvider extends BlockTagsProvider {

    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Vestigia.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        var pickaxe = tag(BlockTags.MINEABLE_WITH_PICKAXE);
        var stoneTool = tag(BlockTags.NEEDS_STONE_TOOL);

        for (Block block : pickaxeMineable()) {
            pickaxe.add(block);
            stoneTool.add(block);
        }
    }

    private static Block[] pickaxeMineable() {
        return new Block[] {
                ModBlocks.VESTIGE_TABLE.get(),
        };
    }
}
