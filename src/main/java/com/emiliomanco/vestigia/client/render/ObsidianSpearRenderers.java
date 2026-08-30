package com.emiliomanco.vestigia.client.render;

import com.emiliomanco.vestigia.entity.projectile.ObsidianSpearProjectile;
import com.emiliomanco.vestigia.item.artifact.ObsidianSpearItem;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;

public final class ObsidianSpearRenderers {
    private ObsidianSpearRenderers() {}

    public static GeoItemRenderer<ObsidianSpearItem> held() {
        return new GeoItemRenderer<>(new SpearModel<ObsidianSpearItem>());
    }

    public static class InFlight extends GeoEntityRenderer<ObsidianSpearProjectile, EntityRenderState> {

        private static final float SHAFT_MIDPOINT = 15.5F / 16.0F;

        private static final DataTicket<Float> YAW = DataTicket.create("vestigia_spear_yaw", Float.class);
        private static final DataTicket<Float> PITCH = DataTicket.create("vestigia_spear_pitch", Float.class);

        public InFlight(EntityRendererProvider.Context context) {
            super(context, new SpearModel<ObsidianSpearProjectile>());
            this.shadowRadius = 0.0F;
        }

        @Override
        public void addRenderData(ObsidianSpearProjectile spear, Void unused,
                                  EntityRenderState state, float partialTick) {
            super.addRenderData(spear, unused, state, partialTick);
            GeoRenderState geoState = (GeoRenderState) state;
            geoState.addGeckolibData(YAW, spear.getYRot(partialTick));
            geoState.addGeckolibData(PITCH, spear.getXRot(partialTick));
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        protected void applyRotations(RenderPassInfo pass, PoseStack poseStack, float scale) {
            GeoRenderState state = (GeoRenderState) pass.renderState();
            poseStack.mulPose(Axis.YP.rotationDegrees(state.getOrDefaultGeckolibData(YAW, 0.0F) - 90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(state.getOrDefaultGeckolibData(PITCH, 0.0F) - 90.0F));
            poseStack.translate(0.0F, -SHAFT_MIDPOINT, 0.0F);
        }
    }

    private static final class SpearModel<T extends GeoAnimatable> extends GeoModel<T> {
        @Override
        public Identifier getModelResource(GeoRenderState state) {
            return ObsidianSpearItem.MODEL;
        }

        @Override
        public Identifier getTextureResource(GeoRenderState state) {
            return ObsidianSpearItem.TEXTURE;
        }

        @Override
        public Identifier getAnimationResource(T animatable) {
            return ObsidianSpearItem.ANIMATION;
        }
    }
}
