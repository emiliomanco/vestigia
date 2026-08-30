package com.emiliomanco.vestigia.registry;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.item.god.SupremeCrownItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {
    private ModCreativeTabs() {}

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Vestigia.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> VESTIGIA = TABS.register(
            "vestigia",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.vestigia"))
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> ModItems.SUN_DISC_OF_INTI.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.OTORONGO_FANG.get());
                        output.accept(ModItems.OBSIDIAN_FRAGMENT.get());
                        output.accept(ModItems.OFFERING.get());

                        output.accept(ModItems.VESTIGE_TABLE.get());

                        output.accept(ModItems.MACUAHUITL.get());
                        output.accept(ModItems.INCAN_CLUB.get());
                        output.accept(ModItems.OBSIDIAN_SPEAR.get());
                        output.accept(ModItems.BLOWGUN.get());
                        ModItems.curareDarts().forEach(d -> output.accept(d.get()));

                        output.accept(ModItems.PUNCHAO.get());
                        output.accept(ModItems.JADE_MASK.get());
                        output.accept(ModItems.LINE_TABLET.get());

                        output.accept(ModItems.OTORONGO_HELM.get());
                        output.accept(ModItems.SUN_DISC_OF_INTI.get());
                        output.accept(ModItems.LUNAR_MIRROR.get());
                        output.accept(ModItems.VIRACOCHA_STAFF.get());
                        output.accept(ModItems.MANTLE_OF_KUKULKAN.get());
                        output.accept(ModItems.CROWN_OF_PACHAMAMA.get());
                        output.accept(SupremeCrownItem.forged());

                        output.accept(ModItems.JAGUAR_SPAWN_EGG.get());
                        output.accept(ModItems.MAYAN_WARRIOR_SPAWN_EGG.get());
                        output.accept(ModItems.MAYAN_ZOMBIE_SPAWN_EGG.get());
                        output.accept(ModItems.MAYAN_SHAMAN_SPAWN_EGG.get());
                        output.accept(ModItems.MAYAN_NACOM_SPAWN_EGG.get());
                        output.accept(ModItems.INCAN_WARRIOR_SPAWN_EGG.get());
                        output.accept(ModItems.INCAN_PRIEST_SPAWN_EGG.get());
                        output.accept(ModItems.INCAN_APUSKIPAY_SPAWN_EGG.get());
                    })
                    .build());

    public static void register(IEventBus modEventBus) {
        TABS.register(modEventBus);
    }
}
