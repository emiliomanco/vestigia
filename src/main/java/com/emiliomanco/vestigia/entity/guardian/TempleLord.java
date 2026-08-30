package com.emiliomanco.vestigia.entity.guardian;

import com.emiliomanco.vestigia.Civilization;
import com.emiliomanco.vestigia.entity.RaisedEarth;
import com.emiliomanco.vestigia.registry.ModSounds;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public abstract class TempleLord extends AncestralBoss {

    public static final int WARNING_NONE = 0;
    public static final int WARNING_LIGHT = 1;
    public static final int WARNING_HEAVY = 2;

    private static final EntityDataAccessor<Integer> WARNING =
            SynchedEntityData.defineId(TempleLord.class, EntityDataSerializers.INT);

    private static final float MAX_HEALTH = 300.0F;
    private static final float FIRE_VULNERABILITY = 3.0F;

    public static final int PARRY_WINDOW_TICKS = 10;
    private static final int STAGGER_TICKS = 40;

    private static final int DEFAULT_RECOVERY_TICKS = 20;
    private static final double MELEE_RANGE = 4.5D;
    private static final double ROCK_MIN_RANGE = 6.0D;
    private static final double ROCK_MAX_RANGE = 22.0D;
    private static final double STOMP_RADIUS = 9.0D;

    private static final float LIGHT_DAMAGE = 6.0F;
    private static final float MEDIUM_DAMAGE = 10.0F;
    private static final float HEAVY_DAMAGE = 18.0F;
    private static final float PIN_DAMAGE = 3.0F;
    private static final int PIN_DURATION_TICKS = 60;
    private static final int PIN_DAMAGE_INTERVAL = 15;
    private static final double LEAP_REACH = 9.0D;
    private static final double LEAP_TICKS = 12.0D;
    private static final float SLAM_DAMAGE = 14.0F;
    private static final float THROW_DAMAGE = 8.0F;
    private static final float STOMP_DAMAGE = 6.0F;
    private static final float ROCK_DAMAGE = 10.0F;
    private static final double HEAVY_KNOCKBACK = 3.4D;
    private static final double STOMP_KNOCKBACK = 2.2D;

    private static final RawAnimation GRAB_ROCK = RawAnimation.begin().thenPlay("grab_rock_boss");
    private static final RawAnimation THROW_ROCK = RawAnimation.begin().thenPlay("throw_rock_boss");
    private static final RawAnimation GRAB_THROW = RawAnimation.begin().thenPlay("grab_throw_player_boss");
    private static final RawAnimation STOMP = RawAnimation.begin().thenPlay("stomp_boss");
    private static final RawAnimation LIGHT_PUNCH = RawAnimation.begin().thenPlay("light_attack_punch");
    private static final RawAnimation MEDIUM_SLASH = RawAnimation.begin().thenPlay("medium_attack_slash");
    private static final RawAnimation MEDIUM_STAB = RawAnimation.begin().thenPlay("medium_attack_stab");
    private static final RawAnimation HEAVY_SLAM = RawAnimation.begin().thenPlay("heavy_attack_groundslam");
    private static final RawAnimation PIN_START = RawAnimation.begin().thenPlay("grab_pindown_player_boss");
    private static final RawAnimation PIN_HOLD = RawAnimation.begin().thenLoop("while_pinned_down");
    private static final RawAnimation PIN_END = RawAnimation.begin().thenPlay("pindown_end");
    private static final String ACTING = "acting";

    private Move pending = Move.NONE;
    private int windUp;
    private int attackCooldown;
    private int staggered;
    private int pinTicks;
    private @Nullable LivingEntity pinned;
    private boolean lunged;

    private enum Move {
        NONE(WARNING_NONE, 0, 0, 0, 0),
        ROCK(WARNING_LIGHT, 20, 33, 40, 120),
        LIGHT(WARNING_LIGHT, 16, 20, 20, 0),
        MEDIUM(WARNING_LIGHT, 18, 20, 25, 0),
        STOMP(WARNING_HEAVY, 20, 20, 45, 160),
        HEAVY(WARNING_HEAVY, 40, 50, 60, 220),
        GRAB_THROW(WARNING_HEAVY, 21, 21, 45, 240),
        PINDOWN(WARNING_HEAVY, 45, 50, 45, 320);

        final int warning;
        final int windUp;
        final int clip;
        final int recovery;
        final int cooldown;

        Move(int warning, int windUp, int clip, int recovery, int cooldown) {
            this.warning = warning;
            this.windUp = windUp;
            this.clip = clip;
            this.recovery = recovery;
            this.cooldown = cooldown;
        }
    }

    private final int[] moveCooldown = new int[Move.values().length];

    private static final EntityDataAccessor<Boolean> ACTING_CLIP =
            SynchedEntityData.defineId(TempleLord.class, EntityDataSerializers.BOOLEAN);

    private int clipLock;

    protected TempleLord(EntityType<? extends TempleLord> type, Level level,
                         Civilization civilization, BossEvent.BossBarColor colour) {
        super(type, level, civilization, colour);
    }

    protected abstract ItemStack weapon();

    public static AttributeSupplier.Builder lordAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, 9.0D)
                .add(Attributes.ARMOR, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.24D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.0D, true));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 20.0F));

        targetSelector.addGoal(1, new HurtByTargetGoal(this, AncestralGuardian.class, TempleLord.class));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(WARNING, WARNING_NONE);
        builder.define(ACTING_CLIP, false);
    }

    public int warning() {
        return entityData.get(WARNING);
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty,
                                        EntitySpawnReason reason, SpawnGroupData data) {
        SpawnGroupData result = super.finalizeSpawn(level, difficulty, reason, data);
        setItemSlot(EquipmentSlot.MAINHAND, weapon());
        setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        return result;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!(level() instanceof ServerLevel level)) {
            return;
        }
        for (int i = 0; i < moveCooldown.length; i++) {
            if (moveCooldown[i] > 0) {
                moveCooldown[i]--;
            }
        }
        if (clipLock > 0) {
            clipLock--;
        }
        boolean acting = clipLock > 0 || pinTicks > 0 || staggered > 0;
        if (entityData.get(ACTING_CLIP) != acting) {
            entityData.set(ACTING_CLIP, acting);
        }
        if (staggered > 0) {
            staggered--;
            return;
        }
        if (pinTicks > 0) {
            holdThemDown(level);
            return;
        }

        if (pending != Move.NONE) {
            if (pending == Move.HEAVY && !lunged) {
                leap();
            }
            if (--windUp <= 0) {
                Move landed = pending;
                land(level, landed);
                pending = Move.NONE;
                entityData.set(WARNING, WARNING_NONE);
                attackCooldown = landed.recovery > 0 ? landed.recovery : DEFAULT_RECOVERY_TICKS;
                moveCooldown[landed.ordinal()] = landed.cooldown;
            }
            return;
        }
        if (attackCooldown > 0) {
            attackCooldown--;
            return;
        }

        LivingEntity target = getTarget();
        if (target == null || !target.isAlive() || !hasLineOfSight(target)) {
            return;
        }
        begin(choose(level, target));
    }

    private boolean ready(Move move) {
        return moveCooldown[move.ordinal()] <= 0;
    }

    private Move choose(ServerLevel level, LivingEntity target) {
        double distance = distanceTo(target);
        if (distance > MELEE_RANGE) {
            boolean inThrowingRange = distance >= ROCK_MIN_RANGE && distance <= ROCK_MAX_RANGE;
            return inThrowingRange && ready(Move.ROCK) ? Move.ROCK : Move.NONE;
        }
        int crowd = level.getEntitiesOfClass(Player.class, getBoundingBox().inflate(STOMP_RADIUS)).size();
        if ((crowd > 1 || random.nextInt(6) == 0) && ready(Move.STOMP)) {
            return Move.STOMP;
        }
        Move rolled = switch (random.nextInt(8)) {
            case 0, 1, 2 -> Move.LIGHT;
            case 3, 4 -> Move.MEDIUM;
            case 5 -> Move.HEAVY;
            case 6 -> Move.GRAB_THROW;
            default -> Move.PINDOWN;
        };
        if (ready(rolled)) {
            return rolled;
        }
        return random.nextBoolean() ? Move.LIGHT : Move.MEDIUM;
    }

    private void begin(Move move) {
        if (move == Move.NONE) {
            return;
        }
        pending = move;
        windUp = move.windUp;
        clipLock = move.clip;
        lunged = false;
        entityData.set(WARNING, move.warning);
        switch (move) {
            case ROCK -> triggerAnim(ACTING, "grab_rock");
            case STOMP -> triggerAnim(ACTING, "stomp");
            case LIGHT -> triggerAnim(ACTING, "light");
            case MEDIUM -> triggerAnim(ACTING, random.nextBoolean() ? "medium_slash" : "medium_stab");
            case HEAVY -> triggerAnim(ACTING, "heavy");
            case GRAB_THROW -> triggerAnim(ACTING, "grab_throw");
            case PINDOWN -> triggerAnim(ACTING, "pin_start");
            default -> { }
        }
    }

    private void leap() {
        lunged = true;
        LivingEntity target = getTarget();
        if (target == null) {
            return;
        }
        Vec3 toward = new Vec3(target.getX() - getX(), 0.0D, target.getZ() - getZ());
        double reach = Math.min(toward.length(), LEAP_REACH);
        if (reach < 0.1D) {
            return;
        }
        setDeltaMovement(toward.normalize().scale(reach / LEAP_TICKS));
        hurtMarked = true;
    }

    private void land(ServerLevel level, Move move) {
        LivingEntity target = getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }
        switch (move) {
            case ROCK -> throwTheFloor(level, target);
            case STOMP -> stomp(level);
            case LIGHT -> simpleBlow(level, target, LIGHT_DAMAGE, 0.0D);
            case MEDIUM -> simpleBlow(level, target, MEDIUM_DAMAGE, 0.6D);
            case HEAVY -> heavyBlow(level, target);
            case GRAB_THROW -> grabAndThrow(level, target);
            case PINDOWN -> beginPin(level, target);
            default -> { }
        }
    }

    private void throwTheFloor(ServerLevel level, LivingEntity target) {
        BlockPos ground = blockPosition().below();
        RaisedEarth boulder = RaisedEarth.raise(level, this, ground, ROCK_DAMAGE);
        if (boulder == null) {
            return;
        }
        boulder.skipRise();
        boulder.setPos(getX(), getEyeY(), getZ());
        level.addFreshEntity(boulder);
        triggerAnim(ACTING, "throw_rock");
        boulder.launchAlong(new Vec3(
                target.getX() - getX(),
                target.getY(0.5D) - getEyeY(),
                target.getZ() - getZ()));
    }

    private void stomp(ServerLevel level) {
        level.playSound(null, blockPosition(), ModSounds.BOSS_STOMP.get(), SoundSource.HOSTILE, 1.6F, 1.0F);
        level.sendParticles(ParticleTypes.EXPLOSION, getX(), getY() + 0.2D, getZ(),
                12, STOMP_RADIUS / 3.0D, 0.1D, STOMP_RADIUS / 3.0D, 0.0D);

        for (LivingEntity caught : level.getEntitiesOfClass(LivingEntity.class,
                getBoundingBox().inflate(STOMP_RADIUS), living -> living != this && living.isAlive())) {
            if (caught instanceof AncestralGuardian || caught instanceof AncestralBoss) {
                continue;
            }
            if (parried(caught)) {
                continue;
            }
            caught.hurtServer(level, damageSources().mobAttack(this), STOMP_DAMAGE);
            Vec3 away = caught.position().subtract(position()).normalize();
            caught.setDeltaMovement(away.scale(STOMP_KNOCKBACK).add(0.0D, 0.9D, 0.0D));
            caught.hurtMarked = true;
        }
    }

    private void heavyBlow(ServerLevel level, LivingEntity target) {
        if (distanceTo(target) > MELEE_RANGE + 1.5D || parried(target)) {
            return;
        }
        swing(net.minecraft.world.InteractionHand.MAIN_HAND);
        target.hurtServer(level, damageSources().mobAttack(this), HEAVY_DAMAGE);
        Vec3 away = target.position().subtract(position()).normalize();
        target.setDeltaMovement(away.scale(HEAVY_KNOCKBACK).add(0.0D, 0.55D, 0.0D));
        target.hurtMarked = true;
        level.playSound(null, blockPosition(), ModSounds.BOSS_STOMP.get(), SoundSource.HOSTILE, 1.5F, 1.1F);
        level.sendParticles(ParticleTypes.EXPLOSION, getX(), getY() + 0.2D, getZ(),
                8, 1.5D, 0.1D, 1.5D, 0.0D);
    }

    private void grabAndThrow(ServerLevel level, LivingEntity target) {
        if (distanceTo(target) > MELEE_RANGE || parried(target)) {
            return;
        }
        target.hurtServer(level, damageSources().mobAttack(this), THROW_DAMAGE);
        Vec3 away = target.position().subtract(position()).normalize();
        target.setDeltaMovement(away.scale(1.6D).add(0.0D, 1.15D, 0.0D));
        target.hurtMarked = true;
        level.playSound(null, blockPosition(), SoundEvents.PLAYER_BIG_FALL, SoundSource.HOSTILE, 1.2F, 0.7F);
    }

    private void simpleBlow(ServerLevel level, LivingEntity target, float damage, double knockback) {
        if (distanceTo(target) > MELEE_RANGE || parried(target)) {
            return;
        }
        swing(net.minecraft.world.InteractionHand.MAIN_HAND);
        target.hurtServer(level, damageSources().mobAttack(this), damage);
        if (knockback > 0.0D) {
            Vec3 away = target.position().subtract(position()).normalize();
            target.setDeltaMovement(away.scale(knockback).add(0.0D, 0.25D, 0.0D));
            target.hurtMarked = true;
        }
    }

    private void beginPin(ServerLevel level, LivingEntity target) {
        if (distanceTo(target) > MELEE_RANGE || parried(target)) {
            return;
        }
        target.hurtServer(level, damageSources().mobAttack(this), SLAM_DAMAGE);
        pinned = target;
        pinTicks = PIN_DURATION_TICKS;
        triggerAnim(ACTING, "pin_hold");
        level.sendParticles(ParticleTypes.CRIT, target.getX(), target.getY(), target.getZ(),
                20, 0.4D, 0.1D, 0.4D, 0.1D);
        level.playSound(null, target.blockPosition(), SoundEvents.PLAYER_BIG_FALL, SoundSource.HOSTILE, 1.5F, 0.5F);
    }

    private void holdThemDown(ServerLevel level) {
        LivingEntity victim = pinned;
        if (victim == null || !victim.isAlive() || distanceTo(victim) > MELEE_RANGE + 2.0D) {
            releasePin();
            return;
        }
        getLookControl().setLookAt(victim, 30.0F, 30.0F);
        victim.setDeltaMovement(0.0D, -0.2D, 0.0D);
        victim.hurtMarked = true;
        if (pinTicks % PIN_DAMAGE_INTERVAL == 0) {
            victim.hurtServer(level, damageSources().mobAttack(this), PIN_DAMAGE);
        }
        if (--pinTicks <= 0) {
            releasePin();
        }
    }

    private void releasePin() {
        if (pinTicks > 0 || pinned != null) {
            triggerAnim(ACTING, "pin_end");
        }
        pinTicks = 0;
        pinned = null;
        entityData.set(WARNING, WARNING_NONE);
        attackCooldown = Move.PINDOWN.recovery;
    }

    private boolean parried(LivingEntity defender) {
        if (!defender.isBlocking() || defender.getTicksUsingItem() > PARRY_WINDOW_TICKS) {
            return false;
        }
        staggered = STAGGER_TICKS;
        pending = Move.NONE;
        if (pinned != null) {
            releasePin();
        }
        entityData.set(WARNING, WARNING_NONE);
        Level level = level();
        level.playSound(null, blockPosition(), SoundEvents.SHIELD_BLOCK.value(), SoundSource.PLAYERS, 1.6F, 0.7F);
        if (level instanceof ServerLevel server) {
            server.sendParticles(ParticleTypes.CRIT, getX(), getEyeY(), getZ(), 25, 0.5D, 0.5D, 0.5D, 0.2D);
        }
        return true;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        float dealt = source.is(DamageTypeTags.IS_FIRE) ? amount * FIRE_VULNERABILITY : amount;
        return super.hurtServer(level, source, dealt);
    }

    @Override
    public boolean isImmobile() {
        return super.isImmobile() || pinTicks > 0;
    }

    @Override
    public void heal(float amount) {
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<TempleLord>(ACTING, 0, state -> PlayState.STOP)
                .triggerableAnim("grab_rock", GRAB_ROCK)
                .triggerableAnim("throw_rock", THROW_ROCK)
                .triggerableAnim("grab_throw", GRAB_THROW)
                .triggerableAnim("stomp", STOMP)
                .triggerableAnim("light", LIGHT_PUNCH)
                .triggerableAnim("medium_slash", MEDIUM_SLASH)
                .triggerableAnim("medium_stab", MEDIUM_STAB)
                .triggerableAnim("heavy", HEAVY_SLAM)
                .triggerableAnim("pin_start", PIN_START)
                .triggerableAnim("pin_hold", PIN_HOLD)
                .triggerableAnim("pin_end", PIN_END));
    }

    @Override
    public boolean armsDriven() {
        return bodyDriven();
    }

    @Override
    public boolean bodyDriven() {
        return entityData.get(ACTING_CLIP);
    }

    private boolean hasRecordedVoice() {
        return civilization() == Civilization.MAYA;
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return hasRecordedVoice() ? MayaVoice.idle(getTarget()) : SoundEvents.HUSK_AMBIENT;
    }

    @Override
    public int getAmbientSoundInterval() {
        return hasRecordedVoice() ? MayaVoice.SPEECH_INTERVAL : super.getAmbientSoundInterval();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(DamageSource source) {
        return hasRecordedVoice() ? MayaVoice.hurt() : SoundEvents.HUSK_HURT;
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return hasRecordedVoice() ? MayaVoice.death() : SoundEvents.HUSK_DEATH;
    }

    @Override
    public void setTarget(@Nullable LivingEntity target) {
        boolean spotted = hasRecordedVoice() && target != null && getTarget() == null;
        super.setTarget(target);
        if (spotted && !level().isClientSide()) {
            playSound(MayaVoice.battlecry(), 1.4F, 0.8F);
        }
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        attackCooldown = input.getIntOr("AttackCooldown", 0);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putInt("AttackCooldown", attackCooldown);
    }
}
