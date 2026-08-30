package com.emiliomanco.vestigia.network;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.item.god.pachamama.BendingBranch;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public final class BendingPayloads {
    private BendingPayloads() {}

    public record UseAbility(int slot) implements CustomPacketPayload {
        public static final Type<UseAbility> TYPE = new Type<>(Vestigia.id("bending_use"));

        public static final StreamCodec<ByteBuf, UseAbility> STREAM_CODEC =
                ByteBufCodecs.VAR_INT.map(UseAbility::new, UseAbility::slot);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record CycleElement(int step) implements CustomPacketPayload {
        public static final Type<CycleElement> TYPE = new Type<>(Vestigia.id("bending_cycle"));

        public static final StreamCodec<ByteBuf, CycleElement> STREAM_CODEC =
                ByteBufCodecs.VAR_INT.map(packed -> new CycleElement(packed == 0 ? -1 : 1),
                        payload -> payload.step() < 0 ? 0 : 1);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record ChooseBranch(BendingBranch branch) implements CustomPacketPayload {
        public static final Type<ChooseBranch> TYPE = new Type<>(Vestigia.id("bending_choose"));

        public static final StreamCodec<ByteBuf, ChooseBranch> STREAM_CODEC =
                BendingBranch.STREAM_CODEC.map(ChooseBranch::new, ChooseBranch::branch);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
