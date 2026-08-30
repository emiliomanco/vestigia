package com.emiliomanco.vestigia.mixin;

import com.emiliomanco.vestigia.entity.PinnedState;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerPinnedPoseMixin {

    @Inject(method = "updatePlayerPose", at = @At("HEAD"), cancellable = true)
    private void vestigia$holdPinnedPose(CallbackInfo ci) {
        Player self = (Player) (Object) this;
        if (!PinnedState.isPinned(self)) {
            return;
        }
        self.setPose(Pose.STANDING);
        ci.cancel();
    }
}
