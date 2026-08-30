package com.emiliomanco.vestigia.network;

import com.emiliomanco.vestigia.Vestigia;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = Vestigia.MODID)
public final class ModPayloads {
    private ModPayloads() {}

    private static final String VERSION = "2";

    @SubscribeEvent
    static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(VERSION).optional();

        registrar.playToClient(
                StasisSyncPayload.TYPE,
                StasisSyncPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(
                        () -> com.emiliomanco.vestigia.client.ClientStasis.accept(payload)));

        registrar.playToClient(
                TimeStopSoundPayload.TYPE,
                TimeStopSoundPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(
                        () -> com.emiliomanco.vestigia.client.TimeStopSound.accept(payload)));

        registrar.playToClient(
                ItemActivationPayload.TYPE,
                ItemActivationPayload.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(
                        () -> com.emiliomanco.vestigia.client.ClientItemActivation.accept(payload)));

        registrar.playToServer(
                BendingPayloads.UseAbility.TYPE,
                BendingPayloads.UseAbility.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    if (context.player() instanceof net.minecraft.server.level.ServerPlayer player) {
                        com.emiliomanco.vestigia.item.god.pachamama.Bending.use(
                                player, payload.slot() == 0 ? 0 : 1);
                    }
                }));

        registrar.playToServer(
                BendingPayloads.CycleElement.TYPE,
                BendingPayloads.CycleElement.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    if (context.player() instanceof net.minecraft.server.level.ServerPlayer player) {
                        com.emiliomanco.vestigia.item.god.pachamama.CrownCycle.cycle(player, payload.step());
                    }
                }));

        registrar.playToServer(
                BendingPayloads.ChooseBranch.TYPE,
                BendingPayloads.ChooseBranch.STREAM_CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    if (context.player() instanceof net.minecraft.server.level.ServerPlayer player) {
                        com.emiliomanco.vestigia.item.god.pachamama.CrownBinding.choose(
                                player, payload.branch());
                    }
                }));
    }
}
