package com.emiliomanco.vestigia.client;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.network.BendingPayloads;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = Vestigia.MODID, value = Dist.CLIENT)
public final class ModKeyMappings {
    private ModKeyMappings() {}

    private static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(Vestigia.id("bending"));

    public static final KeyMapping ABILITY_ONE = new KeyMapping(
            "key.vestigia.ability_one", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_Z, CATEGORY);

    public static final KeyMapping ABILITY_TWO = new KeyMapping(
            "key.vestigia.ability_two", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, CATEGORY);

    public static final KeyMapping ELEMENT_PREVIOUS = new KeyMapping(
            "key.vestigia.element_previous", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_N, CATEGORY);

    public static final KeyMapping ELEMENT_NEXT = new KeyMapping(
            "key.vestigia.element_next", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_M, CATEGORY);

    @SubscribeEvent
    static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(ABILITY_ONE);
        event.register(ABILITY_TWO);
        event.register(ELEMENT_PREVIOUS);
        event.register(ELEMENT_NEXT);
    }

    @SubscribeEvent
    static void onClientTick(ClientTickEvent.Post event) {
        while (ABILITY_ONE.consumeClick()) {
            ClientPacketDistributor.sendToServer(new BendingPayloads.UseAbility(0));
        }
        while (ABILITY_TWO.consumeClick()) {
            ClientPacketDistributor.sendToServer(new BendingPayloads.UseAbility(1));
        }
        while (ELEMENT_PREVIOUS.consumeClick()) {
            ClientPacketDistributor.sendToServer(new BendingPayloads.CycleElement(-1));
        }
        while (ELEMENT_NEXT.consumeClick()) {
            ClientPacketDistributor.sendToServer(new BendingPayloads.CycleElement(1));
        }
    }
}
