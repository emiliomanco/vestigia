package com.emiliomanco.vestigia.registry;

import com.emiliomanco.vestigia.Vestigia;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

public final class ModArmorMaterials {
    private ModArmorMaterials() {}

    public static final ResourceKey<EquipmentAsset> JADE_MASK_ASSET =
            ResourceKey.create(EquipmentAssets.ROOT_ID, Vestigia.id("jade_mask"));

    public static final ResourceKey<EquipmentAsset> OTORONGO_ASSET =
            ResourceKey.create(EquipmentAssets.ROOT_ID, Vestigia.id("otorongo"));

    public static final ArmorMaterial OTORONGO = new ArmorMaterial(
            37,
            Map.of(ArmorType.HELMET, 3),
            0,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            2.0F,
            0.0F,
            ModItemTags.CARVED_STONE,
            OTORONGO_ASSET);

    public static final ResourceKey<EquipmentAsset> PACHAMAMA_ASSET =
            ResourceKey.create(EquipmentAssets.ROOT_ID, Vestigia.id("corona_pachamama"));

    public static final ArmorMaterial PACHAMAMA = new ArmorMaterial(
            40,
            Map.of(ArmorType.HELMET, 8),
            0,
            SoundEvents.ARMOR_EQUIP_TURTLE,
            4.0F,
            0.1F,
            ModItemTags.CARVED_STONE,
            PACHAMAMA_ASSET);

    public static final ResourceKey<EquipmentAsset> KUKULKAN_ASSET =
            ResourceKey.create(EquipmentAssets.ROOT_ID, Vestigia.id("manto_kukulkan"));

    public static final ArmorMaterial KUKULKAN = new ArmorMaterial(
            34,
            Map.of(ArmorType.CHESTPLATE, 3),
            0,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            1.0F,
            0.0F,
            ModItemTags.CARVED_STONE,
            KUKULKAN_ASSET);
}
