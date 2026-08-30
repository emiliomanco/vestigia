package com.emiliomanco.vestigia.mixin;

import com.emiliomanco.vestigia.entity.PinnedState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityPinnedMixin {

    @Unique
    private static final float VESTIGIA$PINNED_HEIGHT = 0.6F;

    @Unique
    private static final float VESTIGIA$PINNED_EYE_HEIGHT = 0.45F;

    @Unique
    private boolean vestigia$wasPinned;

    @Unique
    private static final AttributeModifier VESTIGIA$PINNED_KNOCKBACK = new AttributeModifier(
            Identifier.fromNamespaceAndPath("vestigia", "pinned_knockback"),
            1.0D, AttributeModifier.Operation.ADD_VALUE);

    @Inject(method = "getDimensions", at = @At("RETURN"), cancellable = true)
    private void vestigia$pinnedDimensions(Pose pose, CallbackInfoReturnable<EntityDimensions> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof Player) || !PinnedState.isPinned(self)) {
            return;
        }
        EntityDimensions vanilla = cir.getReturnValue();
        cir.setReturnValue(EntityDimensions
                .scalable(vanilla.width(), VESTIGIA$PINNED_HEIGHT)
                .withEyeHeight(VESTIGIA$PINNED_EYE_HEIGHT));
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void vestigia$refreshWhilePinned(CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof Player)) {
            return;
        }
        boolean pinned = PinnedState.isPinned(self);
        if (pinned || vestigia$wasPinned) {
            self.refreshDimensions();
        }
        vestigia$wasPinned = pinned;

        AttributeInstance knockback = self.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
        if (knockback == null) {
            return;
        }
        if (pinned) {
            if (!knockback.hasModifier(VESTIGIA$PINNED_KNOCKBACK.id())) {
                knockback.addTransientModifier(VESTIGIA$PINNED_KNOCKBACK);
            }
        } else {
            knockback.removeModifier(VESTIGIA$PINNED_KNOCKBACK.id());
        }
    }
}
