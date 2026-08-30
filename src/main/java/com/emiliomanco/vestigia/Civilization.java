package com.emiliomanco.vestigia;

import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum Civilization implements StringRepresentable {
    CHAVIN("chavin"),
    NAZCA("nazca"),
    MAYA("maya"),
    MARAJOARA("marajoara"),
    MUISCA("muisca"),
    INCA("inca");

    public static final com.mojang.serialization.Codec<Civilization> CODEC =
            StringRepresentable.fromEnum(Civilization::values);

    private final String name;
    private final String translationKey;

    Civilization(String name) {
        this.name = name;
        this.translationKey = "civilization." + Vestigia.MODID + "." + name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }

    public String id() {
        return name;
    }

    public Component displayName() {
        return Component.translatable(translationKey);
    }

    public String translationKey() {
        return translationKey;
    }
}
