package com.emiliomanco.vestigia.item.god;

import com.emiliomanco.vestigia.client.render.GodArmorRenderers;
import com.emiliomanco.vestigia.config.VestigiaConfig;
import com.emiliomanco.vestigia.item.god.pachamama.Bending;
import com.emiliomanco.vestigia.item.god.pachamama.BendingBranch;
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
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorType;
import org.jspecify.annotations.Nullable;

public class SupremeCrownItem extends Item implements GeoItem {

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

    public SupremeCrownItem(Properties properties) {
        super(properties);
    }

    public static ItemAttributeModifiers attributes() {
        return ModArmorMaterials.PACHAMAMA.createAttributes(ArmorType.HELMET);
    }

    public static ItemStack forged() {
        ItemStack stack = new ItemStack(com.emiliomanco.vestigia.registry.ModItems.SUPREME_CROWN.get());
        stack.set(com.emiliomanco.vestigia.registry.ModDataComponents.BENDING_BRANCH.get(),
                BendingBranch.EARTH);
        return stack;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable("item.vestigia.supreme_crown")
                .withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.BOLD);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
        if (slot != EquipmentSlot.HEAD || !(owner instanceof Player player)) {
            return;
        }
        if (level.getGameTime() % VestigiaConfig.PACHAMAMA_REGEN_INTERVAL_TICKS.get() != 0) {
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
