package com.emiliomanco.vestigia.data;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.registry.ModItemTags;
import com.emiliomanco.vestigia.registry.ModItems;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

public final class ModItemTagsProvider extends ItemTagsProvider {

    public ModItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, Vestigia.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        tag(ModItemTags.CARVED_STONE)
                .add(Items.CHISELED_STONE_BRICKS)
                .add(Items.STONE_BRICKS)
                .add(Items.POLISHED_ANDESITE)
                .add(Items.CHISELED_SANDSTONE)
                .add(Items.CUT_SANDSTONE)
                .add(Items.CHISELED_DEEPSLATE)
                .add(Items.DEEPSLATE_BRICKS)
                .add(Items.MUD_BRICKS);

        tag(ModItemTags.VESTIGES)
                .add(ModItems.PUNCHAO.get())
                .add(ModItems.JADE_MASK.get())
                .add(ModItems.LINE_TABLET.get());

        tag(ModItemTags.ARTIFACTS)
                .add(ModItems.BLOWGUN.get())
                .add(ModItems.MACUAHUITL.get())
                .add(ModItems.INCAN_CLUB.get());

        tag(ModItemTags.GOD_ITEMS)
                .add(ModItems.SUN_DISC_OF_INTI.get())
                .add(ModItems.LUNAR_MIRROR.get())
                .add(ModItems.OTORONGO_HELM.get())
                .add(ModItems.CROWN_OF_PACHAMAMA.get())
                .add(ModItems.MANTLE_OF_KUKULKAN.get())
                .add(ModItems.SUPREME_CROWN.get());

        enchantability();
    }

    private void enchantability() {
        tag(ItemTags.SWORDS).add(ModItems.MACUAHUITL.get());

        tag(ItemTags.SWORDS).add(ModItems.INCAN_CLUB.get());

        tag(ItemTags.SPEARS).add(ModItems.OBSIDIAN_SPEAR.get());
        tag(ItemTags.TRIDENT_ENCHANTABLE).add(ModItems.OBSIDIAN_SPEAR.get());

        tag(ItemTags.HEAD_ARMOR)
                .add(ModItems.OTORONGO_HELM.get())
                .add(ModItems.CROWN_OF_PACHAMAMA.get())
                .add(ModItems.SUPREME_CROWN.get());
        tag(ItemTags.CHEST_ARMOR).add(ModItems.MANTLE_OF_KUKULKAN.get());

    }
}
