package com.emiliomanco.vestigia.entity.projectile;

import com.emiliomanco.vestigia.entity.guardian.AncestralBoss;
import com.emiliomanco.vestigia.entity.guardian.AncestralGuardian;
import com.emiliomanco.vestigia.registry.ModEntities;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class ElementalBolt extends Projectile implements ItemSupplier {

    public enum Element {
        ICE,
        FIRE
    }

    private static final EntityDataAccessor<Integer> ELEMENT =
            SynchedEntityData.defineId(ElementalBolt.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Boolean> CHARGING =
            SynchedEntityData.defineId(ElementalBolt.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Boolean> GIANT =
            SynchedEntityData.defineId(ElementalBolt.class, EntityDataSerializers.BOOLEAN);

    private static final double BURST_RADIUS = 3.0D;
    private static final double GIANT_BURST_RADIUS = 5.0D;

    private static final double HOLD_DISTANCE = 2.0D;

    private static final double RING_RADIUS = 0.9D;

    private static final int MAX_CHARGE_TICKS = 600;

    private static final int ARM_TICKS = 5;

    private static final int MAX_FLIGHT_TICKS = 100;

    private float damage = 6.0F;
    private float launchSpeed = 1.5F;

    private int chargeTicks = -1;

    private float holdAngle;

    private int flightTicks;

    public ElementalBolt(EntityType<? extends ElementalBolt> type, Level level) {
        super(type, level);
    }

    public ElementalBolt(Level level, LivingEntity shooter, Element element, float damage) {
        this(ModEntities.ICE_SPIKE.get(), level);
        setOwner(shooter);
        setPos(shooter.getX(), shooter.getEyeY() - 0.2D, shooter.getZ());
        entityData.set(ELEMENT, element.ordinal());
        this.damage = damage;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(ELEMENT, Element.ICE.ordinal());
        builder.define(CHARGING, false);
        builder.define(GIANT, false);
    }

    public Element element() {
        return Element.values()[entityData.get(ELEMENT)];
    }

    public boolean isCharging() {
        return entityData.get(CHARGING);
    }

    public boolean isGiant() {
        return entityData.get(GIANT);
    }

    public void makeGiant() {
        entityData.set(GIANT, true);
        refreshDimensions();
    }

    public void hold(int ticks, float angle, float speed) {
        entityData.set(CHARGING, true);
        this.chargeTicks = ticks;
        this.holdAngle = angle;
        this.launchSpeed = speed;
        setNoGravity(true);
    }

    @Override
    public net.minecraft.world.entity.EntityDimensions getDimensions(net.minecraft.world.entity.Pose pose) {
        if (element() != Element.FIRE) {
            return super.getDimensions(pose);
        }
        float size = isGiant() ? 2.0F : 1.0F;
        return net.minecraft.world.entity.EntityDimensions.scalable(size, size);
    }

    @Override
    protected double getDefaultGravity() {
        return 0.0D;
    }

    @Override
    public void tick() {
        super.tick();
        if (isCharging()) {
            tickCharging();
            return;
        }
        tickFlying();
    }

    private void tickCharging() {
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        Entity owner = getOwner();
        if (!(owner instanceof LivingEntity bender) || !bender.isAlive()) {
            discard();
            return;
        }

        Vec3 look = bender.getLookAngle().normalize();
        Vec3 reference = Math.abs(look.y) < 0.99D ? new Vec3(0.0D, 1.0D, 0.0D) : new Vec3(1.0D, 0.0D, 0.0D);
        Vec3 right = look.cross(reference).normalize();
        Vec3 up = right.cross(look).normalize();

        double ring = isGiant() ? 0.0D : RING_RADIUS;
        Vec3 seat = bender.getEyePosition()
                .add(look.scale(HOLD_DISTANCE))
                .add(right.scale(Math.cos(holdAngle) * ring))
                .add(up.scale(Math.sin(holdAngle) * ring));
        setPos(seat.x, seat.y, seat.z);
        setDeltaMovement(Vec3.ZERO);

        emitTrail(serverLevel, tickCount, 0.1D, 0.0D);

        if (chargeTicks > 0 && --chargeTicks == 0) {
            release(bender);
            return;
        }
        if (tickCount > MAX_CHARGE_TICKS) {
            discard();
        }
    }

    public float holdAngle() {
        return holdAngle;
    }

    public boolean isArmed() {
        return tickCount > ARM_TICKS;
    }

    public void release(LivingEntity bender) {
        releaseToward(bender.getLookAngle());
    }

    public void releaseToward(Vec3 direction) {
        entityData.set(CHARGING, false);
        setNoGravity(false);
        chargeTicks = -1;
        flightTicks = 0;
        setDeltaMovement(direction.normalize().scale(launchSpeed));
        hurtMarked = true;

        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, blockPosition(), launchSound(), SoundSource.PLAYERS,
                    isGiant() ? 1.6F : 0.9F, isGiant() ? 0.6F : 1.0F);
        }
    }

    private void tickFlying() {
        HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hit.getType() != HitResult.Type.MISS) {
            hitTargetOrDeflectSelf(hit);
        }
        if (isRemoved()) {
            return;
        }

        setPos(getX() + getDeltaMovement().x, getY() + getDeltaMovement().y, getZ() + getDeltaMovement().z);
        applyGravity();

        if (level() instanceof ServerLevel serverLevel) {
            emitTrail(serverLevel, flightTicks, 0.05D, 0.01D);
            if (++flightTicks > MAX_FLIGHT_TICKS) {
                discard();
            }
        }
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return super.canHitEntity(target) && target != getOwner() && !isOwnGarrison(target);
    }

    private boolean isOwnGarrison(Entity target) {
        Entity owner = getOwner();
        boolean thrownByTemple = owner instanceof AncestralGuardian || owner instanceof AncestralBoss;
        return thrownByTemple
                && (target instanceof AncestralGuardian || target instanceof AncestralBoss);
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return false;
    }

    private int trailInterval() {
        return element() == Element.ICE ? 4 : 1;
    }

    private void emitTrail(ServerLevel level, int clock, double spread, double speed) {
        if (clock % trailInterval() != 0) {
            return;
        }
        if (isGiant()) {
            level.sendParticles(trail(), getX(), getY(), getZ(), 20, 0.9D, 0.9D, 0.9D, 0.01D);
            level.sendParticles(ParticleTypes.LARGE_SMOKE, getX(), getY(), getZ(), 3, 0.6D, 0.6D, 0.6D, 0.0D);
            return;
        }
        level.sendParticles(trail(), getX(), getY(), getZ(), 1, spread, spread, spread, speed);
    }

    private ParticleOptions trail() {
        return switch (element()) {
            case ICE -> ParticleTypes.SNOWFLAKE;
            case FIRE -> ParticleTypes.FLAME;
        };
    }

    private net.minecraft.sounds.SoundEvent launchSound() {
        return switch (element()) {
            case ICE -> SoundEvents.GLASS_BREAK;
            case FIRE -> SoundEvents.FIRECHARGE_USE;
        };
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        Entity target = hitResult.getEntity();
        target.hurt(damageSources().thrown(this, getOwner()), damage);

        switch (element()) {
            case ICE -> {
                if (target instanceof LivingEntity living) {
                    living.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 60, 1));
                }
            }
            case FIRE -> {
                target.igniteForSeconds(5);
                burst(serverLevel);
            }
        }
        discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        super.onHitBlock(hitResult);
        if (level() instanceof ServerLevel serverLevel) {
            if (element() != Element.ICE) {
                burst(serverLevel);
            }
            serverLevel.sendParticles(trail(), getX(), getY(), getZ(), 12, 0.3D, 0.3D, 0.3D, 0.05D);
        }
        discard();
    }

    private void burst(ServerLevel level) {
        double radius = isGiant() ? GIANT_BURST_RADIUS : BURST_RADIUS;
        for (Entity nearby : level.getEntities(this, new AABB(blockPosition()).inflate(radius))) {
            if (nearby == getOwner() || nearby instanceof ElementalBolt || isOwnGarrison(nearby)) {
                continue;
            }
            nearby.hurt(damageSources().thrown(this, getOwner()), damage * 0.6F);
            if (element() == Element.FIRE) {
                nearby.igniteForSeconds(5);
            }
        }
        level.sendParticles(ParticleTypes.EXPLOSION, getX(), getY(), getZ(), 1, 0.0D, 0.0D, 0.0D, 0.0D);
        level.playSound(null, blockPosition(), SoundEvents.GENERIC_EXPLODE.value(),
                SoundSource.PLAYERS, 1.0F, element() == Element.FIRE ? 1.2F : 0.6F);
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(switch (element()) {
            case ICE -> Items.ICE;
            case FIRE -> Items.FIRE_CHARGE;
        });
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putFloat("Damage", damage);
        output.putBoolean("Charging", isCharging());
        output.putFloat("HoldAngle", holdAngle);
        output.putFloat("LaunchSpeed", launchSpeed);
        output.putInt("ChargeTicks", chargeTicks);
        output.putBoolean("Giant", isGiant());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        damage = input.getFloatOr("Damage", 6.0F);
        holdAngle = input.getFloatOr("HoldAngle", 0.0F);
        launchSpeed = input.getFloatOr("LaunchSpeed", 1.5F);
        chargeTicks = input.getIntOr("ChargeTicks", -1);
        if (input.getBooleanOr("Giant", false)) {
            makeGiant();
        }
        if (input.getBooleanOr("Charging", false)) {
            entityData.set(CHARGING, true);
            setNoGravity(true);
        }
    }
}
