package com.emiliomanco.vestigia.network;

import com.emiliomanco.vestigia.Vestigia;
import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record StasisSyncPayload(List<Integer> frozenIds) implements CustomPacketPayload {

    public static final Type<StasisSyncPayload> TYPE = new Type<>(Vestigia.id("stasis_sync"));

    public static final StreamCodec<ByteBuf, StasisSyncPayload> STREAM_CODEC =
            ByteBufCodecs.VAR_INT.apply(ByteBufCodecs.list()).map(
                    StasisSyncPayload::new, StasisSyncPayload::frozenIds);

    public static StasisSyncPayload cleared() {
        return new StasisSyncPayload(List.of());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
