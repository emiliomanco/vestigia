package com.emiliomanco.vestigia.entity.guardian;

import com.emiliomanco.vestigia.Civilization;
import com.emiliomanco.vestigia.entity.projectile.ElementalBolt;
import com.geckolib.animation.RawAnimation;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class IncanPriest extends TemplePriest {

    private static final int FIREBALL_COOLDOWN = 400;

    private static final double FIREBALL_RANGE = 24.0D;

    private static final RawAnimation SPELL_CLIP = RawAnimation.begin().thenPlay("incan_spell");
    private static final RawAnimation THUNDER_CLIP = RawAnimation.begin().thenPlay("incan_call_thunder");
    private static final RawAnimation FIREBALL_CLIP = RawAnimation.begin().thenPlay("incan_fireball");

    private static final int SPELL_CLIP_TICKS = 20;
    private static final int THUNDER_CLIP_TICKS = 30;
    private static final int FIREBALL_CLIP_TICKS = 35;

    private static final int CONJURE_TICK = 15;
    private static final int THROW_TICK = 24;

    private static final float FIREBALL_DAMAGE = 14.0F;
    private static final float FIREBALL_SPEED = 1.1F;

    private int fireballTick = -1;
    private @Nullable ElementalBolt held;
    private @Nullable LivingEntity mark;

    public IncanPriest(EntityType<? extends IncanPriest> type, Level level) {
        super(type, level, Civilization.INCA, FIREBALL_COOLDOWN);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return priestAttributes();
    }

    @Override
    protected RawAnimation spellAnimation() {
        return SPELL_CLIP;
    }

    @Override
    protected RawAnimation thunderAnimation() {
        return THUNDER_CLIP;
    }

    @Override
    protected RawAnimation signatureAnimation() {
        return FIREBALL_CLIP;
    }

    @Override
    protected int spellClipTicks() {
        return SPELL_CLIP_TICKS;
    }

    @Override
    protected int thunderClipTicks() {
        return THUNDER_CLIP_TICKS;
    }

    @Override
    protected int signatureClipTicks() {
        return FIREBALL_CLIP_TICKS;
    }

    @Override
    protected SoundEvent castSound() {
        return SoundEvents.EVOKER_CAST_SPELL;
    }

    @Override
    protected SoundEvent quietSound() {
        return SoundEvents.EVOKER_PREPARE_SUMMON;
    }

    @Override
    protected SoundEvent loudSound() {
        return SoundEvents.EVOKER_PREPARE_ATTACK;
    }

    @Override
    protected boolean castSignature(ServerLevel level) {
        if (!ready(SIGNATURE) || fireballTick >= 0) {
            return false;
        }
        LivingEntity victim = nearestEnemy(level, FIREBALL_RANGE);
        if (victim == null || !hasLineOfSight(victim)) {
            return false;
        }
        mark = victim;
        fireballTick = 0;
        cast(SIGNATURE, SIGNATURE_TRIGGER, FIREBALL_CLIP_TICKS);
        return true;
    }

    @Override
    protected void tickSignature(ServerLevel level) {
        if (fireballTick < 0) {
            return;
        }
        fireballTick++;
        if (mark != null && mark.isAlive()) {
            getLookControl().setLookAt(mark, 30.0F, 30.0F);
        }
        if (fireballTick == CONJURE_TICK) {
            conjure(level);
        } else if (fireballTick == THROW_TICK) {
            hurl(level);
        } else if (fireballTick >= FIREBALL_CLIP_TICKS) {
            dropTheBall();
        }
    }

    private void conjure(ServerLevel level) {
        if (mark == null || !mark.isAlive() || !isAlive()) {
            fireballTick = -1;
            return;
        }
        ElementalBolt ball = new ElementalBolt(level, this, ElementalBolt.Element.FIRE, FIREBALL_DAMAGE);
        ball.makeGiant();
        ball.hold(-1, 0.0F, FIREBALL_SPEED);
        level.addFreshEntity(ball);
        held = ball;
        level.playSound(null, blockPosition(), SoundEvents.BLAZE_SHOOT, SoundSource.HOSTILE, 1.4F, 0.6F);
        level.sendParticles(ParticleTypes.FLAME, getX(), getEyeY(), getZ(), 40, 0.6D, 0.4D, 0.6D, 0.05D);
    }

    private void hurl(ServerLevel level) {
        ElementalBolt ball = held;
        held = null;
        if (ball == null || !ball.isAlive()) {
            fireballTick = -1;
            return;
        }
        LivingEntity victim = mark;
        Vec3 line = victim != null && victim.isAlive()
                ? victim.getEyePosition().subtract(ball.position())
                : Vec3.ZERO;
        if (line.lengthSqr() < 1.0E-4D) {
            line = getLookAngle();
        }
        ball.releaseToward(line);
        level.playSound(null, blockPosition(), SoundEvents.GHAST_SHOOT, SoundSource.HOSTILE, 1.6F, 0.7F);
    }

    private void dropTheBall() {
        fireballTick = -1;
        mark = null;
        if (held != null) {
            if (held.isAlive()) {
                held.discard();
            }
            held = null;
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        dropTheBall();
        super.remove(reason);
    }
}
