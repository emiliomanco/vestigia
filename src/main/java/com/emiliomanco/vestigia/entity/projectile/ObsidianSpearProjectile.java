package com.emiliomanco.vestigia.entity.projectile;

import com.emiliomanco.vestigia.registry.ModEffects;
import com.emiliomanco.vestigia.registry.ModEntities;
import com.emiliomanco.vestigia.registry.ModItems;
import com.emiliomanco.vestigia.registry.ModSounds;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.util.GeckoLibUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class ObsidianSpearProjectile extends AbstractArrow implements GeoEntity {

    private static final int BLEED_DURATION = 80;
    private static final float THROWN_DAMAGE = 8.0F;

    private boolean dealtDamage;

    private static final EntityDataAccessor<Byte> LOYALTY =
            SynchedEntityData.defineId(ObsidianSpearProjectile.class, EntityDataSerializers.BYTE);

    private int returningTicks;

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

    public ObsidianSpearProjectile(EntityType<? extends ObsidianSpearProjectile> type, Level level) {
        super(type, level);
    }

    public ObsidianSpearProjectile(Level level, LivingEntity owner, ItemStack spear) {
        super(ModEntities.OBSIDIAN_SPEAR.get(), owner, level, spear, null);
        entityData.set(LOYALTY, loyaltyOf(spear));
    }

    public ObsidianSpearProjectile(Level level, double x, double y, double z, ItemStack spear) {
        super(ModEntities.OBSIDIAN_SPEAR.get(), x, y, z, level, spear, spear);
        entityData.set(LOYALTY, loyaltyOf(spear));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(LOYALTY, (byte) 0);
    }

    private byte loyaltyOf(ItemStack spear) {
        return level() instanceof ServerLevel serverLevel
                ? (byte) Mth.clamp(EnchantmentHelper.getTridentReturnToOwnerAcceleration(
                        serverLevel, spear, this), 0, 127)
                : 0;
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        Entity struck = hitResult.getEntity();
        Entity thrower = getOwner();
        dealtDamage = true;

        DamageSource source = damageSources().trident(this, thrower == null ? this : thrower);
        if (struck.hurtOrSimulate(source, THROWN_DAMAGE)) {
            if (thrower instanceof LivingEntity livingThrower) {
                livingThrower.setLastHurtMob(struck);
            }
            if (level() instanceof ServerLevel && struck instanceof LivingEntity wounded) {
                wounded.addEffect(new MobEffectInstance(ModEffects.BLEED, BLEED_DURATION, 0), thrower);
            }
        }

        setDeltaMovement(getDeltaMovement().multiply(-0.01D, -0.1D, -0.01D));
        playSound(ModSounds.SPEAR_IMPACT.get(), 1.0F, 1.0F);
    }

    @Override
    protected @Nullable EntityHitResult findHitEntity(Vec3 from, Vec3 to) {
        return dealtDamage ? null : super.findHitEntity(from, to);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        dealtDamage = input.getBooleanOr("DealtDamage", false);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("DealtDamage", dealtDamage);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(ModItems.OBSIDIAN_SPEAR.get());
    }

    @Override
    protected SoundEvent getDefaultHitGroundSoundEvent() {
        return ModSounds.SPEAR_IMPACT.get();
    }

    @Override
    protected void tickDespawn() {
    }

    @Override
    public void tick() {
        int loyalty = entityData.get(LOYALTY);
        Entity owner = getOwner();

        if (loyalty > 0 && owner != null && (dealtDamage || inGroundTime > 4 || isNoPhysics())) {
            if (!owner.isAlive() || (owner instanceof ServerPlayer spectating && spectating.isSpectator())) {
                if (level() instanceof ServerLevel serverLevel && pickup == Pickup.ALLOWED) {
                    spawnAtLocation(serverLevel, getPickupItem(), 0.1F);
                }
                discard();
                return;
            }

            setNoPhysics(true);
            Vec3 toOwner = owner.getEyePosition().subtract(position());
            setPosRaw(getX(), getY() + toOwner.y * 0.015D * loyalty, getZ());
            setDeltaMovement(getDeltaMovement().scale(0.95D)
                    .add(toOwner.normalize().scale(0.05D * loyalty)));
            if (returningTicks == 0) {
                playSound(ModSounds.SPEAR_THROW.get(), 10.0F, 1.0F);
            }
            returningTicks++;
        }

        super.tick();
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }
}
