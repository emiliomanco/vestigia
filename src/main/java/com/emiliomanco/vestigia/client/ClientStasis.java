package com.emiliomanco.vestigia.client;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.entity.StasisState;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.emiliomanco.vestigia.network.StasisSyncPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import org.jspecify.annotations.Nullable;

@EventBusSubscriber(modid = Vestigia.MODID, value = Dist.CLIENT)
public final class ClientStasis {
    private ClientStasis() {}

    public static void accept(StasisSyncPayload payload) {
        StasisState.replace(payload.frozenIds());
        SNAPSHOTS.keySet().retainAll(payload.frozenIds());
    }

    @SubscribeEvent
    static void onEntityTick(EntityTickEvent.Pre event) {
        Entity entity = event.getEntity();
        if (!StasisState.isFrozen(entity) || entity instanceof Player
                || entity.countPlayerPassengers() > 0) {
            return;
        }
        collapseInterpolation(entity);
        event.setCanceled(true);
    }

    private static void collapseInterpolation(Entity entity) {
        entity.setOldPosAndRot();
        if (entity instanceof LivingEntity living) {
            living.yHeadRotO = living.yHeadRot;
            living.yBodyRotO = living.yBodyRot;
            living.oAttackAnim = living.attackAnim;

            SNAPSHOTS.computeIfAbsent(entity.getId(), id -> new Snapshot(
                    living.tickCount,
                    living.walkAnimation.position(),
                    living.walkAnimation.speed()));
        }
    }

    public record Snapshot(float ageInTicks, float walkPos, float walkSpeed) {}

    private static final Map<Integer, Snapshot> SNAPSHOTS = new ConcurrentHashMap<>();

    public static @Nullable Snapshot snapshotFor(Entity entity) {
        return SNAPSHOTS.isEmpty() ? null : SNAPSHOTS.get(entity.getId());
    }

    @SubscribeEvent
    static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        StasisState.clear();
        SNAPSHOTS.clear();
    }
}
