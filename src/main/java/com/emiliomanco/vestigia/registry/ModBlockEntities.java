package com.emiliomanco.vestigia.registry;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.block.entity.VestigeTableBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {
    private ModBlockEntities() {}

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Vestigia.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<VestigeTableBlockEntity>> VESTIGE_TABLE =
            BLOCK_ENTITIES.register("vestige_table",
                    () -> new BlockEntityType<>(VestigeTableBlockEntity::new, ModBlocks.VESTIGE_TABLE.get()));

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }
}
