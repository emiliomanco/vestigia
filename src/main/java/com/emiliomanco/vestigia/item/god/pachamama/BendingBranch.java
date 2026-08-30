package com.emiliomanco.vestigia.item.god.pachamama;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum BendingBranch implements StringRepresentable {

    EARTH("earth", ChatFormatting.GOLD),
    WATER("water", ChatFormatting.BLUE),
    AIR("air", ChatFormatting.WHITE),
    FIRE("fire", ChatFormatting.DARK_RED);

    public static final Codec<BendingBranch> CODEC = StringRepresentable.fromEnum(BendingBranch::values);

    public static final StreamCodec<ByteBuf, BendingBranch> STREAM_CODEC =
            ByteBufCodecs.idMapper(id -> values()[id], BendingBranch::ordinal);

    private final String id;
    private final ChatFormatting colour;

    BendingBranch(String id, ChatFormatting colour) {
        this.id = id;
        this.colour = colour;
    }

    public String id() {
        return id;
    }

    public ChatFormatting colour() {
        return colour;
    }

    public String nameKey() {
        return "item.vestigia.corona_pachamama.branch." + id;
    }

    public String descriptionKey() {
        return nameKey() + ".desc";
    }

    public String abilityKey(int slot) {
        return nameKey() + ".ability_" + slot;
    }

    @Override
    public String getSerializedName() {
        return id;
    }
}
