package com.emiliomanco.vestigia.entity.ai;

import com.emiliomanco.vestigia.entity.projectile.ObsidianSpearProjectile;
import com.emiliomanco.vestigia.registry.ModItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;

public class SpearThrowGoal extends Goal {

    private static final double MIN_RANGE = 6.0D;
    private static final double MAX_RANGE = 14.0D;
    private static final int COOLDOWN_TICKS = 60;
    private static final float THROW_POWER = 1.6F;

    private final Mob mob;
    private int cooldown;

    public SpearThrowGoal(Mob mob) {
        this.mob = mob;
    }

    public static boolean armed(Mob mob) {
        return mob.getMainHandItem().is(ModItems.OBSIDIAN_SPEAR.get());
    }

    @Override
    public boolean canUse() {
        if (cooldown > 0) {
            cooldown--;
            return false;
        }
        if (!armed(mob)) {
            return false;
        }
        LivingEntity target = mob.getTarget();
        if (target == null || !target.isAlive()) {
            return false;
        }
        double distance = mob.distanceTo(target);
        return distance >= MIN_RANGE && distance <= MAX_RANGE && mob.hasLineOfSight(target);
    }

    @Override
    public boolean canContinueToUse() {
        return false;
    }

    @Override
    public void start() {
        LivingEntity target = mob.getTarget();
        if (target == null || !(mob.level() instanceof ServerLevel level)) {
            return;
        }

        ItemStack spear = mob.getMainHandItem().copy();
        mob.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);

        ObsidianSpearProjectile thrown = new ObsidianSpearProjectile(level, mob, spear);
        double dx = target.getX() - mob.getX();
        double dy = target.getY(0.3333333333333333D) - thrown.getY();
        double dz = target.getZ() - mob.getZ();
        double flat = Math.sqrt(dx * dx + dz * dz);
        thrown.shoot(dx, dy + flat * 0.15D, dz, THROW_POWER,
                14 - level.getDifficulty().getId() * 4);
        level.addFreshEntity(thrown);
        mob.playSound(SoundEvents.TRIDENT_THROW.value(), 1.0F, 1.0F);

        mob.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
        cooldown = COOLDOWN_TICKS;
    }
}
