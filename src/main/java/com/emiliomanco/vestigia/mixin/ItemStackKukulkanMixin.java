package com.emiliomanco.vestigia.mixin;

import com.emiliomanco.vestigia.registry.ModItems;
import java.util.function.Consumer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class ItemStackKukulkanMixin {

    @Inject(method = "hurtAndBreak(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V",
            at = @At("HEAD"), cancellable = true)
    private void vestigia$mantleSparesTools(int amount, ServerLevel level, @Nullable LivingEntity owner,
                                            Consumer<Item> onBreak, CallbackInfo callback) {
        if (amount <= 0 || !(owner instanceof ServerPlayer player)) {
            return;
        }
        if (!player.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.MANTLE_OF_KUKULKAN.get())) {
            return;
        }
        ItemStack self = (ItemStack) (Object) this;
        Equippable equippable = self.get(DataComponents.EQUIPPABLE);
        if (equippable != null && equippable.slot().isArmor()) {
            return;
        }
        callback.cancel();
    }
}
