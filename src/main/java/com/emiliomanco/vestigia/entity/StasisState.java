package com.emiliomanco.vestigia.entity;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.world.entity.Entity;

public final class StasisState {
    private StasisState() {}

    private static final Set<Integer> FROZEN = ConcurrentHashMap.newKeySet();

    public static boolean isFrozen(Entity entity) {
        return !FROZEN.isEmpty() && FROZEN.contains(entity.getId());
    }

    public static float animationSpeed(Entity entity, float normalSpeed) {
        return isFrozen(entity) ? 0.0F : normalSpeed;
    }

    public static float animationSpeed(Entity entity) {
        return animationSpeed(entity, 1.0F);
    }

    public static void replace(Collection<Integer> ids) {
        FROZEN.retainAll(ids);
        FROZEN.addAll(ids);
    }

    public static void clear() {
        FROZEN.clear();
    }
}
