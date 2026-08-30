package com.emiliomanco.vestigia.event;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.command.VestigiaCommand;
import com.emiliomanco.vestigia.entity.guardian.MayanWarrior;
import com.emiliomanco.vestigia.entity.guardian.MayanNacom;
import com.emiliomanco.vestigia.config.VestigiaConfig;
import com.emiliomanco.vestigia.entity.animal.Jaguar;
import com.emiliomanco.vestigia.registry.ModEntities;
import com.emiliomanco.vestigia.item.god.LunarVeil;
import com.emiliomanco.vestigia.item.god.MasonryLedger;
import com.emiliomanco.vestigia.item.god.StalkTracker;
import com.emiliomanco.vestigia.item.god.TimeStop;
import com.emiliomanco.vestigia.item.god.SunDiscOfIntiItem;
import com.emiliomanco.vestigia.item.vestige.VestigePassives;
import com.emiliomanco.vestigia.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = Vestigia.MODID)
public final class VestigiaGameEvents {
    private VestigiaGameEvents() {}

    @SubscribeEvent
    static void onRegisterCommands(RegisterCommandsEvent event) {
        VestigiaCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    static void onRegisterSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(ModEntities.JAGUAR.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Jaguar::checkJaguarSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(ModEntities.MAYAN_WARRIOR.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                VestigiaGameEvents::checkCustodianSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(ModEntities.MAYAN_SHAMAN.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                VestigiaGameEvents::checkShamanSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(ModEntities.INCAN_WARRIOR.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                VestigiaGameEvents::checkIncanWarriorSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);

        event.register(ModEntities.INCAN_PRIEST.get(),
                SpawnPlacementTypes.ON_GROUND,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                VestigiaGameEvents::checkIncanPriestSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE);
    }

    private static boolean checkIncanWarriorSpawnRules(
            net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.Mob> type,
            net.minecraft.world.level.ServerLevelAccessor level,
            net.minecraft.world.entity.EntitySpawnReason reason,
            net.minecraft.core.BlockPos pos,
            net.minecraft.util.RandomSource random) {
        return level.getEntitiesOfClass(com.emiliomanco.vestigia.entity.guardian.IncanWarrior.class,
                new net.minecraft.world.phys.AABB(pos).inflate(TEMPLE_COUNT_RADIUS)).size()
                < WARRIORS_PER_TEMPLE;
    }

    private static boolean checkIncanPriestSpawnRules(
            net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.Mob> type,
            net.minecraft.world.level.ServerLevelAccessor level,
            net.minecraft.world.entity.EntitySpawnReason reason,
            net.minecraft.core.BlockPos pos,
            net.minecraft.util.RandomSource random) {
        return level.getEntitiesOfClass(com.emiliomanco.vestigia.entity.guardian.IncanPriest.class,
                new net.minecraft.world.phys.AABB(pos).inflate(TEMPLE_COUNT_RADIUS)).size()
                < PRIESTS_PER_TEMPLE;
    }

    public static final int WARRIORS_PER_TEMPLE = 25;

    public static final int PRIESTS_PER_TEMPLE = 4;

    private static final double TEMPLE_COUNT_RADIUS = 80.0D;

    private static boolean checkCustodianSpawnRules(
            net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.Mob> type,
            net.minecraft.world.level.ServerLevelAccessor level,
            net.minecraft.world.entity.EntitySpawnReason reason,
            net.minecraft.core.BlockPos pos,
            net.minecraft.util.RandomSource random) {
        if (level.getBlockState(pos.below()).is(net.minecraft.tags.BlockTags.LEAVES)) {
            return false;
        }
        int cap = VestigiaConfig.MAX_GUARDIANS_PER_STRUCTURE.get();
        java.util.List<MayanWarrior> nearby = level.getEntitiesOfClass(MayanWarrior.class,
                new net.minecraft.world.phys.AABB(pos).inflate(CUSTODIAN_COUNT_RADIUS));
        return nearby.size() < cap;
    }

    private static boolean checkShamanSpawnRules(
            net.minecraft.world.entity.EntityType<? extends net.minecraft.world.entity.Mob> type,
            net.minecraft.world.level.ServerLevelAccessor level,
            net.minecraft.world.entity.EntitySpawnReason reason,
            net.minecraft.core.BlockPos pos,
            net.minecraft.util.RandomSource random) {
        if (level.getBlockState(pos.below()).is(net.minecraft.tags.BlockTags.LEAVES)) {
            return false;
        }
        java.util.List<com.emiliomanco.vestigia.entity.guardian.MayanShaman> nearby =
                level.getEntitiesOfClass(com.emiliomanco.vestigia.entity.guardian.MayanShaman.class,
                        new net.minecraft.world.phys.AABB(pos).inflate(CUSTODIAN_COUNT_RADIUS));
        return nearby.size() < SHAMANS_PER_PYRAMID;
    }

    public static final int SHAMANS_PER_PYRAMID = 2;

    private static final double CUSTODIAN_COUNT_RADIUS = 64.0D;

    @SubscribeEvent
    static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        java.util.UUID id = event.getEntity().getUUID();
        StalkTracker.forget(id);
        LunarVeil.forget(id);
        SunDiscOfIntiItem.forget(id);
        TimeStop.forget(event.getEntity());
        com.emiliomanco.vestigia.item.god.pachamama.Bending.forget(id);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    static void onGodDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || !VestigePassives.carries(player, ModItems.VIRACOCHA_STAFF.get())
                || player.getCooldowns().isOnCooldown(ModItems.VIRACOCHA_STAFF.get().getDefaultInstance())) {
            return;
        }

        event.setCanceled(true);
        player.setHealth(player.getMaxHealth());
        player.removeAllEffects();
        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
        player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 200, 1));

        player.getCooldowns().addCooldown(ModItems.VIRACOCHA_STAFF.get().getDefaultInstance(),
                VestigiaConfig.VIRACOCHA_REBIRTH_COOLDOWN_TICKS.get());

        player.level().playSound(null, player.blockPosition(), SoundEvents.TOTEM_USE,
                SoundSource.PLAYERS, 1.0F, 0.7F);
        player.sendSystemMessage(Component
                .translatable("item.vestigia.viracocha_staff.rebirth")
                .withStyle(ChatFormatting.GOLD));
    }

    @SubscribeEvent
    static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel level) {
            MasonryLedger.forget(level);
            TimeStop.clear(level);
        }
    }

    @SubscribeEvent
    static void onLivingDeath(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer killer) {
        }
    }
}
