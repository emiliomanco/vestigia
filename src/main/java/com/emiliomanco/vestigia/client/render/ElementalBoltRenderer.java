package com.emiliomanco.vestigia.client.render;

import com.emiliomanco.vestigia.entity.projectile.ElementalBolt;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ThrownItemRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;

public class ElementalBoltRenderer extends EntityRenderer<ElementalBolt, ThrownItemRenderState> {

    private static final float ORDINARY_SCALE = 2.0F;
    private static final float GIANT_SCALE = 4.0F;

    private final ItemModelResolver itemModelResolver;

    public ElementalBoltRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.itemModelResolver = context.getItemModelResolver();
    }

    public static class BoltRenderState extends ThrownItemRenderState {
        public float scale = ORDINARY_SCALE;
        public boolean giant;
    }

    @Override
    public BoltRenderState createRenderState() {
        return new BoltRenderState();
    }

    @Override
    protected int getBlockLightLevel(ElementalBolt entity, BlockPos blockPos) {
        return entity.isGiant() ? 15 : super.getBlockLightLevel(entity, blockPos);
    }

    @Override
    public void extractRenderState(ElementalBolt entity, ThrownItemRenderState state, float partialTicks) {
        super.extractRenderState(entity, state, partialTicks);
        if (state instanceof BoltRenderState bolt) {
            bolt.giant = entity.isGiant();
            bolt.scale = bolt.giant ? GIANT_SCALE : ORDINARY_SCALE;
        }
        itemModelResolver.updateForNonLiving(state.item, entity.getItem(), ItemDisplayContext.GROUND, entity);
    }

    @Override
    public void submit(ThrownItemRenderState state, PoseStack poseStack,
                       SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        float scale = state instanceof BoltRenderState bolt ? bolt.scale : ORDINARY_SCALE;
        poseStack.pushPose();
        poseStack.scale(scale, scale, scale);
        poseStack.mulPose(camera.orientation);
        state.item.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY,
                state.outlineColor);
        poseStack.popPose();
        super.submit(state, poseStack, submitNodeCollector, camera);
    }
}
