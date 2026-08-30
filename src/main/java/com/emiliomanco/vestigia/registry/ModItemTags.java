package com.emiliomanco.vestigia.registry;

import com.emiliomanco.vestigia.Vestigia;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public final class ModItemTags {
    private ModItemTags() {}

    public static final TagKey<Item> CARVED_STONE = create("carved_stone");

    public static final TagKey<Item> VESTIGES = create("vestiges");

    public static final TagKey<Item> ARTIFACTS = create("artifacts");

    public static final TagKey<Item> GOD_ITEMS = create("god_items");

    private static TagKey<Item> create(String path) {
        return TagKey.create(Registries.ITEM, Vestigia.id(path));
    }
}
