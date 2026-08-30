package com.emiliomanco.vestigia.registry;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.effect.BleedEffect;
import com.emiliomanco.vestigia.effect.ParalysisEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEffects {
    private ModEffects() {}

    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, Vestigia.MODID);

    public static final Holder<MobEffect> BLEED =
            EFFECTS.register("bleed", BleedEffect::new);

    public static final Holder<MobEffect> PARALYSIS =
            EFFECTS.register("paralysis", ParalysisEffect::new);

    public static void register(IEventBus modEventBus) {
        EFFECTS.register(modEventBus);
    }
}
