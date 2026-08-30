package com.emiliomanco.vestigia.registry;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.item.god.pachamama.BendingBranch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModDataComponents {
    private ModDataComponents() {}

    public static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Vestigia.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<BendingBranch>> BENDING_BRANCH =
            COMPONENTS.registerComponentType("bending_branch", builder -> builder
                    .persistent(BendingBranch.CODEC)
                    .networkSynchronized(BendingBranch.STREAM_CODEC));

    public static void register(IEventBus modEventBus) {
        COMPONENTS.register(modEventBus);
    }
}
