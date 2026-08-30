package com.emiliomanco.vestigia.entity;

import com.emiliomanco.vestigia.config.VestigiaConfig;
import com.emiliomanco.vestigia.registry.ModEntities;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class AirScooter extends Entity implements com.geckolib.animatable.GeoEntity {

    private static final double SPEED = 0.9D;

    private static final double DRAG = 0.86D;

    private int lifetime;

    public AirScooter(EntityType<? extends AirScooter> type, Level level) {
        super(type, level);
        this.noPhysics = false;
    }

    public AirScooter(Level level, Player rider) {
        this(ModEntities.AIR_SCOOTER.get(), level);
        setPos(rider.getX(), rider.getY(), rider.getZ());
        this.lifetime = VestigiaConfig.PACHAMAMA_AIR_SCOOTER_DURATION.get();
    }

    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {}

    private final com.geckolib.animatable.instance.AnimatableInstanceCache animationCache =
            com.geckolib.util.GeckoLibUtil.createInstanceCache(this);

    private static final com.geckolib.animation.RawAnimation FLYING =
            com.geckolib.animation.RawAnimation.begin().thenLoop("flying");

    @Override
    public void registerControllers(com.geckolib.animatable.manager.AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new com.geckolib.animation.AnimationController<>("main", 0, state -> {
            state.setControllerSpeed(
                    com.emiliomanco.vestigia.entity.StasisState.animationSpeed(this));
            return state.setAndContinue(FLYING);
        }));
    }

    @Override
    public com.geckolib.animatable.instance.AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }

    @Override
    public boolean isNoGravity() {
        return true;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean hurtServer(ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float amount) {
        return false;
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity passenger, net.minecraft.world.entity.EntityDimensions dimensions, float scale) {
        return new Vec3(0.0D, dimensions.height() * 0.6D, 0.0D);
    }

    @Override
    public void tick() {
        super.tick();

        Player rider = rider();
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        if (rider == null || --lifetime <= 0) {
            pop(serverLevel);
            return;
        }

        Vec3 velocity = getDeltaMovement().scale(DRAG);
        double throttle = throttle(rider);
        if (throttle != 0.0D) {
            Vec3 heading = rider.getLookAngle().normalize().scale(SPEED * throttle);
            velocity = velocity.add(heading.scale(1.0D - DRAG));
        }
        setDeltaMovement(velocity);
        move(MoverType.SELF, getDeltaMovement());

        if (horizontalCollision) {
            setDeltaMovement(getDeltaMovement().multiply(0.2D, 1.0D, 0.2D));
        }

        setYRot(rider.getYRot());
        setXRot(rider.getXRot());

        serverLevel.sendParticles(ParticleTypes.CLOUD, getX(), getY(), getZ(),
                4, 0.35D, 0.15D, 0.35D, 0.02D);
    }

    private static double throttle(Player rider) {
        if (!(rider instanceof net.minecraft.server.level.ServerPlayer served)) {
            return 1.0D;
        }
        net.minecraft.world.entity.player.Input input = served.getLastClientInput();
        if (input.forward() == input.backward()) {
            return 0.0D;
        }
        return input.forward() ? 1.0D : -1.0D;
    }

    private @Nullable Player rider() {
        return getFirstPassenger() instanceof Player player ? player : null;
    }

    private void pop(ServerLevel level) {
        level.sendParticles(ParticleTypes.CLOUD, getX(), getY(), getZ(), 20, 0.5D, 0.5D, 0.5D, 0.1D);
        for (Entity passenger : getPassengers()) {
            passenger.resetFallDistance();
        }
        ejectPassengers();
        discard();
    }

    @Override
    public boolean causeFallDamage(double distance, float multiplier, net.minecraft.world.damagesource.DamageSource source) {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        lifetime = input.getIntOr("Lifetime", 0);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putInt("Lifetime", lifetime);
    }

}
