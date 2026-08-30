package com.emiliomanco.vestigia.item.god;

import com.emiliomanco.vestigia.client.render.GodItemRenderers;
import com.emiliomanco.vestigia.config.VestigiaConfig;
import com.emiliomanco.vestigia.registry.ModItems;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.renderer.GeoItemRenderer;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class ViracochaStaffItem extends GodItem {

    public ViracochaStaffItem(Properties properties) {
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
                    renderer = GodItemRenderers.viracochaStaff();
                }
                return renderer;
            }
        });
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer caster)) {
            return InteractionResult.SUCCESS;
        }

        if (TimeStop.isActive(caster)) {
            TimeStop.stop(caster);
            actionBar(caster, Component
                    .translatable("item.vestigia.viracocha_staff.stasis_end")
                    .withStyle(ChatFormatting.GRAY), true);
            return InteractionResult.CONSUME;
        }

        if (VestigiaConfig.VIRACOCHA_STASIS_COSTS_OFFERING.get() && !spendOffering(caster)) {
            actionBar(caster, Component
                    .translatable("item.vestigia.viracocha_staff.no_offering")
                    .withStyle(ChatFormatting.RED), true);
            return InteractionResult.FAIL;
        }

        TimeStop.start(caster);

        int radius = VestigiaConfig.VIRACOCHA_STASIS_RADIUS.get();
        serverLevel.sendParticles(ParticleTypes.END_ROD,
                caster.getX(), caster.getY() + 1.0D, caster.getZ(),
                60, radius * 0.4D, 1.5D, radius * 0.4D, 0.0D);

        actionBar(caster, Component
                .translatable("item.vestigia.viracocha_staff.stasis", TimeStop.heldBy(caster)), true);
        return InteractionResult.CONSUME;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || !(context.getLevel() instanceof ServerLevel level)) {
            return InteractionResult.SUCCESS;
        }
        if (!player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        int restored = MasonryLedger.restoreAround(
                level, context.getClickedPos(), VestigiaConfig.VIRACOCHA_RESTORE_RADIUS.get());

        if (restored == 0) {
            actionBar(player, Component
                    .translatable("item.vestigia.viracocha_staff.nothing_to_restore")
                    .withStyle(ChatFormatting.GRAY), true);
            return InteractionResult.PASS;
        }

        level.playSound(null, context.getClickedPos(), SoundEvents.BEACON_ACTIVATE,
                SoundSource.PLAYERS, 0.8F, 1.4F);
        level.sendParticles(ParticleTypes.END_ROD,
                context.getClickedPos().getX() + 0.5D,
                context.getClickedPos().getY() + 1.0D,
                context.getClickedPos().getZ() + 0.5D,
                30, 2.0D, 2.0D, 2.0D, 0.0D);
        actionBar(player, Component
                .translatable("item.vestigia.viracocha_staff.restored", restored)
                .withStyle(ChatFormatting.GOLD), true);
        return InteractionResult.CONSUME;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player,
                                                  LivingEntity target, InteractionHand hand) {
        if (player.level().isClientSide() || !player.isShiftKeyDown() || !canMend(target)) {
            return InteractionResult.PASS;
        }
        if (target.getHealth() >= target.getMaxHealth()) {
            return InteractionResult.PASS;
        }
        target.setHealth(target.getMaxHealth());
        target.level().playSound(null, target.blockPosition(), SoundEvents.BEACON_POWER_SELECT,
                SoundSource.PLAYERS, 0.8F, 1.6F);
        if (target.level() instanceof ServerLevel level) {
            level.sendParticles(ParticleTypes.HEART,
                    target.getX(), target.getY() + target.getBbHeight(), target.getZ(),
                    8, 0.4D, 0.4D, 0.4D, 0.0D);
        }
        return InteractionResult.CONSUME;
    }

    private static boolean canMend(LivingEntity target) {
        return !(target instanceof Enemy);
    }

    private static void actionBar(Player player, Component message, boolean overlay) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(message, overlay);
        }
    }

    private static boolean spendOffering(Player player) {
        if (player.hasInfiniteMaterials()) {
            return true;
        }
        for (ItemStack stack : player.getInventory()) {
            if (stack.is(ModItems.OFFERING.get())) {
                stack.shrink(1);
                return true;
            }
        }
        return false;
    }

}
