package com.emiliomanco.vestigia.client;

import com.emiliomanco.vestigia.client.screen.BranchChoiceScreen;
import net.minecraft.client.Minecraft;

public final class BendingClient {
    private BendingClient() {}

    public static void openBranchChooser() {
        Minecraft.getInstance().setScreen(new BranchChoiceScreen());
    }
}
