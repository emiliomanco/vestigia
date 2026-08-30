package com.emiliomanco.vestigia.item.god;

import com.emiliomanco.vestigia.client.render.GodItemRenderers;
import com.emiliomanco.vestigia.config.VestigiaConfig;
import com.emiliomanco.vestigia.registry.ModSounds;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.renderer.GeoItemRenderer;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class LunarMirrorItem extends GodItem {

    public LunarMirrorItem(Properties properties) {
        super(properties);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {
            private GeoItemRenderer<?> renderer;

            @Override
            public GeoItemRenderer<?> getGeoItemRenderer() {
                if (renderer == null) {
                    renderer = GodItemRenderers.lunarMirror();
                }
                return renderer;
            }
        });
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        int radius = VestigiaConfig.KILLA_TIDE_RADIUS.get();
        BlockPos centre = player.blockPosition();
        int frozen = 0;

        for (BlockPos pos : BlockPos.betweenClosed(
                centre.offset(-radius, -2, -radius), centre.offset(radius, 2, radius))) {
            if (pos.distSqr(centre) > (double) radius * radius) {
                continue;
            }
            BlockState state = level.getBlockState(pos);
            if (!state.is(Blocks.WATER) || state.getFluidState().getAmount() != 8) {
                continue;
            }
            if (!level.getBlockState(pos.above()).isAir()) {
                continue;
            }
            level.setBlockAndUpdate(pos, Blocks.FROSTED_ICE.defaultBlockState());
            level.scheduleTick(pos.immutable(), Blocks.FROSTED_ICE,
                    Mth.nextInt(level.getRandom(), 60, 120));
            frozen++;
        }

        for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(radius), candidate -> candidate != player)) {
            entity.addEffect(new MobEffectInstance(MobEffects.SLOWNESS,
                    VestigiaConfig.KILLA_TIDE_TICKS.get(), 2, false, true));
        }

        serverLevel.playSound(null, centre, ModSounds.LUNAR_MIRROR_USE.get(),
                SoundSource.PLAYERS, 0.9F, 1.0F);
        serverLevel.sendParticles(ParticleTypes.SNOWFLAKE,
                player.getX(), player.getY() + 0.5D, player.getZ(),
                40, radius * 0.5D, 0.5D, radius * 0.5D, 0.01D);

        player.getCooldowns().addCooldown(stack, VestigiaConfig.KILLA_TIDE_COOLDOWN_TICKS.get());
        return frozen > 0 ? InteractionResult.CONSUME : InteractionResult.SUCCESS;
    }

}
