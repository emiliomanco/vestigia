package com.emiliomanco.vestigia.entity.animal;

import com.emiliomanco.vestigia.config.VestigiaConfig;
import com.emiliomanco.vestigia.entity.PinnedState;
import com.emiliomanco.vestigia.entity.StasisState;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.util.GeckoLibUtil;
import java.util.EnumSet;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import com.emiliomanco.vestigia.registry.ModItems;
import com.emiliomanco.vestigia.registry.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class Jaguar extends Animal implements GeoEntity, NeutralMob {

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("run");
    private static final RawAnimation BITE = RawAnimation.begin().thenPlay("run_attack");

    private static final RawAnimation YAWN = RawAnimation.begin().thenPlay("yawn").thenLoop("idle");

    private static final RawAnimation POUNCE = RawAnimation.begin()
            .thenPlay("jump_attack")
            .thenLoop("jump_attack_WhilePinned");

    private static final EntityDataAccessor<Byte> DATA_FLAGS =
            SynchedEntityData.defineId(Jaguar.class, EntityDataSerializers.BYTE);

    private static final byte FLAG_POUNCING = 1;
    private static final byte FLAG_HUNTING = 2;
    private static final byte FLAG_MOVING = 4;
    private static final byte FLAG_BITING = 8;
    private static final byte FLAG_YAWNING = 16;

    private static final float YAWN_CHANCE = 0.04F;

    private static final int YAWN_TICKS = 50;

    private static final double MOVING_THRESHOLD_SQR = 1.0E-4D;

    private static final int BITE_TICKS = 15;

    private static final EntityDataAccessor<Integer> DATA_PINNED_ID =
            SynchedEntityData.defineId(Jaguar.class, EntityDataSerializers.INT);

    private static final double PROVOKE_RANGE = 6.0D;

    private static final UniformInt ANGER_DURATION = TimeUtil.rangeOfSeconds(20, 39);

    private static final int LEAP_TIMEOUT_TICKS = 30;

    private static final double LEAP_HOMING = 0.35D;

    private static final double CONTACT_MARGIN = 0.6D;

    private static final double LATE_CATCH_DISTANCE = 3.0D;

    private static final double HEAD_REACH = 0.9D;

    private static final double PIN_BREAK_DISTANCE = 3.0D;

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

    private long angerEndTime;
    private @Nullable EntityReference<LivingEntity> angerTarget;

    private @Nullable LivingEntity pinnedVictim;
    private int pounceCooldown;
    private int leapTicks;
    private int pinTicks;
    private int struggleHits;

    private float pinYaw;

    private int lastSyncedPinnedId;

    private int biteTicks;

    private int yawnTicks;

    public Jaguar(EntityType<? extends Jaguar> type, Level level) {
        super(type, level);

        setPathfindingMalus(PathType.LEAVES, 0.0F);
        setPathfindingMalus(PathType.COCOA, 0.0F);
        setPathfindingMalus(PathType.WATER, 4.0F);
        setPathfindingMalus(PathType.WATER_BORDER, 0.0F);
        setPathfindingMalus(PathType.LAVA, -1.0F);
        setPathfindingMalus(PathType.DAMAGING, 16.0F);
        setPathfindingMalus(PathType.FIRE, 16.0F);
    }

    @Override
    protected PathNavigation createNavigation(Level level) {
        GroundPathNavigation navigation = new GroundPathNavigation(this, level);
        navigation.setCanFloat(true);
        navigation.setCanOpenDoors(false);
        navigation.setMaxVisitedNodesMultiplier(3.0F);
        return navigation;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Animal.createAnimalAttributes()
                .add(Attributes.MAX_HEALTH, 30.0D)
                .add(Attributes.ATTACK_DAMAGE, 7.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.375D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.0D)
                .add(Attributes.FOLLOW_RANGE, 20.0D)
                .add(Attributes.STEP_HEIGHT, 1.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_FLAGS, (byte) 0);
        builder.define(DATA_PINNED_ID, 0);
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, net.minecraft.world.entity.Entity target) {
        boolean hurt = super.doHurtTarget(level, target);
        if (hurt) {
            biteTicks = BITE_TICKS;
            playSound(ModSounds.JAGUAR_ATTACK.get(), 1.0F, 1.0F);
        }
        return hurt;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> accessor) {
        super.onSyncedDataUpdated(accessor);
        if (!DATA_PINNED_ID.equals(accessor) || !level().isClientSide()) {
            return;
        }
        int now = entityData.get(DATA_PINNED_ID);
        if (lastSyncedPinnedId != 0 && lastSyncedPinnedId != now) {
            markPinnedById(lastSyncedPinnedId, false);
        }
        if (now != 0) {
            markPinnedById(now, true);
        }
        lastSyncedPinnedId = now;
    }

    @Override
    public void onRemovedFromLevel() {
        if (lastSyncedPinnedId != 0) {
            markPinnedById(lastSyncedPinnedId, false);
            lastSyncedPinnedId = 0;
        }
        if (pinnedVictim != null) {
            PinnedState.setPinned(pinnedVictim, false);
            pinnedVictim = null;
        }
        super.onRemovedFromLevel();
    }

    private void markPinnedById(int entityId, boolean pinned) {
        if (level().getEntity(entityId) instanceof Player player) {
            PinnedState.setPinned(player, pinned);
        }
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new PounceGoal());
        goalSelector.addGoal(2, new LeapAtTargetGoal(this, 0.4F));
        goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.4D, true));
        goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.65D));
        goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 10.0F));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        targetSelector.addGoal(1, new HurtByTargetGoal(this).setAlertOthers());
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false,
                (entity, level) -> isAngryAt((LivingEntity) entity, level)));
        targetSelector.addGoal(3, new ProvocationGoal());
        targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, Animal.class, 12, true, false,
                (entity, level) -> entity instanceof Chicken || entity instanceof Rabbit || entity instanceof Pig));
        targetSelector.addGoal(5, new ResetUniversalAngerTargetGoal<>(this, true));
    }

    private class ProvocationGoal extends Goal {
        @Override
        public boolean canUse() {
            return getTarget() == null && !isAngry() && level() instanceof ServerLevel;
        }

        @Override
        public void start() {
            Player nearest = level().getNearestPlayer(Jaguar.this, PROVOKE_RANGE);
            if (nearest == null || nearest.isCreative() || nearest.isSpectator()) {
                if (isNight()) {
                    nearest = level().getNearestPlayer(Jaguar.this, getAttributeValue(Attributes.FOLLOW_RANGE));
                }
                if (nearest == null || nearest.isCreative() || nearest.isSpectator()) {
                    return;
                }
            }
            setTarget(nearest);
            setPersistentAngerTarget(EntityReference.of(nearest));
            startPersistentAngerTimer();
        }
    }

    private boolean isNight() {
        return Math.floorMod(level().getOverworldClockTime(), 24000L) >= 12000L;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!(level() instanceof ServerLevel serverLevel)) {
            return;
        }
        updatePersistentAnger(serverLevel, true);
        setFlag(FLAG_HUNTING, getTarget() != null);

        double travelled = new Vec3(getX() - xo, 0.0D, getZ() - zo).lengthSqr();
        setFlag(FLAG_MOVING, travelled > MOVING_THRESHOLD_SQR);

        if (biteTicks > 0) {
            biteTicks--;
        }
        setFlag(FLAG_BITING, biteTicks > 0);

        if (yawnTicks > 0) {
            yawnTicks--;
        } else if (getTarget() == null && !hasFlag(FLAG_MOVING) && !isPouncing()
                && random.nextFloat() < YAWN_CHANCE / 20.0F) {
            yawnTicks = YAWN_TICKS;
        }
        setFlag(FLAG_YAWNING, yawnTicks > 0);

        if (pounceCooldown > 0) {
            pounceCooldown--;
        }
        if (isPouncing()) {
            advanceTakedown(serverLevel);
        }
    }

    private class PounceGoal extends Goal {
        PounceGoal() {
            setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK, Flag.JUMP));
        }

        @Override
        public boolean canUse() {
            if (!VestigiaConfig.JAGUAR_POUNCE_ENABLED.get() || pounceCooldown > 0
                    || !onGround() || isPassenger() || isInWater()) {
                return false;
            }
            LivingEntity target = getTarget();
            if (target == null || !target.isAlive() || !hasLineOfSight(target)) {
                return false;
            }
            double min = VestigiaConfig.JAGUAR_POUNCE_MIN_RANGE.get();
            double max = VestigiaConfig.JAGUAR_POUNCE_RANGE.get();
            double distanceSqr = distanceToSqr(target);
            return distanceSqr >= min * min && distanceSqr <= max * max;
        }

        @Override
        public boolean canContinueToUse() {
            return isPouncing();
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void start() {
            LivingEntity target = getTarget();
            if (target == null) {
                return;
            }
            getNavigation().stop();
            pinnedVictim = null;
            leapTicks = 0;
            pinTicks = 0;
            struggleHits = 0;
            setFlag(FLAG_POUNCING, true);
            launchAt(target);
        }

        @Override
        public void stop() {
            if (isPouncing()) {
                endTakedown();
            }
        }
    }

    private void launchAt(LivingEntity target) {
        Vec3 flat = new Vec3(target.getX() - getX(), 0.0D, target.getZ() - getZ());
        double horizontal = flat.length();
        double push = Mth.clamp(horizontal * 0.17D, 0.55D, 1.45D);
        Vec3 launch = horizontal < 1.0E-4D ? Vec3.ZERO : flat.scale(push / horizontal);
        setDeltaMovement(launch.add(0.0D, 0.60D, 0.0D));
        hurtMarked = true;
        playSound(ModSounds.JAGUAR_POUNCE.get(), 1.2F, 1.0F);
    }

    private void advanceTakedown(ServerLevel level) {
        if (pinnedVictim == null) {
            advanceLeap();
        } else {
            advancePin(level);
        }
    }

    private void advanceLeap() {
        leapTicks++;
        LivingEntity target = getTarget();
        if (target == null || !target.isAlive()) {
            endTakedown();
            return;
        }
        getLookControl().setLookAt(target, 40.0F, 40.0F);

        steerTowards(target);

        if (getBoundingBox().inflate(CONTACT_MARGIN).intersects(target.getBoundingBox())) {
            beginPin(target);
            return;
        }

        boolean over = leapTicks > LEAP_TIMEOUT_TICKS || (leapTicks > 3 && onGround());
        if (!over) {
            return;
        }
        if (distanceToSqr(target) <= LATE_CATCH_DISTANCE * LATE_CATCH_DISTANCE && hasLineOfSight(target)) {
            beginPin(target);
        } else {
            endTakedown();
        }
    }

    private void steerTowards(LivingEntity target) {
        Vec3 velocity = getDeltaMovement();
        Vec3 flat = new Vec3(velocity.x, 0.0D, velocity.z);
        double speed = flat.length();
        Vec3 toTarget = new Vec3(target.getX() - getX(), 0.0D, target.getZ() - getZ());
        if (speed < 1.0E-4D || toTarget.lengthSqr() < 1.0E-6D) {
            return;
        }
        Vec3 steered = flat.scale(1.0D - LEAP_HOMING)
                .add(toTarget.normalize().scale(speed * LEAP_HOMING));
        if (steered.lengthSqr() < 1.0E-8D) {
            return;
        }
        Vec3 aimed = steered.normalize().scale(speed);
        setDeltaMovement(aimed.x, velocity.y, aimed.z);
        hurtMarked = true;
    }

    private void beginPin(LivingEntity victim) {
        if (victim instanceof Player player && (player.isCreative() || player.isSpectator())) {
            endTakedown();
            return;
        }
        pinnedVictim = victim;
        pinTicks = 0;
        struggleHits = 0;

        PinnedState.setPinned(victim, true);
        entityData.set(DATA_PINNED_ID, victim.getId());

        pinYaw = (float) (Mth.atan2(victim.getZ() - getZ(), victim.getX() - getX()) * (180.0D / Math.PI))
                - 90.0F;

        setDeltaMovement(Vec3.ZERO);
        victim.setDeltaMovement(victim.getDeltaMovement().x * 0.2D, -0.5D, victim.getDeltaMovement().z * 0.2D);
        victim.hurtMarked = true;

        if (level() instanceof ServerLevel serverLevel) {
            maul(serverLevel, victim);
        }
    }

    private void advancePin(ServerLevel level) {
        LivingEntity victim = pinnedVictim;
        pinTicks++;

        if (victim == null || !victim.isAlive() || victim.isRemoved() || victim.level() != level
                || victim.isSpectator()
                || pinTicks > VestigiaConfig.JAGUAR_PIN_MAX_TICKS.get()
                || distanceToSqr(victim) > PIN_BREAK_DISTANCE * PIN_BREAK_DISTANCE) {
            endTakedown();
            return;
        }

        Vec3 facing = Vec3.directionFromRotation(0.0F, pinYaw);
        setPos(victim.getX() - facing.x * HEAD_REACH, victim.getY(), victim.getZ() - facing.z * HEAD_REACH);
        setDeltaMovement(Vec3.ZERO);
        setYRot(pinYaw);
        yRotO = pinYaw;
        setYBodyRot(pinYaw);
        setYHeadRot(pinYaw);
        yHeadRotO = pinYaw;
        setOnGround(true);
        resetFallDistance();

        if (pinTicks % 5 == 0) {
            victim.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 15, 6, false, false, false), this);
        }
        if (pinTicks % VestigiaConfig.JAGUAR_PIN_MAUL_INTERVAL_TICKS.get() == 0) {
            maul(level, victim);
        }
    }

    private void maul(ServerLevel level, LivingEntity victim) {
        victim.hurtServer(level, damageSources().mobAttack(this),
                VestigiaConfig.JAGUAR_PIN_DAMAGE.get().floatValue());
        level.playSound(null, victim.blockPosition(), ModSounds.JAGUAR_ATTACK.get(),
                SoundSource.HOSTILE, 1.0F, 0.8F);
    }

    private void throwOff() {
        LivingEntity victim = pinnedVictim;
        endTakedown();
        if (victim == null) {
            return;
        }
        Vec3 away = new Vec3(getX() - victim.getX(), 0.0D, getZ() - victim.getZ());
        if (away.lengthSqr() < 1.0E-4D) {
            away = Vec3.directionFromRotation(0.0F, pinYaw).scale(-1.0D);
        }
        setDeltaMovement(away.normalize().scale(0.65D).add(0.0D, 0.4D, 0.0D));
        hurtMarked = true;
        playSound(SoundEvents.RAVAGER_STUNNED, 1.0F, 1.5F);
    }

    public void releaseBeforeFreeze() {
        if (isPouncing()) {
            endTakedown();
        }
    }

    private void endTakedown() {
        if (pinnedVictim != null) {
            PinnedState.setPinned(pinnedVictim, false);
        }
        entityData.set(DATA_PINNED_ID, 0);

        pinnedVictim = null;
        leapTicks = 0;
        pinTicks = 0;
        struggleHits = 0;
        setFlag(FLAG_POUNCING, false);
        pounceCooldown = VestigiaConfig.JAGUAR_POUNCE_COOLDOWN_TICKS.get();
    }

    public boolean isPouncing() {
        return hasFlag(FLAG_POUNCING);
    }

    public boolean isHunting() {
        return hasFlag(FLAG_HUNTING);
    }

    private boolean hasFlag(byte flag) {
        return (entityData.get(DATA_FLAGS) & flag) != 0;
    }

    private void setFlag(byte flag, boolean on) {
        byte flags = entityData.get(DATA_FLAGS);
        entityData.set(DATA_FLAGS, (byte) (on ? flags | flag : flags & ~flag));
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        boolean hurt = super.hurtServer(level, source, amount);
        if (hurt && pinnedVictim != null && source.getEntity() == pinnedVictim) {
            int required = VestigiaConfig.JAGUAR_PIN_ESCAPE_HITS.get();
            if (required > 0 && ++struggleHits >= required) {
                throwOff();
            }
        }
        return hurt;
    }

    @Override
    public boolean isPushable() {
        return !isPouncing() && super.isPushable();
    }

    @Override
    protected void pushEntities() {
        if (isPouncing()) {
            return;
        }
        super.pushEntities();
    }

    @Override
    public void die(DamageSource source) {
        if (isPouncing()) {
            endTakedown();
        }
        super.die(source);
    }

    @Override
    public boolean onClimbable() {
        return horizontalCollision && getTarget() != null;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean killedByPlayer) {
        super.dropCustomDeathLoot(level, source, killedByPlayer);
        if (source.getEntity() instanceof Creeper creeper && creeper.isPowered()) {
            spawnAtLocation(level, new ItemStack(ModItems.OTORONGO_HELM.get()));
        }
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return isHunting() ? ModSounds.JAGUAR_CHASE.get() : SoundEvents.OCELOT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.OCELOT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.OCELOT_DEATH;
    }

    @Override
    public float getVoicePitch() {
        return super.getVoicePitch() * 0.6F;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel level, AgeableMob partner) {
        return null;
    }

    @Override
    public boolean isFood(net.minecraft.world.item.ItemStack stack) {
        return false;
    }

    public static boolean checkJaguarSpawnRules(EntityType<Jaguar> type, LevelAccessor level,
                                                EntitySpawnReason reason, net.minecraft.core.BlockPos pos,
                                                net.minecraft.util.RandomSource random) {
        return Animal.checkAnimalSpawnRules(type, level, reason, pos, random);
    }

    @Override
    public long getPersistentAngerEndTime() {
        return angerEndTime;
    }

    @Override
    public void setPersistentAngerEndTime(long endTime) {
        this.angerEndTime = endTime;
    }

    @Override
    public @Nullable EntityReference<LivingEntity> getPersistentAngerTarget() {
        return angerTarget;
    }

    @Override
    public void setPersistentAngerTarget(@Nullable EntityReference<LivingEntity> target) {
        this.angerTarget = target;
    }

    @Override
    public void startPersistentAngerTimer() {
        setTimeToRemainAngry(ANGER_DURATION.sample(random));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        addPersistentAngerSaveData(output);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        readPersistentAngerSaveData(level(), input);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("main", 5, state -> {
            state.setControllerSpeed(StasisState.animationSpeed(Jaguar.this));

            if (isPouncing()) {
                return state.setAndContinue(POUNCE);
            }
            if (hasFlag(FLAG_BITING)) {
                return state.setAndContinue(BITE);
            }
            if (hasFlag(FLAG_YAWNING)) {
                return state.setAndContinue(YAWN);
            }
            if (!hasFlag(FLAG_MOVING)) {
                return state.setAndContinue(IDLE);
            }
            return state.setAndContinue(isHunting() ? RUN : WALK);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }
}
