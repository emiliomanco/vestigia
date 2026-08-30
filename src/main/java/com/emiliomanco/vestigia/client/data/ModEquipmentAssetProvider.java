package com.emiliomanco.vestigia.client.data;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.registry.ModArmorMaterials;
import java.util.function.BiConsumer;
import net.minecraft.client.data.models.EquipmentAssetProvider;
import net.minecraft.client.resources.model.EquipmentClientInfo;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.EquipmentAsset;

public class ModEquipmentAssetProvider extends EquipmentAssetProvider {

    public ModEquipmentAssetProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void registerModels(BiConsumer<ResourceKey<EquipmentAsset>, EquipmentClientInfo> output) {
        output.accept(ModArmorMaterials.OTORONGO_ASSET,
                EquipmentClientInfo.builder()
                        .addHumanoidLayers(Vestigia.id("otorongohelm"))
                        .build());

        output.accept(ModArmorMaterials.JADE_MASK_ASSET,
                EquipmentClientInfo.builder()
                        .addHumanoidLayers(Vestigia.id("jademask"))
                        .build());
        output.accept(ModArmorMaterials.PACHAMAMA_ASSET,
                EquipmentClientInfo.builder()
                        .addHumanoidLayers(Vestigia.id("corona_pachamama"))
                        .build());
        output.accept(ModArmorMaterials.KUKULKAN_ASSET,
                EquipmentClientInfo.builder()
                        .addHumanoidLayers(Vestigia.id("manto_kukulkan"))
                        .build());
    }
}
