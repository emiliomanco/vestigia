package com.emiliomanco.vestigia.entity.guardian;

import com.emiliomanco.vestigia.Civilization;
import com.emiliomanco.vestigia.entity.ai.Enthralled;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.WeatherData;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

public abstract class TemplePriest extends AncestralGuardian {

    private static final int CAST_GAP_TICKS = 60;

    protected static final int MEND = 0;
    protected static final int SIGNATURE = 1;
    protected static final int CURSE = 2;
    protected static final int CALL = 3;
    protected static final int SKY = 4;

    private static final int[] SPELL_COOLDOWNS = {120, 0, 160, 600, 400};

    private static final double SUPPORT_RANGE = 16.0D;
    private static final double CURSE_RANGE = 14.0D;

    private static final float HEAL_AMOUNT = 6.0F;
    private static final float HEAL_THRESHOLD = 0.25F;
    private static final int CURSE_DURATION = 140;
    private static final List<Holder<MobEffect>> CURSES =
            List.of(MobEffects.BLINDNESS, MobEffects.NAUSEA, MobEffects.WEAKNESS);
    private static final int STORM_DURATION_TICKS = 2400;

    private static final double CALL_RANGE = 20.0D;
    private static final int CALL_DURATION_TICKS = 300;

    protected static final String CASTING = "casting";
    protected static final String SPELL = "spell";
    protected static final String THUNDER = "thunder";
    protected static final String SIGNATURE_TRIGGER = "signature";

    private int castCooldown;
    private int castLock;
    private final int[] cooldown = new int[SPELL_COOLDOWNS.length];
    private final int[] spellCooldowns;

    protected TemplePriest(EntityType<? extends TemplePriest> type, Level level,
                           Civilization civilization, int signatureCooldown) {
        super(type, level, civilization);
        this.spellCooldowns = SPELL_COOLDOWNS.clone();
        this.spellCooldowns[SIGNATURE] = signatureCooldown;
    }

    public static AttributeSupplier.Builder priestAttributes() {
        return baseAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D)
                .add(Attributes.ARMOR, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.30D)
                .add(Attributes.FOLLOW_RANGE, 32.0D);
    }

    protected abstract RawAnimation spellAnimation();

    protected abstract RawAnimation thunderAnimation();

    protected abstract RawAnimation signatureAnimation();

    protected abstract int spellClipTicks();

    protected abstract int thunderClipTicks();

    protected abstract int signatureClipTicks();

    protected abstract boolean castSignature(ServerLevel level);

    protected abstract SoundEvent castSound();

    protected abstract SoundEvent quietSound();

    protected abstract SoundEvent loudSound();

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Player.class, 8.0F, 1.15D, 1.3D));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!(level() instanceof ServerLevel serverLevel) || !isAlive()) {
            return;
        }
        if (castLock > 0 && --castLock == 0) {
            setClipRunning(false);
        }
        tickSignature(serverLevel);
        for (int i = 0; i < cooldown.length; i++) {
            if (cooldown[i] > 0) {
                cooldown[i]--;
            }
        }
        if (castCooldown > 0) {
            castCooldown--;
            return;
        }
        if (mendAlly(serverLevel) || castSignature(serverLevel) || curseEnemy(serverLevel)
                || callTheWild(serverLevel) || callTheSky(serverLevel)) {
            castCooldown = CAST_GAP_TICKS;
        }
    }

    protected void tickSignature(ServerLevel level) {
    }

    protected boolean ready(int spell) {
        return cooldown[spell] <= 0;
    }

    protected void cast(int spell, String trigger, int clipTicks) {
        cooldown[spell] = spellCooldowns[spell];
        castLock = clipTicks;
        setClipRunning(true);
        triggerAnim(CASTING, trigger);
        level().playSound(null, blockPosition(), castSound(), SoundSource.HOSTILE, 1.2F, 1.0F);
    }

    private boolean mendAlly(ServerLevel level) {
        if (!ready(MEND)) {
            return false;
        }
        AncestralGuardian patient = null;
        float worst = HEAL_THRESHOLD;
        for (AncestralGuardian ally : level.getEntitiesOfClass(AncestralGuardian.class,
                getBoundingBox().inflate(SUPPORT_RANGE), guardian -> guardian != this && guardian.isAlive())) {
            float fraction = ally.getHealth() / ally.getMaxHealth();
            if (fraction < worst) {
                worst = fraction;
                patient = ally;
            }
        }
        if (patient == null) {
            return false;
        }
        patient.heal(HEAL_AMOUNT);
        cast(MEND, SPELL, spellClipTicks());
        level.sendParticles(ParticleTypes.HEART, patient.getX(), patient.getEyeY(), patient.getZ(),
                6, 0.4D, 0.4D, 0.4D, 0.0D);
        level.playSound(null, blockPosition(), quietSound(), SoundSource.HOSTILE, 1.0F, 1.2F);
        return true;
    }

    private boolean callTheWild(ServerLevel level) {
        if (!ready(CALL)) {
            return false;
        }
        LivingEntity victim = nearestEnemy(level, CALL_RANGE);
        if (victim == null) {
            return false;
        }
        List<Mob> heard = level.getEntitiesOfClass(Mob.class, getBoundingBox().inflate(CALL_RANGE),
                mob -> mob.isAlive() && mob != victim && !isGarrison(mob) && !Enthralled.isEnthralled(mob));
        if (heard.isEmpty()) {
            return false;
        }
        for (Mob mob : heard) {
            Enthralled.bind(mob, victim, CALL_DURATION_TICKS);
        }
        cast(CALL, THUNDER, thunderClipTicks());
        level.sendParticles(ParticleTypes.ANGRY_VILLAGER, getX(), getEyeY(), getZ(),
                40, CALL_RANGE / 3.0D, 1.5D, CALL_RANGE / 3.0D, 0.0D);
        level.playSound(null, blockPosition(), loudSound(), SoundSource.HOSTILE, 1.6F, 0.7F);
        return true;
    }

    protected static boolean isGarrison(Mob mob) {
        return mob instanceof AncestralGuardian || mob instanceof AncestralBoss;
    }

    private boolean curseEnemy(ServerLevel level) {
        if (!ready(CURSE)) {
            return false;
        }
        LivingEntity victim = nearestEnemy(level, CURSE_RANGE);
        if (victim == null) {
            return false;
        }
        Holder<MobEffect> curse = pickCurse(victim);
        if (curse == null) {
            return false;
        }
        cast(CURSE, SPELL, spellClipTicks());
        victim.addEffect(new MobEffectInstance(curse, CURSE_DURATION, 0), this);
        level.sendParticles(ParticleTypes.WITCH, victim.getX(), victim.getEyeY(), victim.getZ(),
                20, 0.5D, 0.6D, 0.5D, 0.0D);
        level.playSound(null, blockPosition(), quietSound(), SoundSource.HOSTILE, 1.0F, 0.8F);
        return true;
    }

    private @Nullable Holder<MobEffect> pickCurse(LivingEntity victim) {
        List<Holder<MobEffect>> open = new ArrayList<>(CURSES.size());
        for (Holder<MobEffect> curse : CURSES) {
            if (!victim.hasEffect(curse)) {
                open.add(curse);
            }
        }
        return open.isEmpty() ? null : open.get(getRandom().nextInt(open.size()));
    }

    private boolean callTheSky(ServerLevel level) {
        if (!ready(SKY)) {
            return false;
        }
        LivingEntity victim = nearestEnemy(level, CURSE_RANGE);
        if (victim == null || !level.canSeeSky(victim.blockPosition())) {
            return false;
        }
        if (!level.isThundering()) {
            WeatherData weather = level.getWeatherData();
            weather.setRaining(true);
            weather.setRainTime(STORM_DURATION_TICKS);
            weather.setThundering(true);
            weather.setThunderTime(STORM_DURATION_TICKS);
            weather.setClearWeatherTime(0);
        }
        cast(SKY, THUNDER, thunderClipTicks());
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level, EntitySpawnReason.TRIGGERED);
        if (bolt == null) {
            return false;
        }
        BlockPos strike = victim.blockPosition();
        bolt.snapTo(strike.getX() + 0.5D, strike.getY(), strike.getZ() + 0.5D);
        bolt.setCause(null);
        level.addFreshEntity(bolt);
        level.playSound(null, blockPosition(), loudSound(), SoundSource.HOSTILE, 1.4F, 0.8F);
        return true;
    }

    protected @Nullable LivingEntity nearestEnemy(ServerLevel level, double range) {
        LivingEntity own = getTarget();
        if (own != null && own.isAlive() && distanceTo(own) <= range) {
            return own;
        }
        AABB search = getBoundingBox().inflate(range);
        List<AncestralGuardian> allies = level.getEntitiesOfClass(AncestralGuardian.class, search,
                guardian -> guardian != this && guardian.getTarget() != null);
        for (AncestralGuardian ally : allies) {
            LivingEntity theirs = ally.getTarget();
            if (theirs != null && theirs.isAlive() && distanceTo(theirs) <= range) {
                return theirs;
            }
        }
        return null;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<TemplePriest>(CASTING, 0, state -> PlayState.STOP)
                .triggerableAnim(SPELL, spellAnimation())
                .triggerableAnim(THUNDER, thunderAnimation())
                .triggerableAnim(SIGNATURE_TRIGGER, signatureAnimation()));
    }

    @Override
    public boolean armsDriven() {
        return bodyDriven();
    }
}
