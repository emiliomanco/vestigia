package com.emiliomanco.vestigia;

import com.emiliomanco.vestigia.config.VestigiaConfig;
import com.emiliomanco.vestigia.registry.ModBlockEntities;
import com.emiliomanco.vestigia.registry.ModBlocks;
import com.emiliomanco.vestigia.registry.ModCreativeTabs;
import com.emiliomanco.vestigia.registry.ModDataComponents;
import com.emiliomanco.vestigia.registry.ModEffects;
import com.emiliomanco.vestigia.registry.ModEntities;
import com.emiliomanco.vestigia.registry.ModItems;
import com.emiliomanco.vestigia.registry.ModMenus;
import com.emiliomanco.vestigia.registry.ModRecipes;
import com.emiliomanco.vestigia.registry.ModSounds;
import com.emiliomanco.vestigia.registry.ModStructurePieceTypes;
import com.emiliomanco.vestigia.registry.ModStructureTypes;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(Vestigia.MODID)
public final class Vestigia {
    public static final String MODID = "vestigia";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Vestigia(IEventBus modEventBus, ModContainer modContainer) {
        ModDataComponents.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModSounds.register(modEventBus);
        ModEffects.register(modEventBus);
        ModEntities.register(modEventBus);
        ModStructureTypes.register(modEventBus);
        ModStructurePieceTypes.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenus.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModCreativeTabs.register(modEventBus);

        modEventBus.addListener(ModEntities::onCreateAttributes);

        modContainer.registerConfig(ModConfig.Type.COMMON, VestigiaConfig.SPEC);

        LOGGER.info("Vestigia loading - six civilizations, six vestiges, twelve gods.");
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}
