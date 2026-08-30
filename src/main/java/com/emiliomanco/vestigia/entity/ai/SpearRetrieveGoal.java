package com.emiliomanco.vestigia.entity.ai;

import com.emiliomanco.vestigia.entity.projectile.ObsidianSpearProjectile;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

public class SpearRetrieveGoal extends Goal {

    private static final double SEARCH_RADIUS = 20.0D;
    private static final double PICKUP_RANGE_SQR = 2.25D;
    private static final double SPEED = 1.1D;

    private final Mob mob;
    private @Nullable ObsidianSpearProjectile target;

    public SpearRetrieveGoal(Mob mob) {
        this.mob = mob;
        setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (SpearThrowGoal.armed(mob) || !mob.getMainHandItem().isEmpty()) {
            return false;
        }
        target = nearestOwnSpear();
        return target != null;
    }

    @Override
    public boolean canContinueToUse() {
        return target != null && target.isAlive() && !SpearThrowGoal.armed(mob);
    }

    @Override
    public void stop() {
        target = null;
        mob.getNavigation().stop();
    }

    @Override
    public void tick() {
        if (target == null) {
            return;
        }
        mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
        if (mob.distanceToSqr(target) > PICKUP_RANGE_SQR) {
            mob.getNavigation().moveTo(target, SPEED);
            return;
        }
        ItemStack recovered = target.getPickupItemStackOrigin().copy();
        mob.setItemSlot(EquipmentSlot.MAINHAND, recovered);
        target.discard();
        target = null;
    }

    private @Nullable ObsidianSpearProjectile nearestOwnSpear() {
        AABB search = mob.getBoundingBox().inflate(SEARCH_RADIUS);
        List<ObsidianSpearProjectile> spears = mob.level().getEntitiesOfClass(
                ObsidianSpearProjectile.class, search, spear -> spear.isAlive() && spear.getOwner() == mob);
        ObsidianSpearProjectile nearest = null;
        double best = Double.MAX_VALUE;
        for (ObsidianSpearProjectile spear : spears) {
            double distance = mob.distanceToSqr(spear);
            if (distance < best) {
                best = distance;
                nearest = spear;
            }
        }
        return nearest;
    }
}
