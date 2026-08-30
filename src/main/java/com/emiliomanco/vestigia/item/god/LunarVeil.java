package com.emiliomanco.vestigia.item.god;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.config.VestigiaConfig;
import com.emiliomanco.vestigia.item.vestige.VestigePassives;
import com.emiliomanco.vestigia.registry.ModItems;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = Vestigia.MODID)
public final class LunarVeil {
    private LunarVeil() {}

    private static final double STILL_EPSILON_SQR = 1.0E-4;

    private static final double FORGET_RANGE = 40.0D;

    private static final Map<UUID, Integer> STILL_TICKS = new ConcurrentHashMap<>();
    private static final Map<UUID, Boolean> VEILED = new ConcurrentHashMap<>();

    @SubscribeEvent
    static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        UUID id = player.getUUID();

        if (!carriesMirror(player)) {
            STILL_TICKS.remove(id);
            VEILED.remove(id);
            return;
        }

        if (player.tickCount % 40 == 0) {
            player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 400, 0, true, false));
        }

        if (!isNight(player) || !isStill(player)) {
            STILL_TICKS.remove(id);
            if (VEILED.remove(id) != null) {
                player.removeEffect(MobEffects.INVISIBILITY);
            }
            return;
        }

        int ticks = STILL_TICKS.merge(id, 1, Integer::sum);
        if (ticks < VestigiaConfig.KILLA_VEIL_DELAY_TICKS.get()) {
            return;
        }

        if (VEILED.putIfAbsent(id, Boolean.TRUE) == null) {
            player.sendSystemMessage(net.minecraft.network.chat.Component
                    .translatable("item.vestigia.lunar_mirror.veiled"), true);
        }
        player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 40, 0, true, false));

        forgetVeiledPlayer(player);
    }

    @SubscribeEvent
    static void onChangeTarget(LivingChangeTargetEvent event) {
        if (VEILED.isEmpty()) {
            return;
        }
        if (event.getNewAboutToBeSetTarget() instanceof Player target && isVeiled(target)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer attacker)) {
            return;
        }
        if (!Boolean.TRUE.equals(VEILED.remove(attacker.getUUID())) || !carriesMirror(attacker)) {
            return;
        }
        event.setAmount(event.getAmount()
                * (float) (1.0D + VestigiaConfig.KILLA_AMBUSH_BONUS.get()));
        STILL_TICKS.remove(attacker.getUUID());
        attacker.removeEffect(MobEffects.INVISIBILITY);
    }

    private static void forgetVeiledPlayer(ServerPlayer player) {
        for (Mob mob : player.level().getEntitiesOfClass(Mob.class,
                player.getBoundingBox().inflate(FORGET_RANGE))) {
            if (mob.getTarget() == player) {
                mob.setTarget(null);
            }
        }
    }

    public static void forget(UUID playerId) {
        STILL_TICKS.remove(playerId);
        VEILED.remove(playerId);
    }

    public static boolean isVeiled(Player player) {
        return Boolean.TRUE.equals(VEILED.get(player.getUUID()));
    }

    private static boolean carriesMirror(Player player) {
        return VestigePassives.carries(player, ModItems.LUNAR_MIRROR.get());
    }

    private static boolean isNight(Player player) {
        long dayTime = Math.floorMod(player.level().getOverworldClockTime(), 24000L);
        return dayTime >= 12000L;
    }

    private static boolean isStill(Player player) {
        double dx = player.getX() - player.xOld;
        double dz = player.getZ() - player.zOld;
        return dx * dx + dz * dz < STILL_EPSILON_SQR;
    }
}
