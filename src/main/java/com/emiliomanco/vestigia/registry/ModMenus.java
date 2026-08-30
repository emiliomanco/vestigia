package com.emiliomanco.vestigia.registry;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.menu.VestigeTableMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenus {
    private ModMenus() {}

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, Vestigia.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<VestigeTableMenu>> VESTIGE_TABLE =
            MENUS.register("vestige_table",
                    () -> new MenuType<>(VestigeTableMenu::new, FeatureFlags.VANILLA_SET));

    public static void register(IEventBus modEventBus) {
        MENUS.register(modEventBus);
    }
}
