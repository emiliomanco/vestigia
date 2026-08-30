package com.emiliomanco.vestigia.item.god;

import com.emiliomanco.vestigia.client.render.GodItemRenderers;
import com.emiliomanco.vestigia.config.VestigiaConfig;
import com.emiliomanco.vestigia.registry.ModSounds;
import com.emiliomanco.vestigia.entity.projectile.ThrownSunDisc;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.AnimationController;
import com.geckolib.animation.RawAnimation;
import com.geckolib.renderer.GeoItemRenderer;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public class SunDiscOfIntiItem extends GodItem {

    private static final RawAnimation ACTIVE = RawAnimation.begin().thenLoop("active.disk");

    private static final int AURA_INTERVAL_TICKS = 10;

    private static final Set<UUID> IN_FLIGHT = ConcurrentHashMap.newKeySet();

    public SunDiscOfIntiItem(Properties properties) {
        super(properties);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("main", 0, state -> state.setAndContinue(ACTIVE)));
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoItemRenderer<?> renderer;

            @Override
            public GeoItemRenderer<?> getGeoItemRenderer() {
                if (renderer == null) {
                    renderer = GodItemRenderers.sunDisc();
                }
                return renderer;
            }
        });
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
        if (!(owner instanceof Player player) || level.getGameTime() % AURA_INTERVAL_TICKS != 0) {
            return;
        }

        float power = powerAt(level, player.blockPosition());
        double radius = VestigiaConfig.INTI_AURA_RADIUS.get();

        for (Player ally : level.getEntitiesOfClass(Player.class,
                player.getBoundingBox().inflate(radius))) {
            ally.addEffect(new MobEffectInstance(MobEffects.REGENERATION,
                    AURA_INTERVAL_TICKS + 20, power >= 1.0F ? 1 : 0, true, false));
        }

    }

    public static float powerAt(Level level, BlockPos pos) {
        long dayTime = Math.floorMod(level.getOverworldClockTime(), 24000L);
        boolean sunlit = dayTime < 12000L && level.canSeeSky(pos) && !level.isRaining();
        return sunlit ? 1.0F : (float) (double) VestigiaConfig.INTI_WEAKENED_MULTIPLIER.get();
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }
        if (IN_FLIGHT.contains(player.getUUID())) {
            return InteractionResult.FAIL;
        }

        float power = powerAt(level, player.blockPosition());
        float damage = (float) (VestigiaConfig.INTI_THROW_DAMAGE.get() * power);

        ThrownSunDisc disc = new ThrownSunDisc(level, player, damage, power < 1.0F);
        disc.setDeltaMovement(player.getLookAngle().scale(ThrownSunDisc.launchSpeed()));
        serverLevel.addFreshEntity(disc);

        IN_FLIGHT.add(player.getUUID());
        stack.setCount(0);

        serverLevel.playSound(null, player.blockPosition(), ModSounds.SUN_DISC_THROW.get(),
                SoundSource.PLAYERS, 1.0F, 1.5F);
        player.getCooldowns().addCooldown(getDefaultInstance(),
                VestigiaConfig.INTI_THROW_COOLDOWN_TICKS.get());
        return InteractionResult.CONSUME;
    }

    public static void clearThrownFlag(Player player) {
        IN_FLIGHT.remove(player.getUUID());
    }

    public static void forget(UUID playerId) {
        IN_FLIGHT.remove(playerId);
    }

}
