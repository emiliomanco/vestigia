package com.emiliomanco.vestigia.item.god;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.registry.ModItems;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = Vestigia.MODID)
public final class StalkTracker {
    private StalkTracker() {}

    public static final int STALK_TICKS = 60;
    public static final float STALK_MULTIPLIER = 2.5F;
    private static final int STAGGER_TICKS = 60;
    private static final int STAGGER_AMPLIFIER = 2;

    private static final double STILL_EPSILON_SQR = 1.0E-4;

    private static final Map<UUID, Integer> CROUCH_TICKS = new ConcurrentHashMap<>();
    private static final Map<UUID, Boolean> PRIMED = new ConcurrentHashMap<>();

    @SubscribeEvent
    static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        UUID id = player.getUUID();

        if (!isWearingOtorongo(player) || !player.isShiftKeyDown() || !isStill(player)) {
            CROUCH_TICKS.remove(id);
            return;
        }

        int ticks = CROUCH_TICKS.merge(id, 1, Integer::sum);
        if (ticks == STALK_TICKS) {
            PRIMED.put(id, Boolean.TRUE);
        }
    }

    @SubscribeEvent
    static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer attacker)) {
            return;
        }
        if (!Boolean.TRUE.equals(PRIMED.remove(attacker.getUUID()))) {
            return;
        }
        if (!isWearingOtorongo(attacker)) {
            return;
        }

        event.setAmount(event.getAmount() * STALK_MULTIPLIER);
        LivingEntity victim = event.getEntity();
        victim.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, STAGGER_TICKS, STAGGER_AMPLIFIER));
        CROUCH_TICKS.remove(attacker.getUUID());
    }

    public static void forget(UUID playerId) {
        CROUCH_TICKS.remove(playerId);
        PRIMED.remove(playerId);
    }

    public static boolean isPrimed(Player player) {
        return Boolean.TRUE.equals(PRIMED.get(player.getUUID()));
    }

    private static boolean isWearingOtorongo(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).is(ModItems.OTORONGO_HELM.get());
    }

    private static boolean isStill(Player player) {
        double dx = player.getX() - player.xOld;
        double dz = player.getZ() - player.zOld;
        return dx * dx + dz * dz < STILL_EPSILON_SQR;
    }
}
