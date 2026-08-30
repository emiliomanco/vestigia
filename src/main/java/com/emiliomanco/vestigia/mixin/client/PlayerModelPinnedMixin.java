package com.emiliomanco.vestigia.mixin.client;

import com.emiliomanco.vestigia.client.animation.PinnedDownAnimation;
import com.emiliomanco.vestigia.client.render.PinnedPlayerRenderer;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public abstract class PlayerModelPinnedMixin {

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)V",
            at = @At("TAIL"))
    private void vestigia$poseWhilePinned(AvatarRenderState state, CallbackInfo ci) {
        if (!PinnedPlayerRenderer.isPinned(state)) {
            return;
        }
        PinnedDownAnimation.apply((PlayerModel) (Object) this, state.ageInTicks);
    }
}
