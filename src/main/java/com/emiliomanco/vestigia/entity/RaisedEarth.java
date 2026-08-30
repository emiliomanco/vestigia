package com.emiliomanco.vestigia.entity;

import com.emiliomanco.vestigia.mixin.FallingBlockEntityAccessor;
import com.emiliomanco.vestigia.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class RaisedEarth extends FallingBlockEntity {

    private static final EntityDataAccessor<Boolean> FLYING =
            SynchedEntityData.defineId(RaisedEarth.class, EntityDataSerializers.BOOLEAN);

    private static final int RISE_TICKS = 10;

    private static final double HOVER_HEIGHT = 2.1D;

    private static final double LAUNCH_SPEED = 1.35D;

    private static final double BURST_RADIUS = 4.0D;

    private static final int MAX_FLIGHT_TICKS = 100;

    private @Nullable BlockPos origin;
    private @Nullable LivingEntity bender;
    private int riseTicks;
    private int flightTicks;
    private float damage = 16.0F;

    public RaisedEarth(EntityType<? extends RaisedEarth> type, Level level) {
        super(type, level);
    }

    public static @Nullable RaisedEarth raise(ServerLevel level, LivingEntity bender, BlockPos from, float damage) {
        BlockState taken = level.getBlockState(from);
        if (taken.isAir()) {
            return null;
        }
        RaisedEarth boulder = new RaisedEarth(ModEntities.BLOCK_PROJECTILE.get(), level);
        ((FallingBlockEntityAccessor) boulder).vestigia$setBlockState(taken);
        boulder.bender = bender;
        boulder.origin = from.immutable();
        boulder.damage = damage;
        boulder.setPos(from.getX() + 0.5D, from.getY(), from.getZ() + 0.5D);
        boulder.setStartPos(from);

        level.setBlockAndUpdate(from, Blocks.AIR.defaultBlockState());
        level.playSound(null, from, SoundEvents.ROOTED_DIRT_BREAK, SoundSource.PLAYERS, 1.3F, 0.5F);
        return boulder;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FLYING, false);
    }

    public boolean isFlying() {
        return entityData.get(FLYING);
    }

    @Override
    public boolean isPickable() {
        return !isFlying();
    }

    @Override
    public boolean isAttackable() {
        return !isFlying();
    }

    @Override
    public boolean canBeCollidedWith(Entity by) {
        return !isFlying();
    }

    public void launchAlong(Vec3 direction) {
        if (!(level() instanceof ServerLevel level)) {
            return;
        }
        entityData.set(FLYING, true);
        setDeltaMovement(direction.normalize().scale(LAUNCH_SPEED));
        hurtMarked = true;
        level.playSound(null, blockPosition(), SoundEvents.ROOTED_DIRT_BREAK, SoundSource.PLAYERS, 1.4F, 0.6F);
    }

    public void skipRise() {
        riseTicks = RISE_TICKS;
    }

    @Override
    public boolean skipAttackInteraction(Entity attacker) {
        if (isFlying() || bender == null || riseTicks < RISE_TICKS) {
            return false;
        }
        if (!(level() instanceof ServerLevel level)) {
            return true;
        }
        entityData.set(FLYING, true);
        setDeltaMovement(bender.getLookAngle().normalize().scale(LAUNCH_SPEED));
        hurtMarked = true;
        level.playSound(null, blockPosition(), SoundEvents.ROOTED_DIRT_BREAK, SoundSource.PLAYERS, 1.4F, 0.7F);
        return true;
    }

    @Override
    public void tick() {
        baseTick();

        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        if (isFlying()) {
            tickFlying(serverLevel);
        } else {
            tickRising(serverLevel);
        }
    }

    private void tickRising(ServerLevel level) {
        if (origin == null || bender == null || !bender.isAlive()) {
            land(level);
            return;
        }
        if (riseTicks < RISE_TICKS) {
            riseTicks++;
            double progress = (double) riseTicks / RISE_TICKS;
            double eased = 1.0D - (1.0D - progress) * (1.0D - progress);
            setPos(origin.getX() + 0.5D, origin.getY() + eased * HOVER_HEIGHT, origin.getZ() + 0.5D);
            level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, getBlockState()),
                    getX(), origin.getY() + 0.5D, getZ(), 6, 0.4D, 0.2D, 0.4D, 0.02D);
        }
        setDeltaMovement(Vec3.ZERO);
    }

    private void tickFlying(ServerLevel level) {
        HitResult hit = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hit.getType() != HitResult.Type.MISS) {
            if (hit instanceof EntityHitResult entityHit) {
                entityHit.getEntity().hurt(damageSources().thrown(this, bender), damage);
            }
            land(level);
            return;
        }
        setPos(getX() + getDeltaMovement().x, getY() + getDeltaMovement().y, getZ() + getDeltaMovement().z);
        setDeltaMovement(getDeltaMovement().add(0.0D, -0.045D, 0.0D));

        level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, getBlockState()),
                getX(), getY(), getZ(), 2, 0.2D, 0.2D, 0.2D, 0.01D);

        if (++flightTicks > MAX_FLIGHT_TICKS) {
            land(level);
        }
    }

    private boolean canHitEntity(Entity target) {
        return target != bender && target.isAlive() && target.isPickable();
    }

    private void land(ServerLevel level) {
        BlockPos at = blockPosition();
        BlockState state = getBlockState();

        for (Entity nearby : level.getEntities(this, new AABB(at).inflate(BURST_RADIUS))) {
            if (nearby == bender) {
                continue;
            }
            nearby.hurt(damageSources().thrown(this, bender), damage);
        }

        if (level.getBlockState(at).canBeReplaced()) {
            level.setBlockAndUpdate(at, state);
        } else {
            Block.popResource(level, at, new ItemStack(state.getBlock()));
        }

        level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, state),
                getX(), getY(), getZ(), 40, 0.6D, 0.4D, 0.6D, 0.15D);
        level.playSound(null, at, SoundEvents.GENERIC_EXPLODE.value(), SoundSource.PLAYERS, 1.2F, 0.6F);
        discard();
    }
}
