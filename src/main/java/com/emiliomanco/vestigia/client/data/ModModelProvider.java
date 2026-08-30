package com.emiliomanco.vestigia.client.data;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.registry.ModBlocks;
import com.emiliomanco.vestigia.registry.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public final class ModModelProvider extends ModelProvider {

    public ModModelProvider(PackOutput output) {
        super(output, Vestigia.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {

        blockModels.createAirLikeBlock(ModBlocks.VESTIGE_TABLE.get(),
                TextureMapping.getBlockTexture(ModBlocks.VESTIGE_TABLE.get()));

        itemModels.generateFlatItem(ModItems.LINE_TABLET.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.OTORONGO_FANG.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.OBSIDIAN_FRAGMENT.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ModItems.OFFERING.get(), ModelTemplates.FLAT_ITEM);
    }

    @Override
    protected Stream<Holder<Item>> getKnownItems() {
        return BuiltInRegistries.ITEM.listElements()
                .filter(holder -> holder.getKey().identifier().getNamespace().equals(Vestigia.MODID))
                .filter(holder -> !GECKOLIB_ITEMS.contains(holder.value()))
                .map(holder -> (Holder<Item>) holder);
    }

    private static final Set<Item> GECKOLIB_ITEMS = geckolibItems();

    private static Set<Item> geckolibItems() {
        Set<Item> items = new HashSet<>(Set.of(
            ModItems.SUN_DISC_OF_INTI.get(),
            ModItems.LUNAR_MIRROR.get(),
            ModItems.VIRACOCHA_STAFF.get(),
            ModItems.CROWN_OF_PACHAMAMA.get(),
            ModItems.MANTLE_OF_KUKULKAN.get(),
            ModItems.SUPREME_CROWN.get(),
            ModItems.OTORONGO_HELM.get(),
            ModItems.BLOWGUN.get(),
            ModItems.MACUAHUITL.get(),
            ModItems.INCAN_CLUB.get(),
            ModItems.PUNCHAO.get(),
            ModItems.OBSIDIAN_SPEAR.get(),
            ModItems.JADE_MASK.get(),
            ModItems.VESTIGE_TABLE.get(),
            ModItems.JAGUAR_SPAWN_EGG.get(),
            ModItems.MAYAN_WARRIOR_SPAWN_EGG.get(),
            ModItems.MAYAN_ZOMBIE_SPAWN_EGG.get(),
            ModItems.MAYAN_SHAMAN_SPAWN_EGG.get(),
            ModItems.MAYAN_NACOM_SPAWN_EGG.get(),
            ModItems.INCAN_WARRIOR_SPAWN_EGG.get(),
            ModItems.INCAN_PRIEST_SPAWN_EGG.get(),
            ModItems.INCAN_APUSKIPAY_SPAWN_EGG.get()));
        ModItems.curareDarts().forEach(dart -> items.add(dart.get()));
        return Set.copyOf(items);
    }
}
