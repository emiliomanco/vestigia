package com.emiliomanco.vestigia.client.render;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.item.artifact.BlowgunItem;
import com.geckolib.cache.model.BakedGeoModel;
import com.geckolib.constant.DataTickets;
import com.geckolib.model.GeoModel;
import com.geckolib.renderer.GeoItemRenderer;
import com.geckolib.renderer.base.GeoRenderState;
import com.geckolib.renderer.base.RenderPassInfo;
import com.geckolib.renderer.layer.GeoRenderLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import org.jspecify.annotations.Nullable;

public final class BlowgunRenderer {
    private BlowgunRenderer() {}

    public static GeoItemRenderer<BlowgunItem> blowgun() {
        return new GeoItemRenderer<BlowgunItem>(new BlowgunModel())
                .withRenderLayer(FirstPersonArms::new);
    }

    private static final class BlowgunModel extends GeoModel<BlowgunItem> {
        private static final Identifier MODEL = Vestigia.id("item/blowgun_body");
        private static final Identifier TEXTURE = Vestigia.id("textures/item/blowgun.png");
        private static final Identifier ANIMATION = Vestigia.id("entity/blowgun");

        @Override
        public Identifier getModelResource(GeoRenderState state) {
            return MODEL;
        }

        @Override
        public Identifier getTextureResource(GeoRenderState state) {
            return TEXTURE;
        }

        @Override
        public Identifier getAnimationResource(BlowgunItem animatable) {
            return ANIMATION;
        }
    }

    private static final Identifier ARMS_WIDE = Vestigia.id("item/blowgun_arms");
    private static final Identifier ARMS_SLIM = Vestigia.id("item/blowgun_arms_slim");

    private static PlayerSkin playerSkin() {
        @Nullable AbstractClientPlayer player = Minecraft.getInstance().player;
        return player == null ? DefaultPlayerSkin.getDefaultSkin() : player.getSkin();
    }

    private static Identifier skinTexture() {
        return playerSkin().body().texturePath();
    }

    private static Identifier armsModelFor(PlayerSkin skin) {
        return skin.model() == PlayerModelType.SLIM ? ARMS_SLIM : ARMS_WIDE;
    }

    private static final class FirstPersonArms
            extends GeoRenderLayer<BlowgunItem, GeoItemRenderer.RenderData, GeoRenderState> {

        private final ArmsModel armsModel = new ArmsModel();

        FirstPersonArms(GeoItemRenderer<BlowgunItem> renderer) {
            super(renderer);
        }

        @Override
        public void submitRenderTask(RenderPassInfo<GeoRenderState> pass, SubmitNodeCollector collector) {
            if (!pass.willRender()) {
                return;
            }
            ItemDisplayContext perspective =
                    pass.renderState().getGeckolibData(DataTickets.ITEM_RENDER_PERSPECTIVE);
            if (perspective == null || !perspective.firstPerson()) {
                return;
            }

            BakedGeoModel arms = armsModel.getBakedModel(armsModelFor(playerSkin()));
            if (arms == null || arms.isMissingno()) {
                return;
            }

            RenderType renderType = RenderTypes.entityCutout(skinTexture());
            int light = pass.packedLight();
            int overlay = pass.packedOverlay();
            int color = pass.renderColor();

            collector.order(1).submitCustomGeometry(pass.poseStack(), renderType, (pose, buffer) -> {
                pass.poseStack().pushPose();
                pass.poseStack().last().set(pose);
                pass.renderPosed(() -> arms.render(pass, buffer, light, overlay, color));
                pass.poseStack().popPose();
            });
        }

    }

    private static final class ArmsModel extends GeoModel<BlowgunItem> {
        @Override
        public Identifier getModelResource(GeoRenderState state) {
            return armsModelFor(playerSkin());
        }

        @Override
        public Identifier getTextureResource(GeoRenderState state) {
            return skinTexture();
        }

        @Override
        public Identifier getAnimationResource(BlowgunItem animatable) {
            return armsModelFor(playerSkin());
        }
    }
}
