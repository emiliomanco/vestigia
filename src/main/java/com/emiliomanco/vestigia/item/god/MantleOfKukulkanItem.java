package com.emiliomanco.vestigia.item.god;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.client.render.GodArmorRenderers;
import com.emiliomanco.vestigia.config.VestigiaConfig;
import com.emiliomanco.vestigia.registry.ModArmorMaterials;
import com.emiliomanco.vestigia.registry.ModItems;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.renderer.GeoArmorRenderer;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.util.GeckoLibUtil;
import java.util.function.Consumer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;
import org.jspecify.annotations.Nullable;

@EventBusSubscriber(modid = Vestigia.MODID)
public class MantleOfKukulkanItem extends Item implements GeoItem {

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

    public MantleOfKukulkanItem(Properties properties) {
        super(properties);
    }

    public static ItemAttributeModifiers attributes() {
        return ModArmorMaterials.KUKULKAN.createAttributes(ArmorType.CHESTPLATE);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack worn = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!worn.is(ModItems.MANTLE_OF_KUKULKAN.get())) {
            return InteractionResult.PASS;
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }
        if (player.isShiftKeyDown()) {
            return bloom(serverLevel, player);
        }
        return dash(serverLevel, player, worn);
    }

    private static InteractionResult bloom(ServerLevel level, Player player) {
        if (!(player instanceof ServerPlayer served) || !KukulkanBloom.begin(level, served)) {
            return InteractionResult.FAIL;
        }
        level.playSound(null, player.blockPosition(), SoundEvents.AMETHYST_BLOCK_CHIME,
                SoundSource.PLAYERS, 0.9F, 0.8F);
        level.sendParticles(ParticleTypes.HAPPY_VILLAGER, player.getX(), player.getY() + 1.0D, player.getZ(),
                40, 2.0D, 1.0D, 2.0D, 0.0D);
        return InteractionResult.SUCCESS;
    }

    private static InteractionResult dash(ServerLevel serverLevel, Player player, ItemStack worn) {
        if (player.getCooldowns().isOnCooldown(worn)) {
            return InteractionResult.FAIL;
        }

        Vec3 direction = player.getLookAngle().normalize()
                .scale(VestigiaConfig.KUKULKAN_DASH_SPEED.get());
        player.setDeltaMovement(direction);
        player.hurtMarked = true;
        player.resetFallDistance();

        if (!player.hasInfiniteMaterials()) {
            player.getCooldowns().addCooldown(worn, VestigiaConfig.KUKULKAN_DASH_COOLDOWN_TICKS.get());
        }

        serverLevel.playSound(null, player.blockPosition(), SoundEvents.BREEZE_SHOOT,
                SoundSource.PLAYERS, 0.7F, 1.3F);
        serverLevel.sendParticles(ParticleTypes.GUST, player.getX(), player.getY() + 0.8D, player.getZ(),
                6, 0.3D, 0.3D, 0.3D, 0.02D);
        return InteractionResult.SUCCESS;
    }

    @SubscribeEvent
    static void onDamageDealt(LivingDamageEvent.Post event) {
        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof Player attacker) || source.getDirectEntity() != attacker) {
            return;
        }
        if (event.getEntity() == attacker || event.getHealthDamage() <= 0.0F) {
            return;
        }
        if (!attacker.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.MANTLE_OF_KUKULKAN.get())) {
            return;
        }
        event.getEntity().addEffect(new MobEffectInstance(MobEffects.POISON,
                VestigiaConfig.KUKULKAN_POISON_TICKS.get(), POISON_II), attacker);
    }

    private static final int POISON_II = 1;

    @SubscribeEvent
    static void onFall(LivingFallEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.MANTLE_OF_KUKULKAN.get())) {
            event.setCanceled(true);
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animationCache;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoArmorRenderer<?, ?> renderer;
            private GeoItemRenderer<?> heldRenderer;

            @Override
            public GeoItemRenderer<?> getGeoItemRenderer() {
                if (heldRenderer == null) {
                    heldRenderer = GodArmorRenderers.mantleOfKukulkanInHand();
                }
                return heldRenderer;
            }

            @Override
            public GeoArmorRenderer<?, ?> getGeoArmorRenderer(@Nullable ItemStack stack, @Nullable EquipmentSlot slot) {
                if (renderer == null) {
                    renderer = GodArmorRenderers.mantleOfKukulkan();
                }
                return renderer;
            }
        });
    }

}
