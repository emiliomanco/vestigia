package com.emiliomanco.vestigia.client.render;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.entity.guardian.TempleLord;
import com.geckolib.constant.dataticket.DataTicket;
import com.geckolib.renderer.base.GeoRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import org.joml.Matrix4f;

@EventBusSubscriber(modid = Vestigia.MODID, value = Dist.CLIENT)
public final class BossWarningIcon {
    private BossWarningIcon() {}

    public static final DataTicket<Integer> WARNING =
            DataTicket.create("vestigia_boss_warning", Integer.class);

    private static final Identifier LIGHT = Vestigia.id("textures/icons/light_medium_warning.png");
    private static final Identifier HEAVY = Vestigia.id("textures/icons/heavy_warning.png");

    private static final float LIFT = 1.1F;
    private static final float SCALE = 0.045F;
    private static final float HALF = 8.0F;

    @SubscribeEvent
    static void onRenderLiving(RenderLivingEvent.Post<?, ?, ?> event) {
        if (!(event.getRenderState() instanceof LivingEntityRenderState living)) {
            return;
        }
        Integer warning = ((GeoRenderState) living).getGeckolibData(WARNING);
        if (warning == null || warning == TempleLord.WARNING_NONE) {
            return;
        }
        draw(event.getPoseStack(), event.getSubmitNodeCollector(), living,
                warning == TempleLord.WARNING_HEAVY ? HEAVY : LIGHT, living.lightCoords);
    }

    private static void draw(PoseStack poseStack, SubmitNodeCollector collector,
                             LivingEntityRenderState state, Identifier icon, int light) {
        poseStack.pushPose();
        poseStack.translate(0.0F, state.boundingBoxHeight + LIFT, 0.0F);
        Camera camera = Minecraft.getInstance().getEntityRenderDispatcher().camera;
        if (camera == null) {
            poseStack.popPose();
            return;
        }
        poseStack.mulPose(camera.rotation());
        poseStack.scale(-SCALE, -SCALE, SCALE);

        Matrix4f pose = poseStack.last().pose();
        RenderType renderType = RenderTypes.entityCutout(icon);
        collector.order(0).submitCustomGeometry(poseStack, renderType, (last, buffer) -> {
            buffer.addVertex(pose, -HALF, -HALF, 0.0F).setColor(-1)
                    .setUv(0.0F, 0.0F).setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(light).setNormal(0.0F, 0.0F, -1.0F);
            buffer.addVertex(pose, -HALF, HALF, 0.0F).setColor(-1)
                    .setUv(0.0F, 1.0F).setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(light).setNormal(0.0F, 0.0F, -1.0F);
            buffer.addVertex(pose, HALF, HALF, 0.0F).setColor(-1)
                    .setUv(1.0F, 1.0F).setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(light).setNormal(0.0F, 0.0F, -1.0F);
            buffer.addVertex(pose, HALF, -HALF, 0.0F).setColor(-1)
                    .setUv(1.0F, 0.0F).setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(light).setNormal(0.0F, 0.0F, -1.0F);
        });
        poseStack.popPose();
    }
}
