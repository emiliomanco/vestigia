package com.emiliomanco.vestigia.item.god;

import com.emiliomanco.vestigia.client.render.GodArmorRenderers;
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
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.equipment.ArmorType;
import org.jspecify.annotations.Nullable;

public class OtorongoHelmItem extends Item implements GeoItem {

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

    public static final double BONUS_ATTACK_DAMAGE = 4.0D;
    public static final double ATTACK_SPEED_BONUS = 0.20D;

    private static final Identifier DAMAGE_MODIFIER_ID = Identifier.fromNamespaceAndPath("vestigia", "otorongo_damage");
    private static final Identifier SPEED_MODIFIER_ID = Identifier.fromNamespaceAndPath("vestigia", "otorongo_speed");

    public OtorongoHelmItem(Properties properties) {
        super(properties);
    }

    public static ItemAttributeModifiers attributes() {
        return ModArmorMaterials.OTORONGO.createAttributes(ArmorType.HELMET)
                .withModifierAdded(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(DAMAGE_MODIFIER_ID, BONUS_ATTACK_DAMAGE, AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.HEAD)
                .withModifierAdded(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(SPEED_MODIFIER_ID, ATTACK_SPEED_BONUS, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL),
                        EquipmentSlotGroup.HEAD);
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
                    heldRenderer = GodArmorRenderers.otorongoHelmInHand();
                }
                return heldRenderer;
            }

            @Override
            public GeoArmorRenderer<?, ?> getGeoArmorRenderer(@Nullable ItemStack stack, @Nullable EquipmentSlot slot) {
                if (renderer == null) {
                    renderer = GodArmorRenderers.otorongoHelm();
                }
                return renderer;
            }
        });
    }

}
