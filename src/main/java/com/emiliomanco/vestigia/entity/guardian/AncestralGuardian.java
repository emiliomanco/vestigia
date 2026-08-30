package com.emiliomanco.vestigia.entity.guardian;

import com.emiliomanco.vestigia.Civilization;
import com.emiliomanco.vestigia.config.VestigiaConfig;
import com.emiliomanco.vestigia.entity.PosedHumanoid;
import com.emiliomanco.vestigia.entity.StasisState;
import com.emiliomanco.vestigia.registry.ModItems;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.util.GeckoLibUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;
import net.minecraft.world.level.Level;

public abstract class AncestralGuardian extends Monster implements GeoEntity, PosedHumanoid {

    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    protected static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    protected static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");

    private static final RawAnimation SPEAR_HOLD_RIGHT = RawAnimation.begin().thenLoop("holdanimation_right");
    private static final RawAnimation SPEAR_HOLD_LEFT = RawAnimation.begin().thenLoop("holdanimation_left");
    private static final RawAnimation SPEAR_ATTACK_RIGHT = RawAnimation.begin().thenPlay("attack_right");
    private static final RawAnimation SPEAR_ATTACK_LEFT = RawAnimation.begin().thenPlay("attack_left");

    public static final String SPEAR_GRIP = "spear_grip";
    public static final String SPEAR_SWING_RIGHT = "spear_swing_right";
    public static final String SPEAR_SWING_LEFT = "spear_swing_left";

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    private final Civilization civilization;

    protected AncestralGuardian(EntityType<? extends AncestralGuardian> type, Level level, Civilization civilization) {
        super(type, level);
        this.civilization = civilization;
        setPersistenceRequired();
    }

    public Civilization civilization() {
        return civilization;
    }

    public boolean holdsSpear() {
        return getMainHandItem().is(ModItems.OBSIDIAN_SPEAR.get());
    }

    @Override
    public boolean armsDriven() {
        return holdsSpear();
    }

    protected void registerSpearControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<AncestralGuardian>(SPEAR_GRIP, 0, state -> {
            state.setControllerSpeed(StasisState.animationSpeed(this));
            return holdsSpear() && !bodyDriven()
                    ? state.setAndContinue(isLeftHanded() ? SPEAR_HOLD_LEFT : SPEAR_HOLD_RIGHT)
                    : PlayState.STOP;
        })
                .triggerableAnim(SPEAR_SWING_RIGHT, SPEAR_ATTACK_RIGHT)
                .triggerableAnim(SPEAR_SWING_LEFT, SPEAR_ATTACK_LEFT));
    }

    protected void swingSpear() {
        if (holdsSpear()) {
            triggerAnim(SPEAR_GRIP, isLeftHanded() ? SPEAR_SWING_LEFT : SPEAR_SWING_RIGHT);
        }
    }

    private static final EntityDataAccessor<Boolean> CLIP_RUNNING =
            SynchedEntityData.defineId(AncestralGuardian.class, EntityDataSerializers.BOOLEAN);

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(CLIP_RUNNING, false);
    }

    protected void setClipRunning(boolean running) {
        if (entityData.get(CLIP_RUNNING) != running) {
            entityData.set(CLIP_RUNNING, running);
        }
    }

    @Override
    public boolean bodyDriven() {
        return entityData.get(CLIP_RUNNING);
    }

    protected boolean speaksMaya() {
        return civilization == Civilization.MAYA;
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return speaksMaya() ? MayaVoice.idle(getTarget()) : SoundEvents.HUSK_AMBIENT;
    }

    @Override
    public int getAmbientSoundInterval() {
        return speaksMaya() ? MayaVoice.SPEECH_INTERVAL : super.getAmbientSoundInterval();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return speaksMaya() ? MayaVoice.hurt() : SoundEvents.HUSK_HURT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return speaksMaya() ? MayaVoice.death() : SoundEvents.HUSK_DEATH;
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        boolean spotted = speaksMaya() && target != null && getTarget() == null;
        super.setTarget(target);
        if (spotted && !level().isClientSide()) {
            playSound(MayaVoice.battlecry(), 1.2F, 1.0F);
        }
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        playSound(SoundEvents.STONE_STEP, 0.25F, 0.8F);
    }

    public static AttributeSupplier.Builder baseAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 34.0D)
                .add(Attributes.ATTACK_DAMAGE, 6.0D)
                .add(Attributes.ARMOR, 6.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.26D)
                .add(Attributes.FOLLOW_RANGE, 28.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.4D);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, false));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 12.0F));
        goalSelector.addGoal(9, new RandomLookAroundGoal(this));

        targetSelector.addGoal(1, new HurtByTargetGoal(this, AncestralGuardian.class));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public void applyDifficultyScaling() {
        double healthMultiplier = VestigiaConfig.GUARDIAN_HEALTH_MULTIPLIER.get();
        double damageMultiplier = VestigiaConfig.GUARDIAN_DAMAGE_MULTIPLIER.get();

        var health = getAttribute(Attributes.MAX_HEALTH);
        if (health != null) {
            health.setBaseValue(health.getBaseValue() * healthMultiplier);
            setHealth(getMaxHealth());
        }
        var damage = getAttribute(Attributes.ATTACK_DAMAGE);
        if (damage != null) {
            damage.setBaseValue(damage.getBaseValue() * damageMultiplier);
        }
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public final boolean canAttack(LivingEntity target) {
        return super.canAttack(target);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("main", 4, state -> {
            state.setControllerSpeed(StasisState.animationSpeed(this));
            if (swinging) {
                return state.setAndContinue(ATTACK);
            }
            return state.isMoving() ? state.setAndContinue(WALK) : state.setAndContinue(IDLE);
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }
}
