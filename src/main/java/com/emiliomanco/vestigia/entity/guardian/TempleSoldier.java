package com.emiliomanco.vestigia.entity.guardian;

import com.emiliomanco.vestigia.Civilization;
import com.emiliomanco.vestigia.entity.ai.SpearRetrieveGoal;
import com.emiliomanco.vestigia.entity.ai.SpearThrowGoal;
import com.emiliomanco.vestigia.entity.projectile.CurareDartProjectile;
import com.emiliomanco.vestigia.item.artifact.CurareDart;
import com.emiliomanco.vestigia.registry.ModItems;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;

public abstract class TempleSoldier extends AncestralGuardian implements RangedAttackMob {

    private static final double LEAP_RANGE_SQR = 100.0D;
    private static final double LEAP_MIN_RANGE_SQR = 16.0D;
    private static final int LEAP_COOLDOWN_TICKS = 100;
    private static final double LEAP_POWER = 0.72D;
    private static final double LEAP_LIFT = 0.44D;

    private static final int BLOWGUN_INTERVAL_TICKS = 30;
    private static final float BLOWGUN_RANGE = 12.0F;
    private static final int SHOOT_ANIM_TICKS = 10;

    private static final RawAnimation SHOOT = RawAnimation.begin().thenPlay("shoot_blowgun");
    private static final String SHOOT_CONTROLLER = "shoot";

    private static final EntityDataAccessor<Integer> SHOOTING_TICKS =
            SynchedEntityData.defineId(TempleSoldier.class, EntityDataSerializers.INT);

    private int leapCooldown;

    protected TempleSoldier(EntityType<? extends TempleSoldier> type, Level level, Civilization civilization) {
        super(type, level, civilization);
    }

    protected abstract ItemStack chooseWeapon(RandomSource random);

    protected void onMeleeHit(ServerLevel level, LivingEntity target) {
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new RangedBowAttackGoal<>(this, 1.0D, 20, 15.0F));
        goalSelector.addGoal(1, new SpearThrowGoal(this));
        goalSelector.addGoal(1, new SpearRetrieveGoal(this));
        goalSelector.addGoal(1, new RangedAttackGoal(this, 1.0D, BLOWGUN_INTERVAL_TICKS, BLOWGUN_RANGE) {
            @Override
            public boolean canUse() {
                return hasBlowgun() && super.canUse();
            }

            @Override
            public boolean canContinueToUse() {
                return hasBlowgun() && super.canContinueToUse();
            }
        });
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(SHOOTING_TICKS, 0);
    }

    public boolean isShooting() {
        return entityData.get(SHOOTING_TICKS) > 0;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<TempleSoldier>(SHOOT_CONTROLLER, 0, state -> PlayState.STOP)
                .triggerableAnim(SHOOT_CONTROLLER, SHOOT));
        registerSpearControllers(controllers);
    }

    @Override
    public boolean armsDriven() {
        return isShooting() || super.armsDriven();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        EntitySpawnReason reason, SpawnGroupData data) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, data);
        setItemSlot(EquipmentSlot.MAINHAND, chooseWeapon(level.getRandom()));
        setDropChance(EquipmentSlot.MAINHAND, 0.05F);
        return result;
    }

    @Override
    public void performRangedAttack(LivingEntity target, float power) {
        if (hasBlowgun()) {
            blowDart(target);
            return;
        }
        shootArrow(target, power);
    }

    private void blowDart(LivingEntity target) {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        CurareDartProjectile dart = new CurareDartProjectile(level(), this, CurareDart.POISON);
        dart.setFreeShot(true);
        double dx = target.getX() - getX();
        double dy = target.getY(0.3333333333333333D) - dart.getY();
        double dz = target.getZ() - getZ();
        double flat = Math.sqrt(dx * dx + dz * dz);
        dart.shoot(dx, dy + flat * 0.1D, dz, 2.4F, 12 - serverLevel.getDifficulty().getId() * 3);
        serverLevel.addFreshEntity(dart);

        entityData.set(SHOOTING_TICKS, SHOOT_ANIM_TICKS);
        triggerAnim(SHOOT_CONTROLLER, SHOOT_CONTROLLER);
    }

    private void shootArrow(LivingEntity target, float power) {
        ItemStack bow = getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, Items.BOW));
        ItemStack ammo = getProjectile(bow);
        AbstractArrow arrow = ProjectileUtil.getMobArrow(this, ammo, power, bow);
        double dx = target.getX() - getX();
        double dy = target.getY(0.3333333333333333D) - arrow.getY();
        double dz = target.getZ() - getZ();
        double flat = Math.sqrt(dx * dx + dz * dz);
        if (level() instanceof ServerLevel serverLevel) {
            Projectile.spawnProjectileUsingShoot(arrow, serverLevel, ammo, dx, dy + flat * 0.2D, dz,
                    1.6F, 14 - serverLevel.getDifficulty().getId() * 4);
        }
        playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (getRandom().nextFloat() * 0.4F + 0.8F));
    }

    private boolean isRanged() {
        return getMainHandItem().getItem() instanceof BowItem || hasBlowgun();
    }

    private boolean isDisarmed() {
        return getMainHandItem().isEmpty();
    }

    private boolean hasBlowgun() {
        return getMainHandItem().is(ModItems.BLOWGUN.get());
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (level().isClientSide()) {
            return;
        }
        int shooting = entityData.get(SHOOTING_TICKS);
        if (shooting > 0) {
            entityData.set(SHOOTING_TICKS, shooting - 1);
        }
        if (leapCooldown > 0) {
            leapCooldown--;
            return;
        }
        LivingEntity target = getTarget();
        if (target == null || !onGround() || isRanged() || isDisarmed()) {
            return;
        }

        double distanceSqr = distanceToSqr(target);
        if (distanceSqr > LEAP_RANGE_SQR || distanceSqr < LEAP_MIN_RANGE_SQR || !hasLineOfSight(target)) {
            return;
        }

        Vec3 toTarget = new Vec3(target.getX() - getX(), 0.0D, target.getZ() - getZ()).normalize();
        setDeltaMovement(toTarget.scale(LEAP_POWER).add(0.0D, LEAP_LIFT, 0.0D));
        hurtMarked = true;
        leapCooldown = LEAP_COOLDOWN_TICKS;
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        boolean hit = super.doHurtTarget(level, target);
        if (hit) {
            swingSpear();
        }
        if (hit && target instanceof LivingEntity living) {
            onMeleeHit(level, living);
        }
        return hit;
    }
}
