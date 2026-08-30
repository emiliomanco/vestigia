package com.emiliomanco.vestigia.entity.projectile;

import com.emiliomanco.vestigia.item.artifact.CurareDart;
import com.emiliomanco.vestigia.registry.ModEntities;
import com.emiliomanco.vestigia.registry.ModItems;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.util.GeckoLibUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

public class CurareDartProjectile extends Projectile implements ItemSupplier, GeoEntity {

    private static final float IMPACT_DAMAGE = 4.0F;

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

    private static final EntityDataAccessor<Integer> DART_TYPE =
            SynchedEntityData.defineId(CurareDartProjectile.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Boolean> IN_GROUND =
            SynchedEntityData.defineId(CurareDartProjectile.class, EntityDataSerializers.BOOLEAN);

    private static final int FLIGHT_LIMIT_TICKS = 200;
    private static final int STUCK_LIFETIME_TICKS = 1200;

    private int stuckTicks;
    private @Nullable BlockState lastState;

    private boolean freeShot;

    public CurareDartProjectile(EntityType<? extends CurareDartProjectile> type, Level level) {
        super(type, level);
    }

    public CurareDartProjectile(Level level, LivingEntity shooter, CurareDart dart) {
        this(ModEntities.DART.get(), level);
        setOwner(shooter);
        setPos(shooter.getX(), shooter.getEyeY() - 0.1D, shooter.getZ());
        setDart(dart);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DART_TYPE, CurareDart.POISON.ordinal());
        builder.define(IN_GROUND, false);
    }

    public boolean inGround() {
        return entityData.get(IN_GROUND);
    }

    private void setInGround(boolean inGround) {
        entityData.set(IN_GROUND, inGround);
    }

    public void setFreeShot(boolean freeShot) {
        this.freeShot = freeShot;
    }

    public CurareDart dart() {
        return CurareDart.values()[entityData.get(DART_TYPE)];
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(ModItems.curareDart(dart()).get());
    }

    public void setDart(CurareDart dart) {
        entityData.set(DART_TYPE, dart.ordinal());
    }

    @Override
    protected double getDefaultGravity() {
        return 0.008D;
    }

    @Override
    public void tick() {
        super.tick();

        BlockPos here = blockPosition();
        BlockState state = level().getBlockState(here);
        if (!state.isAir()) {
            VoxelShape shape = state.getCollisionShape(level(), here);
            if (!shape.isEmpty()) {
                Vec3 where = position();
                for (AABB box : shape.toAabbs()) {
                    if (box.move(here).contains(where)) {
                        setDeltaMovement(Vec3.ZERO);
                        setInGround(true);
                        break;
                    }
                }
            }
        }

        if (inGround()) {
            tickEmbedded(state);
            return;
        }

        HitResult hit = net.minecraft.world.entity.projectile.ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hit.getType() != HitResult.Type.MISS) {
            setPos(hit.getLocation());
            hitTargetOrDeflectSelf(hit);
            if (inGround()) {
                return;
            }
        }

        setPos(getX() + getDeltaMovement().x, getY() + getDeltaMovement().y, getZ() + getDeltaMovement().z);
        applyGravity();
        applyEffectsFromBlocks();
        updateRotation();

        if (level() instanceof ServerLevel serverLevel && tickCount % 2 == 0) {
            serverLevel.sendParticles(ParticleTypes.CRIT, getX(), getY(), getZ(), 1, 0, 0, 0, 0);
        }
        if (tickCount > FLIGHT_LIMIT_TICKS) {
            discard();
        }
    }

    private void tickEmbedded(BlockState currentState) {
        if (level().isClientSide()) {
            return;
        }
        boolean blockGone = lastState != null && lastState != currentState
                && level().noCollision(new AABB(position(), position()).inflate(0.06D));
        if (blockGone) {
            setInGround(false);
            stuckTicks = 0;
            return;
        }
        if (++stuckTicks >= STUCK_LIFETIME_TICKS) {
            discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult hitResult) {
        lastState = level().getBlockState(hitResult.getBlockPos());
        super.onHitBlock(hitResult);
        if (level().isClientSide()) {
            return;
        }
        Vec3 movement = getDeltaMovement();
        Vec3 backOff = new Vec3(Math.signum(movement.x), Math.signum(movement.y), Math.signum(movement.z))
                .scale(0.05D);
        setPos(position().subtract(backOff));
        setDeltaMovement(Vec3.ZERO);
        setInGround(true);
        stuckTicks = 0;
    }

    @Override
    public void playerTouch(Player player) {
        if (level().isClientSide() || !inGround() || !tryPickup(player)) {
            return;
        }
        player.take(this, 1);
        discard();
    }

    private boolean tryPickup(Player player) {
        if (freeShot) {
            return player.hasInfiniteMaterials();
        }
        return player.getInventory().add(new ItemStack(ModItems.curareDart(dart()).get()));
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        Entity target = hitResult.getEntity();
        target.hurt(damageSources().thrown(this, getOwner()), IMPACT_DAMAGE);

        if (target instanceof LivingEntity living && !level().isClientSide()) {
            dart().applyTo(living);
        }
        if (!level().isClientSide()) {
            discard();
        }
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return super.canHitEntity(target) && target != getOwner();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        setDart(CurareDart.values()[Math.clamp(input.getIntOr("Dart", 0), 0, CurareDart.values().length - 1)]);
        setInGround(input.getBooleanOr("InGround", false));
        stuckTicks = input.getIntOr("StuckTicks", 0);
        freeShot = input.getBooleanOr("FreeShot", false);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("Dart", dart().ordinal());
        output.putBoolean("InGround", inGround());
        output.putInt("StuckTicks", stuckTicks);
        output.putBoolean("FreeShot", freeShot);
    }
}
