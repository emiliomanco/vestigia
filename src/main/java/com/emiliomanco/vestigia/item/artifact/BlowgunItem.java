package com.emiliomanco.vestigia.item.artifact;

import com.emiliomanco.vestigia.client.render.BlowgunRenderer;
import com.emiliomanco.vestigia.entity.projectile.CurareDartProjectile;
import com.emiliomanco.vestigia.registry.ModItems;
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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public class BlowgunItem extends Item implements GeoItem {

    private static final int COOLDOWN_TICKS = 20;
    private static final float VELOCITY = 2.6F;
    private static final float INACCURACY = 0.35F;

    private static final float SHOT_VOLUME = 0.6F;
    private static final float SHOT_PITCH = 1.0F;

    private static final RawAnimation SHOOT = RawAnimation.begin().thenPlay("shoot_blowgun");
    private static final String CONTROLLER = "shoot";
    private static final String SHOOT_TRIGGER = "shoot";

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

    public BlowgunItem(Properties properties) {
        super(properties);
        GeoItem.registerSyncedAnimatable(this);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || !newStack.is(this);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack blowgun = player.getItemInHand(hand);
        ItemStack ammunition = findDart(player);

        boolean free = player.hasInfiniteMaterials();
        if (ammunition.isEmpty() && !free) {
            return InteractionResult.FAIL;
        }
        CurareDart dart = free && ammunition.isEmpty()
                ? CurareDart.POISON
                : dartTypeOf(ammunition);
        if (dart == null) {
            return InteractionResult.FAIL;
        }

        if (level instanceof ServerLevel serverLevel) {
            CurareDartProjectile projectile = new CurareDartProjectile(level, player, dart);
            projectile.setFreeShot(free);
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, VELOCITY, INACCURACY);
            serverLevel.addFreshEntity(projectile);

            blowgun.hurtAndBreak(1, serverLevel, player, item -> {});
            triggerAnim(player, GeoItem.getOrAssignId(blowgun, serverLevel), CONTROLLER, SHOOT_TRIGGER);

            serverLevel.playSound(null, player.getX(), player.getY(), player.getZ(),
                    ModSounds.BLOWGUN_SHOOT.get(), SoundSource.PLAYERS, SHOT_VOLUME, SHOT_PITCH);
        }

        if (!free) {
            ammunition.shrink(1);
        }
        player.getCooldowns().addCooldown(blowgun, COOLDOWN_TICKS);
        return InteractionResult.CONSUME;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<BlowgunItem>(CONTROLLER, 0, state -> PlayState.STOP)
                .triggerableAnim(SHOOT_TRIGGER, SHOOT));
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
                    renderer = BlowgunRenderer.blowgun();
                }
                return renderer;
            }
        });
    }

    private static ItemStack findDart(Player player) {
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (dartTypeOf(stack) != null) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private static @Nullable CurareDart dartTypeOf(ItemStack stack) {
        for (CurareDart dart : CurareDart.values()) {
            if (stack.is(ModItems.curareDart(dart).get())) {
                return dart;
            }
        }
        return null;
    }
}
