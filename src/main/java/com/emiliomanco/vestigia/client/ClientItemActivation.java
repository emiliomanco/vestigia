package com.emiliomanco.vestigia.client;

import com.emiliomanco.vestigia.network.ItemActivationPayload;
import net.minecraft.client.Minecraft;

public final class ClientItemActivation {
    private ClientItemActivation() {}

    public static void accept(ItemActivationPayload payload) {
        if (payload.stack().isEmpty()) {
            return;
        }
        Minecraft.getInstance().gameRenderer.displayItemActivation(payload.stack());
    }
}
