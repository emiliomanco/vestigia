package com.emiliomanco.vestigia.item.god;

import com.emiliomanco.vestigia.client.render.GodArmorRenderers;
import com.emiliomanco.vestigia.config.VestigiaConfig;
import com.emiliomanco.vestigia.registry.ModArmorMaterials;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.renderer.GeoArmorRenderer;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.util.GeckoLibUtil;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import com.emiliomanco.vestigia.item.god.pachamama.Bending;
import com.emiliomanco.vestigia.item.god.pachamama.BendingBranch;
import com.emiliomanco.vestigia.registry.ModDataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class CrownOfPachamamaItem extends Item implements GeoItem {

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

    public CrownOfPachamamaItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        BendingBranch branch = Bending.branchOf(stack);
        if (branch == null) {
            return super.getName(stack);
        }
        return Component.translatable("item.vestigia.corona_pachamama.named",
                        Component.translatable(branch.nameKey()))
                .withStyle(branch.colour());
    }

    public static ItemAttributeModifiers attributes() {
        return ModArmorMaterials.PACHAMAMA.createAttributes(ArmorType.HELMET);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (player.getItemInHand(hand).get(ModDataComponents.BENDING_BRANCH.get()) != null) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            com.emiliomanco.vestigia.client.BendingClient.openBranchChooser();
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
        if (slot != EquipmentSlot.HEAD || !(owner instanceof Player player)) {
            return;
        }
        int interval = VestigiaConfig.PACHAMAMA_REGEN_INTERVAL_TICKS.get();
        if (level.getGameTime() % interval != 0) {
            return;
        }

        if (player instanceof ServerPlayer served) {
            BendingBranch branch = Bending.branchOf(stack);
            if (branch == BendingBranch.WATER) {
                Bending.waterPassives(level, served);
            } else if (branch == BendingBranch.FIRE) {
                Bending.firePassives(level, served);
            }
        }

        if (!standingOnEarth(level, player)) {
            return;
        }
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,
                interval + 20, VestigiaConfig.PACHAMAMA_REGEN_AMPLIFIER.get(), true, false));
    }

    private static boolean standingOnEarth(ServerLevel level, Player player) {
        BlockPos below = BlockPos.containing(player.getX(), player.getBoundingBox().minY - 0.2D, player.getZ());
        BlockState state = level.getBlockState(below);
        return state.is(BlockTags.DIRT)
                || state.is(BlockTags.BASE_STONE_OVERWORLD)
                || state.is(BlockTags.SAND)
                || state.is(BlockTags.TERRACOTTA)
                || state.is(BlockTags.LOGS)
                || state.is(BlockTags.SNOW);
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
                    heldRenderer = GodArmorRenderers.crownOfPachamamaInHand();
                }
                return heldRenderer;
            }

            @Override
            public GeoArmorRenderer<?, ?> getGeoArmorRenderer(@Nullable ItemStack stack, @Nullable EquipmentSlot slot) {
                if (renderer == null) {
                    renderer = GodArmorRenderers.crownOfPachamama();
                }
                return renderer;
            }
        });
    }

}
