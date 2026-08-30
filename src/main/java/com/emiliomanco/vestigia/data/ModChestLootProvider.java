package com.emiliomanco.vestigia.data;

import com.emiliomanco.vestigia.registry.ModItems;
import com.emiliomanco.vestigia.registry.ModLootTables;
import java.util.function.BiConsumer;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public final class ModChestLootProvider implements LootTableSubProvider {

    private static final int MOD_ITEM = 1;
    private static final int OFFERING_MIN = 5, OFFERING_MAX = 8;
    private static final int GOLD_MIN = 3, GOLD_MAX = 4;
    private static final int IRON_MIN = 3, IRON_MAX = 4;
    private static final int DIAMOND_MIN = 2, DIAMOND_MAX = 3;

    private static final int INTI_GOD = 1;
    private static final int INTI_KEEPSAKE = 1;
    private static final int INTI_DART_MIN = 3, INTI_DART_MAX = 5;
    private static final int INTI_DIAMOND_MIN = 0, INTI_DIAMOND_MAX = 2;
    private static final int SUMMIT_OFFERING_MIN = 3, SUMMIT_OFFERING_MAX = 4;
    private static final int INTI_OFFERING_MIN = 4, INTI_OFFERING_MAX = 5;
    private static final int INTI_GOLD = 4;
    private static final int INTI_IRON = 3;

    private static final int CLUB_WEIGHT = 3;
    private static final int BLOWGUN_WEIGHT = 2;
    private static final int TABLET_WEIGHT = 1;

    public ModChestLootProvider(HolderLookup.Provider registries) {
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(ModLootTables.MAYA_PYRAMID, LootTable.lootTable()

                .withPool(one()
                        .add(count(ModItems.OTORONGO_HELM.get(), MOD_ITEM))
                        .add(count(ModItems.BLOWGUN.get(), MOD_ITEM))
                        .add(count(ModItems.MACUAHUITL.get(), MOD_ITEM)))

                .withPool(one().add(stack(ModItems.OFFERING.get(), OFFERING_MIN, OFFERING_MAX)))

                .withPool(one().add(stack(Items.GOLD_INGOT, GOLD_MIN, GOLD_MAX)))
                .withPool(one().add(stack(Items.IRON_INGOT, IRON_MIN, IRON_MAX)))
                .withPool(one().add(stack(Items.DIAMOND, DIAMOND_MIN, DIAMOND_MAX))));

        intiTemple(output);
    }

    private void intiTemple(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(ModLootTables.INTI_TEMPLE_SUMMIT, LootTable.lootTable()
                .withPool(one()
                        .add(count(ModItems.CROWN_OF_PACHAMAMA.get(), INTI_GOD))
                        .add(count(ModItems.VIRACOCHA_STAFF.get(), INTI_GOD))
                        .add(count(ModItems.LUNAR_MIRROR.get(), INTI_GOD))
                        .add(count(ModItems.SUN_DISC_OF_INTI.get(), INTI_GOD)))
                .withPool(keepsake())
                .withPool(darts())
                .withPool(diamonds())
                .withPool(one().add(stack(ModItems.OFFERING.get(), SUMMIT_OFFERING_MIN, SUMMIT_OFFERING_MAX)))
                .withPool(one().add(count(Items.GOLD_INGOT, INTI_GOLD)))
                .withPool(one().add(count(Items.IRON_INGOT, INTI_IRON))));

        output.accept(ModLootTables.INTI_TEMPLE, LootTable.lootTable()
                .withPool(keepsake())
                .withPool(darts())
                .withPool(diamonds())
                .withPool(one().add(stack(ModItems.OFFERING.get(), INTI_OFFERING_MIN, INTI_OFFERING_MAX)))
                .withPool(one().add(count(Items.GOLD_INGOT, INTI_GOLD)))
                .withPool(one().add(count(Items.IRON_INGOT, INTI_IRON))));
    }

    private static LootPool.Builder keepsake() {
        return one()
                .add(count(ModItems.INCAN_CLUB.get(), INTI_KEEPSAKE).setWeight(CLUB_WEIGHT))
                .add(count(ModItems.BLOWGUN.get(), INTI_KEEPSAKE).setWeight(BLOWGUN_WEIGHT))
                .add(count(ModItems.LINE_TABLET.get(), INTI_KEEPSAKE).setWeight(TABLET_WEIGHT));
    }

    private static LootPool.Builder darts() {
        LootPool.Builder pool = one();
        ModItems.curareDarts().forEach(dart ->
                pool.add(stack(dart.get(), INTI_DART_MIN, INTI_DART_MAX)));
        return pool;
    }

    private static LootPool.Builder diamonds() {
        return one().add(stack(Items.DIAMOND, INTI_DIAMOND_MIN, INTI_DIAMOND_MAX));
    }

    private static LootPool.Builder one() {
        return LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F));
    }

    private static LootItem.Builder<?> stack(ItemLike item, int min, int max) {
        return LootItem.lootTableItem(item)
                .apply(SetItemCountFunction.setCount(UniformGenerator.between(min, max)));
    }

    private static LootItem.Builder<?> count(ItemLike item, int exact) {
        return LootItem.lootTableItem(item)
                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(exact)));
    }
}
