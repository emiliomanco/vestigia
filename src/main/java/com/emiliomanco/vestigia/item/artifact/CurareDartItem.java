package com.emiliomanco.vestigia.item.artifact;

import com.emiliomanco.vestigia.client.render.CurareDartRenderers;
import com.geckolib.animatable.GeoItem;
import com.geckolib.animatable.client.GeoRenderProvider;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.util.GeckoLibUtil;
import java.util.function.Consumer;
import net.minecraft.world.item.Item;

public class CurareDartItem extends Item implements GeoItem {

    private final CurareDart dart;
    private final AnimatableInstanceCache animationCache = GeckoLibUtil.createInstanceCache(this);

    public CurareDartItem(Properties properties, CurareDart dart) {
        super(properties);
        this.dart = dart;
    }

    public CurareDart dart() {
        return dart;
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
            private GeoItemRenderer<?> renderer;

            @Override
            public GeoItemRenderer<?> getGeoItemRenderer() {
                if (renderer == null) {
                    renderer = CurareDartRenderers.inHand(dart);
                }
                return renderer;
            }
        });
    }
}
