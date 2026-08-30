package com.emiliomanco.vestigia.item.god;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.config.VestigiaConfig;
import com.emiliomanco.vestigia.item.vestige.VestigePassives;
import com.emiliomanco.vestigia.network.StasisSyncPayload;
import com.emiliomanco.vestigia.registry.ModItems;
import com.emiliomanco.vestigia.registry.ModSounds;
import com.emiliomanco.vestigia.network.TimeStopSoundPayload;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = Vestigia.MODID)
public final class TimeStop {
    private TimeStop() {}

    private static final int RESCAN_INTERVAL_TICKS = 5;

    private static final int BROADCAST_MARGIN = 48;

    private static final class Field {
        final Set<UUID> held = new HashSet<>();
        final Set<Integer> heldIds = new HashSet<>();
        long expiresAt;
    }

    private static final Map<UUID, Field> FIELDS = new ConcurrentHashMap<>();
    private static final Set<UUID> HELD = ConcurrentHashMap.newKeySet();

    public static boolean isActive(Player player) {
        return FIELDS.containsKey(player.getUUID());
    }

    public static void start(ServerPlayer caster) {
        Field field = new Field();
        field.expiresAt = caster.hasInfiniteMaterials()
                ? Long.MAX_VALUE
                : caster.level().getGameTime() + VestigiaConfig.VIRACOCHA_STASIS_MAX_TICKS.get();
        FIELDS.put(caster.getUUID(), field);
        rescan(caster, field);
        announce(caster, ModSounds.VIRACOCHA_STOP_TIME.get());
        PacketDistributor.sendToPlayer(caster, TimeStopSoundPayload.started());
    }

    public static void stop(ServerPlayer caster) {
        Field field = FIELDS.remove(caster.getUUID());
        if (field == null) {
            return;
        }
        rebuildHeld();

        if (caster.level() instanceof ServerLevel level) {
            broadcast(level, caster.getX(), caster.getY(), caster.getZ());
        }

        announce(caster, ModSounds.VIRACOCHA_RESUME_TIME.get());
        PacketDistributor.sendToPlayer(caster, TimeStopSoundPayload.ended());

        if (!caster.hasInfiniteMaterials()) {
            caster.getCooldowns().addCooldown(ModItems.VIRACOCHA_STAFF.get().getDefaultInstance(),
                    VestigiaConfig.VIRACOCHA_STASIS_COOLDOWN_TICKS.get());
        }
    }

    private static void announce(ServerPlayer caster, net.minecraft.sounds.SoundEvent sound) {
        if (caster.level() instanceof ServerLevel level) {
            level.playSound(null, caster.blockPosition(), sound,
                    net.minecraft.sounds.SoundSource.PLAYERS, 0.7F, 1.0F);
        }
    }

    @SubscribeEvent
    static void onPlayerTick(PlayerTickEvent.Post event) {
        if (FIELDS.isEmpty() || !(event.getEntity() instanceof ServerPlayer caster)) {
            return;
        }
        Field field = FIELDS.get(caster.getUUID());
        if (field == null) {
            return;
        }

        if (!caster.isAlive()
                || !VestigePassives.carries(caster, ModItems.VIRACOCHA_STAFF.get())
                || caster.level().getGameTime() >= field.expiresAt) {
            stop(caster);
            return;
        }

        if (caster.tickCount % RESCAN_INTERVAL_TICKS == 0) {
            rescan(caster, field);
        }
    }

    private static void rescan(ServerPlayer caster, Field field) {
        if (!(caster.level() instanceof ServerLevel level)) {
            return;
        }
        int radius = VestigiaConfig.VIRACOCHA_STASIS_RADIUS.get();

        Set<UUID> stillInside = new HashSet<>();
        field.heldIds.clear();
        for (Entity entity : level.getEntities(caster,
                caster.getBoundingBox().inflate(radius), TimeStop::isFreezable)) {
            UUID id = entity.getUUID();
            stillInside.add(id);
            field.heldIds.add(entity.getId());
            if (field.held.add(id)) {
                entity.setDeltaMovement(Vec3.ZERO);
                entity.hurtMarked = true;
                HELD.add(id);

                if (entity instanceof com.emiliomanco.vestigia.entity.animal.Jaguar jaguar) {
                    jaguar.releaseBeforeFreeze();
                }
            }
        }

        field.held.removeIf(id -> {
            if (stillInside.contains(id)) {
                return false;
            }
            HELD.remove(id);
            return true;
        });

        broadcast(level, caster.getX(), caster.getY(), caster.getZ());
    }

    private static boolean isFreezable(Entity candidate) {
        return !(candidate instanceof Player)
                && !(candidate instanceof com.emiliomanco.vestigia.entity.projectile.ThrownSunDisc);
    }

    private static void broadcast(ServerLevel level, double x, double y, double z) {
        Set<Integer> union = new HashSet<>();
        for (Field field : FIELDS.values()) {
            union.addAll(field.heldIds);
        }
        int reach = VestigiaConfig.VIRACOCHA_STASIS_RADIUS.get() + BROADCAST_MARGIN;
        PacketDistributor.sendToPlayersNear(level, null, x, y, z, reach,
                new StasisSyncPayload(List.copyOf(union)));
    }

    @SubscribeEvent
    static void onEntityTick(EntityTickEvent.Pre event) {
        if (HELD.isEmpty()) {
            return;
        }
        Entity entity = event.getEntity();
        if (!HELD.contains(entity.getUUID())) {
            return;
        }

        if (entity.level() instanceof ServerLevel level && level.getGameTime() % 4 == 0) {
            level.sendParticles(ParticleTypes.END_ROD,
                    entity.getX(), entity.getY() + entity.getBbHeight() * 0.6D, entity.getZ(),
                    1, 0.2D, 0.2D, 0.2D, 0.0D);
        }

        event.setCanceled(true);
    }

    public static int heldBy(Player player) {
        Field field = FIELDS.get(player.getUUID());
        return field == null ? 0 : field.held.size();
    }

    public static void forget(Player player) {
        Field field = FIELDS.remove(player.getUUID());
        if (field == null) {
            return;
        }
        rebuildHeld();
        if (player.level() instanceof ServerLevel level) {
            broadcast(level, player.getX(), player.getY(), player.getZ());
        }
    }

    public static void clear(ServerLevel level) {
        FIELDS.clear();
        HELD.clear();
        PacketDistributor.sendToPlayersInDimension(level, StasisSyncPayload.cleared());
    }

    private static void rebuildHeld() {
        HELD.clear();
        for (Field field : FIELDS.values()) {
            HELD.addAll(field.held);
        }
    }
}
