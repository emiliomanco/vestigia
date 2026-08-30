package com.emiliomanco.vestigia.client.render;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.entity.projectile.CurareDartProjectile;
import com.emiliomanco.vestigia.item.artifact.CurareDart;
import com.emiliomanco.vestigia.item.artifact.CurareDartItem;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;

public final class CurareDartRenderers {
    private CurareDartRenderers() {}

    private static final Map<CurareDart, Identifier> TEXTURES = textures();

    private static Map<CurareDart, Identifier> textures() {
        Map<CurareDart, Identifier> byDart = new EnumMap<>(CurareDart.class);
        for (CurareDart dart : CurareDart.values()) {
            byDart.put(dart, Vestigia.id("textures/entity/" + dart.id() + "_dart.png"));
        }
        return Map.copyOf(byDart);
    }

    private static final DataTicket<CurareDart> DART_TYPE =
            DataTicket.create("vestigia_dart_type", CurareDart.class);

    private static final DataTicket<Float> DART_YAW =
            DataTicket.create("vestigia_dart_yaw", Float.class);

    private static final DataTicket<Float> DART_PITCH =
            DataTicket.create("vestigia_dart_pitch", Float.class);

    public static class InFlight extends GeoEntityRenderer<CurareDartProjectile, EntityRenderState> {

        public InFlight(EntityRendererProvider.Context context) {
            super(context, new FlyingDartModel());
            this.shadowRadius = 0.0F;
        }

        @Override
        public void addRenderData(CurareDartProjectile dart, Void unused,
                                  EntityRenderState state, float partialTick) {
            super.addRenderData(dart, unused, state, partialTick);
            GeoRenderState geoState = (GeoRenderState) state;
            geoState.addGeckolibData(DART_TYPE, dart.dart());
            geoState.addGeckolibData(DART_YAW, dart.getYRot(partialTick));
            geoState.addGeckolibData(DART_PITCH, dart.getXRot(partialTick));
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        @Override
        protected void applyRotations(RenderPassInfo pass, PoseStack poseStack, float scale) {
            GeoRenderState state = (GeoRenderState) pass.renderState();
            float yaw = state.getOrDefaultGeckolibData(DART_YAW, 0.0F);
            float pitch = state.getOrDefaultGeckolibData(DART_PITCH, 0.0F);
            poseStack.mulPose(Axis.YP.rotationDegrees(yaw - 90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(pitch + 90.0F));
        }
    }

    private static final class FlyingDartModel extends GeoModel<CurareDartProjectile> {
        private static final Identifier MODEL = Vestigia.id("entity/blowgun_dart");

        @Override
        public Identifier getModelResource(GeoRenderState state) {
            return MODEL;
        }

        @Override
        public Identifier getTextureResource(GeoRenderState state) {
            return TEXTURES.get(state.getOrDefaultGeckolibData(DART_TYPE, CurareDart.POISON));
        }

        @Override
        public Identifier getAnimationResource(CurareDartProjectile animatable) {
            return MODEL;
        }
    }

    public static GeoItemRenderer<CurareDartItem> inHand(CurareDart dart) {
        return new GeoItemRenderer<>(new HeldDartModel<>(TEXTURES.get(dart)));
    }

    private static final class HeldDartModel<T extends GeoAnimatable> extends GeoModel<T> {
        private static final Identifier MODEL = Vestigia.id("item/blowgun_dart_item");

        private final Identifier texture;

        HeldDartModel(Identifier texture) {
            this.texture = texture;
        }

        @Override
        public Identifier getModelResource(GeoRenderState state) {
            return MODEL;
        }

        @Override
        public Identifier getTextureResource(GeoRenderState state) {
            return texture;
        }

        @Override
        public Identifier getAnimationResource(T animatable) {
            return MODEL;
        }
    }
}
