package com.emiliomanco.vestigia.client.render;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.entity.AirScooter;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.resources.Identifier;

public class AirScooterRenderer extends GeoEntityRenderer<AirScooter, EntityRenderState> {

    public AirScooterRenderer(EntityRendererProvider.Context context) {
        super(context, new AirBallModel<>());
        this.shadowRadius = 0.0F;
    }

    private static final class AirBallModel<T extends GeoAnimatable> extends GeoModel<T> {
        private static final Identifier MODEL = Vestigia.id("entity/airball");
        private static final Identifier ANIMATION = Vestigia.id("entity/airball");
        private static final Identifier TEXTURE = Vestigia.id("textures/entity/airball.png");

        @Override
        public Identifier getModelResource(GeoRenderState state) {
            return MODEL;
        }

        @Override
        public Identifier getTextureResource(GeoRenderState state) {
            return TEXTURE;
        }

        @Override
        public Identifier getAnimationResource(T animatable) {
            return ANIMATION;
        }
    }

    @Override
    public RenderType getRenderType(EntityRenderState state, Identifier texture) {
        return RenderTypes.entityTranslucent(texture);
    }
}
