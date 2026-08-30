package com.emiliomanco.vestigia.client;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.client.render.GodItemRenderers;
import com.emiliomanco.vestigia.client.render.AirScooterRenderer;
import com.emiliomanco.vestigia.client.render.CurareDartRenderers;
import com.emiliomanco.vestigia.client.render.ElementalBoltRenderer;
import com.emiliomanco.vestigia.client.render.JaguarRenderer;
import com.emiliomanco.vestigia.client.render.AncestralHumanoidRenderer;
import com.emiliomanco.vestigia.client.render.ObsidianSpearRenderers;
import com.emiliomanco.vestigia.client.render.VestigeTableRenderers;
import com.emiliomanco.vestigia.client.screen.VestigeTableScreen;
import com.emiliomanco.vestigia.registry.ModBlockEntities;
import com.emiliomanco.vestigia.registry.ModEntities;
import com.emiliomanco.vestigia.registry.ModMenus;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = Vestigia.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = Vestigia.MODID, value = Dist.CLIENT)
public final class VestigiaClient {

    public VestigiaClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        Vestigia.LOGGER.debug("Vestigia client setup complete.");
    }

    @SubscribeEvent
    static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.VESTIGE_TABLE.get(), VestigeTableScreen::new);
    }

    @SubscribeEvent
    static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.VESTIGE_TABLE.get(),
                VestigeTableRenderers::placed);
        event.registerEntityRenderer(ModEntities.DART.get(), CurareDartRenderers.InFlight::new);
        event.registerEntityRenderer(ModEntities.OBSIDIAN_SPEAR.get(), ObsidianSpearRenderers.InFlight::new);
        event.registerEntityRenderer(ModEntities.SUN_DISC.get(),
                GodItemRenderers::thrownSunDisc);
        event.registerEntityRenderer(ModEntities.ICE_SPIKE.get(), ElementalBoltRenderer::new);

        event.registerEntityRenderer(ModEntities.MAYAN_WARRIOR.get(), AncestralHumanoidRenderer::soldier);
        event.registerEntityRenderer(ModEntities.MAYAN_ZOMBIE.get(), AncestralHumanoidRenderer::zombie);
        event.registerEntityRenderer(ModEntities.MAYAN_SHAMAN.get(), AncestralHumanoidRenderer::shaman);
        event.registerEntityRenderer(ModEntities.MAYAN_NACOM.get(), AncestralHumanoidRenderer::boss);

        event.registerEntityRenderer(ModEntities.INCAN_WARRIOR.get(), AncestralHumanoidRenderer::incaSoldier);
        event.registerEntityRenderer(ModEntities.INCAN_PRIEST.get(), AncestralHumanoidRenderer::incaPriest);
        event.registerEntityRenderer(ModEntities.INCAN_APUSKIPAY.get(), AncestralHumanoidRenderer::incaBoss);

        event.registerEntityRenderer(ModEntities.JAGUAR.get(), JaguarRenderer::new);

        event.registerEntityRenderer(ModEntities.BLOCK_PROJECTILE.get(),
                net.minecraft.client.renderer.entity.FallingBlockRenderer::new);

        event.registerEntityRenderer(ModEntities.AIR_SCOOTER.get(), AirScooterRenderer::new);
    }
}
