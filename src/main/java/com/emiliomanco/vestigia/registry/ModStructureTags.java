package com.emiliomanco.vestigia.registry;

import com.emiliomanco.vestigia.Vestigia;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

public final class ModStructureTags {
    private ModStructureTags() {}

    public static final TagKey<Structure> VESTIGIA =
            TagKey.create(Registries.STRUCTURE, Vestigia.id("vestigia"));
}
