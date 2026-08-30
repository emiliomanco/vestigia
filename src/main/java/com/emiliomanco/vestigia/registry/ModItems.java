package com.emiliomanco.vestigia.registry;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.item.artifact.BlowgunItem;
import com.emiliomanco.vestigia.item.artifact.CurareDart;
import com.emiliomanco.vestigia.item.artifact.CurareDartItem;
import com.emiliomanco.vestigia.item.artifact.IncanClubItem;
import com.emiliomanco.vestigia.item.VestigeTableBlockItem;
import com.emiliomanco.vestigia.item.artifact.ObsidianSpearItem;
import com.emiliomanco.vestigia.item.artifact.MacuahuitlItem;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import com.emiliomanco.vestigia.item.god.LunarMirrorItem;
import com.emiliomanco.vestigia.item.god.CrownOfPachamamaItem;
import com.emiliomanco.vestigia.item.god.MantleOfKukulkanItem;
import com.emiliomanco.vestigia.item.god.OtorongoHelmItem;
import com.emiliomanco.vestigia.item.god.SupremeCrownItem;
import com.emiliomanco.vestigia.item.god.SunDiscOfIntiItem;
import com.emiliomanco.vestigia.item.god.ViracochaStaffItem;
import com.emiliomanco.vestigia.item.vestige.JadeMaskItem;
import com.emiliomanco.vestigia.item.vestige.LineTabletItem;
import com.emiliomanco.vestigia.item.vestige.PunchaoItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    private ModItems() {}

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Vestigia.MODID);

    public static final DeferredItem<Item> OTORONGO_FANG = ITEMS.registerSimpleItem("otorongo_fang");

    public static final DeferredItem<Item> OBSIDIAN_FRAGMENT = ITEMS.registerSimpleItem("obsidian_fragment");

    public static final DeferredItem<Item> OFFERING =
            ITEMS.registerSimpleItem("offering", p -> p.rarity(Rarity.UNCOMMON));

    public static final DeferredItem<IncanClubItem> INCAN_CLUB =
            ITEMS.registerItem("incan_club", IncanClubItem::new, p -> p
                    .stacksTo(1)
                    .durability(500)
                    .attributes(IncanClubItem.attributes())
                    .enchantable(10)
                    .repairable(Items.COBBLESTONE));

    public static final DeferredItem<MacuahuitlItem> MACUAHUITL =
            ITEMS.registerItem("macuahuitl", MacuahuitlItem::new, p -> p
                    .stacksTo(1)
                    .durability(420)
                    .attributes(MacuahuitlItem.attributes())
                    .enchantable(10)
                    .repairable(Items.OBSIDIAN));

    public static final DeferredItem<JadeMaskItem> JADE_MASK =
            ITEMS.registerItem("jade_mask", JadeMaskItem::new,
                    p -> vestige(p).component(DataComponents.EQUIPPABLE,
                            Equippable.builder(EquipmentSlot.HEAD)
                                    .setEquipSound(SoundEvents.ARMOR_EQUIP_GENERIC)
                                    .setAsset(ModArmorMaterials.JADE_MASK_ASSET)
                                    .build()));

    public static final DeferredItem<LineTabletItem> LINE_TABLET =
            ITEMS.registerItem("line_tablet", LineTabletItem::new, ModItems::vestige);

    public static final DeferredItem<ObsidianSpearItem> OBSIDIAN_SPEAR =
            ITEMS.registerItem("obsidian_spear", ObsidianSpearItem::new,
                    p -> p.stacksTo(1)
                            .durability(340)
                            .attributes(ObsidianSpearItem.attributes())
                            .enchantable(10)
                            .repairable(Items.OBSIDIAN));

    public static final DeferredItem<BlowgunItem> BLOWGUN =
            ITEMS.registerItem("blowgun", BlowgunItem::new, p -> p.stacksTo(1).durability(320));

    private static final Map<CurareDart, DeferredItem<CurareDartItem>> CURARE_DARTS =
            new EnumMap<>(CurareDart.class);

    static {
        for (CurareDart dart : CurareDart.values()) {
            CURARE_DARTS.put(dart, ITEMS.registerItem(dart.id() + "_dart",
                    properties -> new CurareDartItem(properties, dart),
                    p -> p.stacksTo(16)));
        }
    }

    public static DeferredItem<CurareDartItem> curareDart(CurareDart dart) {
        return CURARE_DARTS.get(dart);
    }

    public static Collection<DeferredItem<CurareDartItem>> curareDarts() {
        return CURARE_DARTS.values();
    }

    public static final DeferredItem<PunchaoItem> PUNCHAO =
            ITEMS.registerItem("punchao",
                    PunchaoItem::new,
                    ModItems::vestige);

    private static Item.Properties vestige(Item.Properties properties) {
        return properties.stacksTo(1).rarity(Rarity.EPIC).fireResistant();
    }

    public static final DeferredItem<SunDiscOfIntiItem> SUN_DISC_OF_INTI =
            ITEMS.registerItem("sun_disc_of_inti", SunDiscOfIntiItem::new,
                    p -> p.stacksTo(1).rarity(Rarity.EPIC).fireResistant().setNoCombineRepair());

    public static final DeferredItem<LunarMirrorItem> LUNAR_MIRROR =
            ITEMS.registerItem("lunar_mirror", LunarMirrorItem::new,
                    p -> p.stacksTo(1).rarity(Rarity.EPIC).fireResistant().setNoCombineRepair());

    public static final DeferredItem<ViracochaStaffItem> VIRACOCHA_STAFF =
            ITEMS.registerItem("viracocha_staff", ViracochaStaffItem::new,
                    p -> p.stacksTo(1).rarity(Rarity.EPIC).fireResistant().setNoCombineRepair());

    public static final DeferredItem<OtorongoHelmItem> OTORONGO_HELM =
            ITEMS.registerItem("otorongo_helm", OtorongoHelmItem::new,
                    p -> p.stacksTo(1)
                            .rarity(Rarity.RARE)
                            .durability(ArmorType.HELMET.getDurability(37))
                            .setNoCombineRepair()
                            .enchantable(15)
                            .attributes(OtorongoHelmItem.attributes())
                            .component(DataComponents.EQUIPPABLE,
                                    worn(EquipmentSlot.HEAD, ModArmorMaterials.OTORONGO)));

    public static final DeferredItem<CrownOfPachamamaItem> CROWN_OF_PACHAMAMA =
            ITEMS.registerItem("corona_pachamama", CrownOfPachamamaItem::new,
                    p -> p.stacksTo(1)
                            .rarity(Rarity.EPIC)
                            .durability(ArmorType.HELMET.getDurability(40))
                            .setNoCombineRepair()
                            .enchantable(15)
                            .attributes(CrownOfPachamamaItem.attributes())
                            .component(DataComponents.EQUIPPABLE,
                                    worn(EquipmentSlot.HEAD, ModArmorMaterials.PACHAMAMA)));

    public static final DeferredItem<SupremeCrownItem> SUPREME_CROWN =
            ITEMS.registerItem("supreme_crown", SupremeCrownItem::new,
                    p -> p.stacksTo(1)
                            .rarity(Rarity.EPIC)
                            .enchantable(15)
                            .attributes(SupremeCrownItem.attributes())
                            .component(DataComponents.EQUIPPABLE,
                                    worn(EquipmentSlot.HEAD, ModArmorMaterials.PACHAMAMA)));

    public static final DeferredItem<MantleOfKukulkanItem> MANTLE_OF_KUKULKAN =
            ITEMS.registerItem("manto_kukulkan", MantleOfKukulkanItem::new,
                    p -> p.stacksTo(1)
                            .rarity(Rarity.EPIC)
                            .durability(ArmorType.CHESTPLATE.getDurability(34))
                            .setNoCombineRepair()
                            .enchantable(15)
                            .attributes(MantleOfKukulkanItem.attributes())
                            .component(DataComponents.EQUIPPABLE,
                                    worn(EquipmentSlot.CHEST, ModArmorMaterials.KUKULKAN)));

    public static final DeferredItem<Item> JAGUAR_SPAWN_EGG =
            ITEMS.registerItem("jaguar_spawn_egg",
                    net.minecraft.world.item.SpawnEggItem::new,
                    p -> p.spawnEgg(ModEntities.JAGUAR.get()));

    public static final DeferredItem<Item> MAYAN_WARRIOR_SPAWN_EGG =
            ITEMS.registerItem("mayan_warrior_spawn_egg",
                    net.minecraft.world.item.SpawnEggItem::new,
                    p -> p.spawnEgg(ModEntities.MAYAN_WARRIOR.get()));

    public static final DeferredItem<Item> MAYAN_ZOMBIE_SPAWN_EGG =
            ITEMS.registerItem("mayan_zombie_spawn_egg",
                    net.minecraft.world.item.SpawnEggItem::new,
                    p -> p.spawnEgg(ModEntities.MAYAN_ZOMBIE.get()));

    public static final DeferredItem<Item> MAYAN_SHAMAN_SPAWN_EGG =
            ITEMS.registerItem("mayan_shaman_spawn_egg",
                    net.minecraft.world.item.SpawnEggItem::new,
                    p -> p.spawnEgg(ModEntities.MAYAN_SHAMAN.get()));

    public static final DeferredItem<Item> MAYAN_NACOM_SPAWN_EGG =
            ITEMS.registerItem("mayan_nacom_spawn_egg",
                    net.minecraft.world.item.SpawnEggItem::new,
                    p -> p.spawnEgg(ModEntities.MAYAN_NACOM.get()));

    public static final DeferredItem<Item> INCAN_WARRIOR_SPAWN_EGG =
            ITEMS.registerItem("incan_warrior_spawn_egg",
                    net.minecraft.world.item.SpawnEggItem::new,
                    p -> p.spawnEgg(ModEntities.INCAN_WARRIOR.get()));

    public static final DeferredItem<Item> INCAN_PRIEST_SPAWN_EGG =
            ITEMS.registerItem("incan_priest_spawn_egg",
                    net.minecraft.world.item.SpawnEggItem::new,
                    p -> p.spawnEgg(ModEntities.INCAN_PRIEST.get()));

    public static final DeferredItem<Item> INCAN_APUSKIPAY_SPAWN_EGG =
            ITEMS.registerItem("incan_apuskipay_spawn_egg",
                    net.minecraft.world.item.SpawnEggItem::new,
                    p -> p.spawnEgg(ModEntities.INCAN_APUSKIPAY.get()));

    public static final DeferredItem<VestigeTableBlockItem> VESTIGE_TABLE =
            ITEMS.registerItem("vestige_table",
                    properties -> new VestigeTableBlockItem(ModBlocks.VESTIGE_TABLE.get(), properties),
                    Item.Properties::useBlockDescriptionPrefix);

    private static Equippable worn(EquipmentSlot slot, ArmorMaterial material) {
        return Equippable.builder(slot)
                .setEquipSound(material.equipSound())
                .setAsset(material.assetId())
                .build();
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
