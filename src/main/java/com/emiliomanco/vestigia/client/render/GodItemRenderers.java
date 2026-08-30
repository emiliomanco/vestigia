package com.emiliomanco.vestigia.client.render;

import com.emiliomanco.vestigia.Vestigia;
import com.geckolib.animatable.GeoAnimatable;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.DefaultedItemGeoModel;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoEntityRenderer;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import com.mojang.math.Axis;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.item.Item;

public final class GodItemRenderers {
    private GodItemRenderers() {}

    public static <T extends Item & GeoAnimatable> GeoItemRenderer<T> sunDisc() {
        return translucent(model("sun_disc_of_inti", "sundisk", "intidisk", "sundisk"));
    }

    public static <T extends Item & GeoAnimatable> GeoItemRenderer<T> punchao() {
        return new GeoItemRenderer<>(model("punchao", "punchao", "punchao", null));
    }

    public static <T extends Item & GeoAnimatable> GeoItemRenderer<T> macuahuitl() {
        return new GeoItemRenderer<>(model("macuahuitl", "maquahuitl", "maquahuitl", null));
    }

    public static <T extends Item & GeoAnimatable> GeoItemRenderer<T> incanClub() {
        return new GeoItemRenderer<>(model("incan_club", "incan_club", "incan_club", null));
    }

    public static <T extends net.minecraft.world.entity.Entity & GeoAnimatable>
            GeoEntityRenderer<T, EntityRenderState> thrownSunDisc(EntityRendererProvider.Context context) {
        return new GeoEntityRenderer<T, EntityRenderState>(context, new ThrownDiscModel<>()) {
            @Override
            public RenderType getRenderType(EntityRenderState state, Identifier texture) {
                return RenderTypes.entityTranslucent(texture);
            }

            @Override
            public void submit(EntityRenderState state, PoseStack poseStack,
                               SubmitNodeCollector collector, CameraRenderState camera) {
                Vec3 velocity = ((GeoRenderState) state)
                        .getOrDefaultGeckolibData(DataTickets.VELOCITY, Vec3.ZERO);

                if (velocity.lengthSqr() > 1.0E-6) {
                    float yaw = (float) (Mth.atan2(velocity.x, velocity.z) * (180.0F / Math.PI));
                    float pitch = (float) (Mth.atan2(velocity.y, velocity.horizontalDistance())
                            * (180.0F / Math.PI));
                    poseStack.mulPose(Axis.YP.rotationDegrees(yaw));
                    poseStack.mulPose(Axis.XP.rotationDegrees(-pitch));
                }

                super.submit(state, poseStack, collector, camera);
            }
        };
    }

    public static <T extends Item & GeoAnimatable> GeoItemRenderer<T> lunarMirror() {
        return new GeoItemRenderer<>(model("lunar_mirror", "moonmirror", "moonmirror", null));
    }

    public static <T extends Item & GeoAnimatable> GeoItemRenderer<T> viracochaStaff() {
        return translucent(model("viracocha_staff", "creatorstaff", "viracochastaff", null));
    }

    private static final class ThrownDiscModel<T extends GeoAnimatable> extends GeoModel<T> {
        private static final Identifier MODEL = Vestigia.id("entity/sundisk_projectile");
        private static final Identifier ANIMATION = Vestigia.id("item/sundisk");

        private static final Identifier TEXTURE = Vestigia.id("textures/item/intidisk.png");

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

    private static <T extends GeoAnimatable> DefaultedItemGeoModel<T> model(
            String registryId, String geoName, String textureName, String animationName) {
        DefaultedItemGeoModel<T> model = new DefaultedItemGeoModel<T>(Vestigia.id(registryId))
                .withAltModel(Vestigia.id(geoName))
                .withAltTexture(Vestigia.id(textureName));
        return animationName == null ? model : model.withAltAnimations(Vestigia.id(animationName));
    }

    private static <T extends Item & GeoAnimatable> GeoItemRenderer<T> translucent(
            DefaultedItemGeoModel<T> model) {
        return new GeoItemRenderer<>(model) {
            @Override
            public RenderType getRenderType(GeoRenderState state, Identifier texture) {
                return RenderTypes.entityTranslucent(texture);
            }
        };
    }
}
