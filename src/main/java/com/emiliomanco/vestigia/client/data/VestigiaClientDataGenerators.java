package com.emiliomanco.vestigia.client.data;

import com.emiliomanco.vestigia.Vestigia;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = Vestigia.MODID, value = Dist.CLIENT)
public final class VestigiaClientDataGenerators {
    private VestigiaClientDataGenerators() {}

    @SubscribeEvent
    static void onGatherData(GatherDataEvent.Client event) {
        event.createProvider(ModModelProvider::new);
        event.createProvider(ModEquipmentAssetProvider::new);
        event.createProvider(ModSoundDefinitionsProvider::new);
        event.createProvider(ModLanguageProvider.EnglishUs::new);
        event.createProvider(ModLanguageProvider.SpanishEs::new);
    }
}
