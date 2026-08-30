package com.emiliomanco.vestigia.item.artifact;

import com.emiliomanco.vestigia.client.render.ObsidianSpearRenderers;
import com.emiliomanco.vestigia.entity.projectile.ObsidianSpearProjectile;
import com.emiliomanco.vestigia.registry.ModEffects;
import com.emiliomanco.vestigia.registry.ModSounds;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.object.PlayState;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.util.GeckoLibUtil;
import java.util.function.Consumer;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;

public class ObsidianSpearItem extends Item implements GeoItem {

    private static final int THROW_THRESHOLD_TICKS = 10;
    private static final float THROW_POWER = 2.5F;

    private static final float BLEED_CHANCE = 0.25F;
    private static final int BLEED_DURATION = 80;

    private static final RawAnimation HIT = RawAnimation.begin().thenPlay("hit_animation");
    private static final String CONTROLLER = "hit";

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

    public ObsidianSpearItem(Properties properties) {
        super(properties);
        GeoItem.registerSyncedAnimatable(this);
    }

    public static ItemAttributeModifiers attributes() {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 7.0D, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.8D, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build();
    }

    @Override
    public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        super.hurtEnemy(stack, target, attacker);
        if (!(attacker.level() instanceof ServerLevel serverLevel)) {
            return;
        }
        if (attacker.getRandom().nextFloat() < BLEED_CHANCE) {
            target.addEffect(new MobEffectInstance(ModEffects.BLEED, BLEED_DURATION, 0), attacker);
        }
        triggerAnim(attacker, GeoItem.getOrAssignId(stack, serverLevel), CONTROLLER, CONTROLLER);
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.TRIDENT;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.nextDamageWillBreak()) {
            return InteractionResult.FAIL;
        }
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level level, LivingEntity user, int remainingTime) {
        if (!(user instanceof Player player)) {
            return false;
        }
        if (getUseDuration(stack, user) - remainingTime < THROW_THRESHOLD_TICKS || stack.nextDamageWillBreak()) {
            return false;
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        if (!(level instanceof ServerLevel serverLevel)) {
            return true;
        }

        stack.hurtWithoutBreaking(1, player);
        ItemStack thrown = stack.consumeAndReturn(1, player);
        ObsidianSpearProjectile spear = Projectile.spawnProjectileFromRotation(
                ObsidianSpearProjectile::new, serverLevel, thrown, player, 0.0F, THROW_POWER, 1.0F);
        if (player.hasInfiniteMaterials()) {
            spear.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
        }
        level.playSound(null, spear, ModSounds.SPEAR_THROW.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        return true;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<ObsidianSpearItem>(CONTROLLER, 0, state -> PlayState.STOP)
                .triggerableAnim(CONTROLLER, HIT));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoItemRenderer<?> renderer;

            @Override
            public GeoItemRenderer<?> getGeoItemRenderer() {
                if (renderer == null) {
                    renderer = ObsidianSpearRenderers.held();
                }
                return renderer;
            }
        });
    }

    public static final Identifier MODEL = Identifier.fromNamespaceAndPath("vestigia", "item/obsidian_spear");
    public static final Identifier TEXTURE =
            Identifier.fromNamespaceAndPath("vestigia", "textures/item/obsidianspear.png");
    public static final Identifier ANIMATION = Identifier.fromNamespaceAndPath("vestigia", "item/spear");
}
