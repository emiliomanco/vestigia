package com.emiliomanco.vestigia.network;

import com.emiliomanco.vestigia.Vestigia;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record TimeStopSoundPayload(boolean active) implements CustomPacketPayload {

    public static final Type<TimeStopSoundPayload> TYPE = new Type<>(Vestigia.id("time_stop_sound"));

    public static final StreamCodec<ByteBuf, TimeStopSoundPayload> STREAM_CODEC =
            ByteBufCodecs.BOOL.map(TimeStopSoundPayload::new, TimeStopSoundPayload::active);

    public static TimeStopSoundPayload started() {
        return new TimeStopSoundPayload(true);
    }

    public static TimeStopSoundPayload ended() {
        return new TimeStopSoundPayload(false);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
