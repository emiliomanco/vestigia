package com.emiliomanco.vestigia.entity.guardian;

import com.emiliomanco.vestigia.entity.PosedHumanoid;
import com.emiliomanco.vestigia.Civilization;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.util.GeckoLibUtil;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;

public abstract class AncestralBoss extends Monster implements GeoEntity, PosedHumanoid {

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);
    private final Civilization civilization;
    private final ServerBossEvent bossEvent;

    protected AncestralBoss(EntityType<? extends AncestralBoss> type, Level level,
                            Civilization civilization, BossEvent.BossBarColor colour) {
        super(type, level);
        this.civilization = civilization;
        this.bossEvent = new ServerBossEvent(
                Mth.createInsecureUUID(random),
                Component.translatable(type.getDescriptionId()),
                colour,
                BossEvent.BossBarOverlay.NOTCHED_6);
        this.bossEvent.setProgress(1.0F);
        setPersistenceRequired();
    }

    public Civilization civilization() {
        return civilization;
    }

    protected ServerBossEvent bossEvent() {
        return bossEvent;
    }

    public static AttributeSupplier.Builder baseBossAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 200.0D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D)
                .add(Attributes.ARMOR, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.28D)
                .add(Attributes.FOLLOW_RANGE, 40.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.9D);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        bossEvent.setProgress(getHealth() / getMaxHealth());
    }

    protected void announce(String translationKey) {
        Component message = Component.translatable(translationKey);
        for (ServerPlayer player : bossEvent.getPlayers()) {
            player.sendSystemMessage(message, true);
        }
    }

    protected float healthFraction() {
        return getHealth() / getMaxHealth();
    }

    protected boolean isServer() {
        return level() instanceof ServerLevel;
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        bossEvent.removePlayer(player);
    }

    @Override
    public void die(DamageSource source) {
        bossEvent.removeAllPlayers();
        super.die(source);
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
    public boolean canBeAffected(MobEffectInstance effect) {
        List<net.minecraft.core.Holder<MobEffect>> immune = List.of(
                MobEffects.SLOWNESS,
                MobEffects.WEAKNESS,
                com.emiliomanco.vestigia.registry.ModEffects.PARALYSIS);
        return !immune.contains(effect.getEffect()) && super.canBeAffected(effect);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }
}
