package com.emiliomanco.vestigia.entity;

import net.minecraft.world.entity.Entity;

public final class PinnedState {
    private PinnedState() {}

    private static final String KEY = "VestigiaPinned";

    public static boolean isPinned(Entity entity) {
        return entity.getPersistentData().getBooleanOr(KEY, false);
    }

    public static void setPinned(Entity entity, boolean pinned) {
        if (pinned) {
            entity.getPersistentData().putBoolean(KEY, true);
        } else {
            entity.getPersistentData().remove(KEY);
        }
    }
}
