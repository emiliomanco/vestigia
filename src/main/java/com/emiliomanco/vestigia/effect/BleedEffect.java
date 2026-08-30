package com.emiliomanco.vestigia.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class BleedEffect extends MobEffect {

    private static final float DAMAGE_PER_TICK = 2.0F;
    private static final int BASE_INTERVAL_TICKS = 20;

    public BleedEffect() {
        super(MobEffectCategory.HARMFUL, 0x8C1C13);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int tickCount, int amplification) {
        int interval = Math.max(5, BASE_INTERVAL_TICKS >> amplification);
        return tickCount % interval == 0;
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity mob, int amplification) {
        mob.hurtServer(level,
                level.damageSources().source(DamageTypes.GENERIC_KILL),
                DAMAGE_PER_TICK);
        return true;
    }
}
