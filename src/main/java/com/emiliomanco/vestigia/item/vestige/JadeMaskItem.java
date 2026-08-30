package com.emiliomanco.vestigia.item.vestige;

import com.emiliomanco.vestigia.client.render.GodArmorRenderers;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.renderer.GeoArmorRenderer;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.util.GeckoLibUtil;
import java.util.function.Consumer;
import net.minecraft.world.entity.EquipmentSlot;
import org.jspecify.annotations.Nullable;
import net.minecraft.world.item.ItemStack;

public class JadeMaskItem extends VestigeItem implements GeoItem {

    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

    public JadeMaskItem(Properties properties) {
        super(properties);
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
            private GeoArmorRenderer<?, ?> worn;
            private GeoItemRenderer<?> held;

            @Override
            public GeoItemRenderer<?> getGeoItemRenderer() {
                if (held == null) {
                    held = GodArmorRenderers.jadeMaskInHand();
                }
                return held;
            }

            @Override
            public GeoArmorRenderer<?, ?> getGeoArmorRenderer(@Nullable ItemStack stack, @Nullable EquipmentSlot slot) {
                if (worn == null) {
                    worn = GodArmorRenderers.jadeMask();
                }
                return worn;
            }
        });
    }
}
