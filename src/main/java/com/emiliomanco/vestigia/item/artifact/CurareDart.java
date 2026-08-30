package com.emiliomanco.vestigia.item.artifact;

import com.emiliomanco.vestigia.registry.ModEffects;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

public enum CurareDart implements StringRepresentable {

    POISON("poison", 0x4C7A34) {
        @Override
        public void applyTo(LivingEntity target) {
            target.addEffect(new MobEffectInstance(MobEffects.POISON, 160, 2));
        }
    },

    PARALYTIC("paralytic", 0x4B6584) {
        @Override
        public void applyTo(LivingEntity target) {
            target.addEffect(new MobEffectInstance(ModEffects.PARALYSIS, 120, 0));
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 120, 2));
        }
    },

    SLEEP("sleep", 0x6B5B95) {
        @Override
        public void applyTo(LivingEntity target) {
            if (target instanceof Mob mob) {
                mob.setTarget(null);
                mob.setLastHurtByMob(null);
                mob.getNavigation().stop();
            }
            target.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 400, 4));
        }
    },

    MARKER("marker", 0xC9A227) {
        @Override
        public void applyTo(LivingEntity target) {
            target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 1200, 0));
            target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 1200, 0));
        }
    };

    private final String name;
    private final int colour;

    CurareDart(String name, int colour) {
        this.name = name;
        this.colour = colour;
    }

    public abstract void applyTo(LivingEntity target);

    @Override
    public String getSerializedName() {
        return name;
    }

    public String id() {
        return name;
    }

    public int colour() {
        return colour;
    }
}
