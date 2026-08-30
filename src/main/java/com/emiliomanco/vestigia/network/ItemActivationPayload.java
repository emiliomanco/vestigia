package com.emiliomanco.vestigia.network;

import com.emiliomanco.vestigia.Vestigia;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;

public record ItemActivationPayload(ItemStack stack) implements CustomPacketPayload {

    public static final Type<ItemActivationPayload> TYPE = new Type<>(Vestigia.id("item_activation"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemActivationPayload> STREAM_CODEC =
            ItemStack.STREAM_CODEC.map(ItemActivationPayload::new, ItemActivationPayload::stack);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
