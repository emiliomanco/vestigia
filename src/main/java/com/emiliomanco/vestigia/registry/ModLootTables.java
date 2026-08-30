package com.emiliomanco.vestigia.registry;

import com.emiliomanco.vestigia.Vestigia;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public final class ModLootTables {
    private ModLootTables() {}

    public static final ResourceKey<LootTable> MAYA_PYRAMID = chest("maya_pyramid");

    public static final ResourceKey<LootTable> INTI_TEMPLE = chest("inti_temple");

    public static final ResourceKey<LootTable> INTI_TEMPLE_SUMMIT = chest("inti_temple_summit");

    private static ResourceKey<LootTable> chest(String path) {
        return ResourceKey.create(Registries.LOOT_TABLE, Vestigia.id("chests/" + path));
    }
}
