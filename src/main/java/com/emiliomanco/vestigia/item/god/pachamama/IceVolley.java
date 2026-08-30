package com.emiliomanco.vestigia.item.god.pachamama;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.entity.projectile.ElementalBolt;
import java.util.Comparator;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jspecify.annotations.Nullable;

@EventBusSubscriber(modid = Vestigia.MODID)
public final class IceVolley {
    private IceVolley() {}

    private static final double REACH = 8.0D;

    @SubscribeEvent
    static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || !(player.level() instanceof ServerLevel level)) {
            return;
        }
        if (!swungThisTick(player)) {
            return;
        }
        ElementalBolt next = nextHeld(level, player);
        if (next != null) {
            next.release(player);
        }
    }

    private static boolean swungThisTick(ServerPlayer player) {
        return player.swinging && player.swingTime == 0;
    }

    private static @Nullable ElementalBolt nextHeld(ServerLevel level, ServerPlayer player) {
        return held(level, player).stream()
                .filter(ElementalBolt::isArmed)
                .min(Comparator.comparingDouble(ElementalBolt::holdAngle))
                .orElse(null);
    }

    public static List<ElementalBolt> held(ServerLevel level, ServerPlayer player) {
        return level.getEntitiesOfClass(ElementalBolt.class,
                player.getBoundingBox().inflate(REACH),
                bolt -> bolt.isCharging()
                        && bolt.element() == ElementalBolt.Element.ICE
                        && bolt.getOwner() == player);
    }

    public static void clearHeld(ServerLevel level, ServerPlayer player) {
        for (ElementalBolt bolt : held(level, player)) {
            bolt.discard();
        }
    }
}
