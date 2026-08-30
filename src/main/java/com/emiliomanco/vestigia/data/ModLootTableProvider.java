package com.emiliomanco.vestigia.data;

import com.emiliomanco.vestigia.registry.ModBlocks;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

public final class ModLootTableProvider {
    private ModLootTableProvider() {}

    public static LootTableProvider create(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        return new LootTableProvider(
                output,
                Set.of(),
                List.of(
                        new LootTableProvider.SubProviderEntry(BlockLoot::new, LootContextParamSets.BLOCK),
                        new LootTableProvider.SubProviderEntry(ModEntityLootProvider::new, LootContextParamSets.ENTITY),
                        new LootTableProvider.SubProviderEntry(ModChestLootProvider::new, LootContextParamSets.CHEST)),
                registries);
    }

    private static final class BlockLoot extends BlockLootSubProvider {
        private BlockLoot(HolderLookup.Provider registries) {
            super(Set.of(), FeatureFlags.DEFAULT_FLAGS, registries);
        }

        @Override
        protected void generate() {
            ModBlocks.BLOCKS.getEntries().forEach(holder -> dropSelf(holder.value()));
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return ModBlocks.BLOCKS.getEntries().stream().map(holder -> (Block) holder.value()).toList();
        }
    }
}
