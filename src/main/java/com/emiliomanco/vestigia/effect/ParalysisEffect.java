package com.emiliomanco.vestigia.effect;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class ParalysisEffect extends MobEffect {

    public ParalysisEffect() {
        super(MobEffectCategory.HARMFUL, 0x4B6584);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int tickCount, int amplification) {
        return true;
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity mob, int amplification) {
        mob.setDeltaMovement(0.0D, Math.min(0.0D, mob.getDeltaMovement().y), 0.0D);
        mob.hurtMarked = true;

        if (mob instanceof net.minecraft.world.entity.Mob pathfinder) {
            pathfinder.getNavigation().stop();
        }
        return true;
    }
}
