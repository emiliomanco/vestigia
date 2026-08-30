package com.emiliomanco.vestigia.client.render;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.client.animation.PinnedDownAnimation;
import com.emiliomanco.vestigia.entity.PinnedState;
import com.mojang.math.Axis;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Avatar;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderPlayerEvent;
import net.neoforged.neoforge.client.renderstate.AvatarRenderStateModifier;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;

@EventBusSubscriber(modid = Vestigia.MODID, value = Dist.CLIENT)
public final class PinnedPlayerRenderer {
    private PinnedPlayerRenderer() {}

    private static final ContextKey<Boolean> PINNED = new ContextKey<>(Vestigia.id("pinned"));

    private static final float PIVOT_HEIGHT = 0.75F;

    public static boolean isPinned(AvatarRenderState state) {
        return Boolean.TRUE.equals(state.getRenderData(PINNED));
    }

    @SubscribeEvent
    static void onRegisterRenderStateModifiers(RegisterRenderStateModifiersEvent event) {
        event.registerAvatarEntityModifier(new AvatarRenderStateModifier() {
            @Override
            public <T extends Avatar & ClientAvatarEntity> void accept(T avatar, AvatarRenderState state) {
                state.setRenderData(PINNED, PinnedState.isPinned(avatar));
            }
        });
    }

    @SubscribeEvent
    static void onRenderPlayerPre(RenderPlayerEvent.Pre<?> event) {
        AvatarRenderState state = event.getRenderState();
        if (!isPinned(state)) {
            return;
        }
        var poseStack = event.getPoseStack();
        poseStack.pushPose();

        poseStack.translate(0.0F, PinnedDownAnimation.BODY_OFFSET_BLOCKS, 0.0F);

        poseStack.translate(0.0F, PIVOT_HEIGHT, 0.0F);
        poseStack.mulPose(Axis.XP.rotationDegrees(PinnedDownAnimation.BODY_PITCH_DEGREES));
        poseStack.translate(0.0F, -PIVOT_HEIGHT, 0.0F);
    }

    @SubscribeEvent
    static void onRenderPlayerPost(RenderPlayerEvent.Post<?> event) {
        if (!isPinned(event.getRenderState())) {
            return;
        }
        event.getPoseStack().popPose();
    }
}
