package com.emiliomanco.vestigia.entity.projectile;

import com.emiliomanco.vestigia.config.VestigiaConfig;
import com.emiliomanco.vestigia.entity.StasisState;
import com.emiliomanco.vestigia.item.god.SunDiscOfIntiItem;
import com.emiliomanco.vestigia.registry.ModEntities;
import com.emiliomanco.vestigia.registry.ModItems;
import com.emiliomanco.vestigia.registry.ModSounds;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class ThrownSunDisc extends Projectile implements ItemSupplier, GeoEntity {

    private static final RawAnimation ACTIVE = RawAnimation.begin().thenLoop("active.disk");

    private static final double OUTBOUND_REACH = 24.0D;
    private static final double RETURN_ACCELERATION = 0.30D;
    private static final double CATCH_DISTANCE = 1.6D;
    private static final int MAX_LIFETIME_TICKS = 400;

    private static final double RICOCHET_SEEK_RANGE = 12.0D;
    private static final double RICOCHET_SEEK_BIAS = 0.92D;

    private static final int MAX_TERRAIN_BOUNCES = 6;

    private static final double MAX_STEP = 0.35D;

    private static final double CLEARANCE = 0.35D;

    private final Set<UUID> alreadyHit = new HashSet<>();
    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

    private Vec3 origin = Vec3.ZERO;
    private boolean returning;
    private int pierced;
    private int terrainBounces;
    private float damage;
    private boolean weakened;

    public ThrownSunDisc(EntityType<? extends ThrownSunDisc> type, Level level) {
        super(type, level);
    }

    public ThrownSunDisc(Level level, LivingEntity thrower, float damage, boolean weakened) {
        this(ModEntities.SUN_DISC.get(), level);
        setOwner(thrower);
        setPos(thrower.getX(), thrower.getEyeY() - 0.2D, thrower.getZ());
        this.origin = position();
        this.damage = damage;
        this.weakened = weakened;
    }

    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(ModItems.SUN_DISC_OF_INTI.get());
    }

    @Override
    protected double getDefaultGravity() {
        return 0.0D;
    }

    @Override
    public void tick() {
        super.tick();

        if (!(level() instanceof ServerLevel serverLevel)) {
            spawnTrail();
            setPos(getX() + getDeltaMovement().x, getY() + getDeltaMovement().y, getZ() + getDeltaMovement().z);
            return;
        }

        Entity owner = getOwner();
        if (owner == null || !owner.isAlive() || owner.level() != level()) {
            dropDisc(serverLevel);
            return;
        }

        if (!returning && (pierced >= VestigiaConfig.INTI_THROW_PIERCE.get()
                || terrainBounces >= MAX_TERRAIN_BOUNCES
                || position().distanceTo(origin) > OUTBOUND_REACH)) {
            returning = true;
        }

        if (returning) {
            steerHome(owner);
            if (distanceToSqr(owner) < CATCH_DISTANCE * CATCH_DISTANCE) {
                catchDisc(serverLevel, owner);
                return;
            }
        }

        sweep(serverLevel);
        faceTravel();
        spawnTrail();

        if (tickCount > MAX_LIFETIME_TICKS) {
            dropDisc(serverLevel);
        }
    }

    private void sweep(ServerLevel level) {
        Vec3 motion = getDeltaMovement();
        double distance = motion.length();
        if (distance < 1.0E-6) {
            return;
        }

        int steps = Math.max(1, Mth.ceil(distance / MAX_STEP));
        for (int step = 0; step < steps; step++) {
            Vec3 stepMotion = getDeltaMovement().scale(1.0D / steps);
            Vec3 from = position();
            Vec3 to = from.add(stepMotion);

            HitResult entityHit = ProjectileUtil.getEntityHitResult(
                    level, this, from, to,
                    getBoundingBox().expandTowards(stepMotion).inflate(0.3D),
                    this::canHitEntity);
            if (entityHit instanceof EntityHitResult hit) {
                setPos(to);
                onHitEntity(hit);
                if (isRemoved()) {
                    return;
                }
                continue;
            }

            if (!returning && bounceOffTerrain(level, from, to)) {
                continue;
            }

            setPos(to);
        }
    }

    private void faceTravel() {
        if (getDeltaMovement().lengthSqr() < 1.0E-6) {
            return;
        }
        updateRotation();
    }

    private void steerHome(Entity owner) {
        Vec3 toOwner = owner.position().add(0.0D, owner.getBbHeight() * 0.5D, 0.0D)
                .subtract(position()).normalize();
        setDeltaMovement(getDeltaMovement().add(toOwner.scale(RETURN_ACCELERATION)).scale(0.92D));
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        Entity target = hitResult.getEntity();
        if (!alreadyHit.add(target.getUUID())) {
            return;
        }
        pierced++;

        target.hurt(damageSources().thrown(this, getOwner()), damage);
        int ignite = VestigiaConfig.INTI_THROW_IGNITE_SECONDS.get();
        if (ignite > 0) {
            target.igniteForSeconds(ignite);
        }

        if (level() instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, target.blockPosition(), SoundEvents.SHIELD_BLOCK.value(),
                    SoundSource.PLAYERS, 0.9F, 1.7F);
            serverLevel.sendParticles(ParticleTypes.FLAME,
                    target.getX(), target.getY() + target.getBbHeight() * 0.5D, target.getZ(),
                    8, 0.3D, 0.3D, 0.3D, 0.02D);
        }

        ricochet(target);
    }

    private void ricochet(Entity struck) {
        Vec3 normal = position()
                .subtract(struck.position().add(0.0D, struck.getBbHeight() * 0.5D, 0.0D))
                .normalize();
        if (normal.lengthSqr() < 1.0E-6) {
            normal = getDeltaMovement().normalize().scale(-1.0D);
        }
        deflect(normal, true);
    }

    private boolean bounceOffTerrain(ServerLevel level, Vec3 from, Vec3 to) {
        BlockHitResult hit = level.clip(new ClipContext(
                from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
        if (hit.getType() != HitResult.Type.BLOCK) {
            return false;
        }

        terrainBounces++;

        Vec3 normal = Vec3.atLowerCornerOf(hit.getDirection().getUnitVec3i());
        setPos(hit.getLocation().add(normal.scale(CLEARANCE)));

        deflect(normal, false);

        level.playSound(null, blockPosition(), SoundEvents.SHIELD_BLOCK.value(),
                SoundSource.PLAYERS, 0.6F, 2.0F);
        level.sendParticles(ParticleTypes.CRIT, getX(), getY(), getZ(), 4, 0.1D, 0.1D, 0.1D, 0.05D);
        return true;
    }

    private void deflect(Vec3 normal, boolean seek) {
        double incomingSpeed = getDeltaMovement().length();
        if (incomingSpeed < 1.0E-4) {
            return;
        }
        double speed = incomingSpeed * VestigiaConfig.INTI_BOUNCE_RETENTION.get();

        Vec3 incoming = getDeltaMovement().normalize();
        Vec3 reflected = incoming.subtract(normal.scale(2.0D * incoming.dot(normal))).normalize();

        Vec3 heading = reflected;
        if (seek) {
            Entity next = nearestUnhitEnemy();
            if (next != null) {
                double launch = VestigiaConfig.INTI_THROW_SPEED.get();
                double slowness = Mth.clamp(1.0D - (incomingSpeed / launch), 0.0D, 1.0D);
                double bias = RICOCHET_SEEK_BIAS * (0.55D + 0.45D * slowness);

                Vec3 toNext = next.position().add(0.0D, next.getBbHeight() * 0.5D, 0.0D)
                        .subtract(position()).normalize();
                heading = reflected.scale(1.0D - bias).add(toNext.scale(bias)).normalize();
            }
        }

        setDeltaMovement(heading.scale(speed));
        hurtMarked = true;
    }

    private @Nullable Entity nearestUnhitEnemy() {
        Entity owner = getOwner();
        Entity best = null;
        double bestDistance = Double.MAX_VALUE;

        for (LivingEntity candidate : level().getEntitiesOfClass(LivingEntity.class,
                getBoundingBox().inflate(RICOCHET_SEEK_RANGE))) {
            if (candidate == owner || alreadyHit.contains(candidate.getUUID()) || !candidate.isAlive()) {
                continue;
            }
            double distance = distanceToSqr(candidate);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = candidate;
            }
        }
        return best;
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return super.canHitEntity(target)
                && target != getOwner()
                && target instanceof LivingEntity
                && !alreadyHit.contains(target.getUUID());
    }

    private void catchDisc(ServerLevel level, Entity owner) {
        level.playSound(null, blockPosition(), ModSounds.SUN_DISC_CATCH.get(),
                SoundSource.PLAYERS, 0.8F, 1.0F);
        ItemStack stack = getItem();
        if (owner instanceof Player player) {
            SunDiscOfIntiItem.clearThrownFlag(player);
            if (!player.getInventory().add(stack)) {
                player.drop(stack, false);
            }
        } else {
            spawnAtLocation(level, stack);
        }
        discard();
    }

    private void dropDisc(ServerLevel level) {
        if (getOwner() instanceof Player player) {
            SunDiscOfIntiItem.clearThrownFlag(player);
        }
        spawnAtLocation(level, getItem());
        discard();
    }

    private void spawnTrail() {
        if (level().isClientSide()) {
            level().addParticle(ParticleTypes.SMALL_FLAME, getX(), getY(), getZ(), 0.0D, 0.0D, 0.0D);
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        returning = input.getBooleanOr("Returning", false);
        pierced = input.getIntOr("Pierced", 0);
        terrainBounces = input.getIntOr("TerrainBounces", 0);
        damage = input.getFloatOr("Damage", (float) (double) VestigiaConfig.INTI_THROW_DAMAGE.get());
        weakened = input.getBooleanOr("Weakened", false);
        origin = new Vec3(
                input.getDoubleOr("OriginX", getX()),
                input.getDoubleOr("OriginY", getY()),
                input.getDoubleOr("OriginZ", getZ()));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("Returning", returning);
        output.putInt("Pierced", pierced);
        output.putInt("TerrainBounces", terrainBounces);
        output.putFloat("Damage", damage);
        output.putBoolean("Weakened", weakened);
        output.putDouble("OriginX", origin.x);
        output.putDouble("OriginY", origin.y);
        output.putDouble("OriginZ", origin.z);
    }

    public static double launchSpeed() {
        return VestigiaConfig.INTI_THROW_SPEED.get();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<ThrownSunDisc>("main", 0, state -> {
            state.setControllerSpeed(StasisState.animationSpeed(
                    this, VestigiaConfig.INTI_SPIN_SPEED.get().floatValue()));
            return state.setAndContinue(ACTIVE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }
}
