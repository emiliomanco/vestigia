package com.emiliomanco.vestigia.registry;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.block.VestigeTableBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    private ModBlocks() {}

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Vestigia.MODID);

    public static final DeferredBlock<VestigeTableBlock> VESTIGE_TABLE =
            BLOCKS.registerBlock("vestige_table", VestigeTableBlock::new, p -> p
                    .mapColor(MapColor.STONE)
                    .strength(4.0F, 12.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE)
                    .lightLevel(state -> 3)
                    .noOcclusion());

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
