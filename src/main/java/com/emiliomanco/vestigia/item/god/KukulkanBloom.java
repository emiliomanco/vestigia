package com.emiliomanco.vestigia.item.god;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.config.VestigiaConfig;
import com.emiliomanco.vestigia.registry.ModItems;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gamerules.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.jspecify.annotations.Nullable;

@EventBusSubscriber(modid = Vestigia.MODID)
public final class KukulkanBloom {
    private KukulkanBloom() {}

    private record Bloom(ResourceKey<Level> dimension, BlockPos anchor, int ticksLeft) {}

    private static final Map<UUID, Bloom> ACTIVE = new ConcurrentHashMap<>();

    private static final int RECAST_TICKS = 40;

    private static final int PARTICLE_INTERVAL = 20;

    private static final double BLOCKS_PER_SECTION = 4096.0D;

    private static final int BREAD_TO_BREED = 3;

    public static boolean begin(ServerLevel level, ServerPlayer player) {
        int duration = VestigiaConfig.KUKULKAN_BLOOM_DURATION_TICKS.get();
        Bloom current = ACTIVE.get(player.getUUID());
        if (current != null && current.ticksLeft() > duration - RECAST_TICKS) {
            return false;
        }
        ACTIVE.put(player.getUUID(), new Bloom(level.dimension(), player.blockPosition(), duration));
        return true;
    }

    public static boolean isActive(ServerPlayer player) {
        return ACTIVE.containsKey(player.getUUID());
    }

    @SubscribeEvent
    static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || !(player.level() instanceof ServerLevel here)) {
            return;
        }
        Bloom bloom = ACTIVE.get(player.getUUID());
        if (bloom == null) {
            return;
        }

        if (!player.getItemBySlot(EquipmentSlot.CHEST).is(ModItems.MANTLE_OF_KUKULKAN.get())) {
            end(player);
            return;
        }
        if (bloom.ticksLeft() <= 1) {
            end(player);
            return;
        }
        ACTIVE.put(player.getUUID(), new Bloom(bloom.dimension(), bloom.anchor(), bloom.ticksLeft() - 1));

        ServerLevel level = here.getServer().getLevel(bloom.dimension());
        if (level == null) {
            return;
        }
        quicken(level, bloom.anchor());
        if (bloom.ticksLeft() % PARTICLE_INTERVAL == 0) {
            level.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    bloom.anchor().getX() + 0.5D, bloom.anchor().getY() + 1.0D, bloom.anchor().getZ() + 0.5D,
                    12, 6.0D, 2.0D, 6.0D, 0.0D);
        }
    }

    private static void end(ServerPlayer player) {
        ACTIVE.remove(player.getUUID());
    }

    @SubscribeEvent
    static void onLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        ACTIVE.remove(event.getEntity().getUUID());
    }

    private static void quicken(ServerLevel level, BlockPos anchor) {
        int rate = level.getGameRules().get(GameRules.RANDOM_TICK_SPEED);
        double bonus = VestigiaConfig.KUKULKAN_BLOOM_GROWTH_BONUS.get();
        if (rate <= 0 || bonus <= 0.0D) {
            return;
        }
        int radius = VestigiaConfig.KUKULKAN_BLOOM_RADIUS.get();
        int span = radius * 2 + 1;
        long cube = (long) span * span * span;
        int samples = (int) Math.round(cube * rate * bonus / BLOCKS_PER_SECTION);
        if (samples <= 0) {
            return;
        }

        RandomSource random = level.getRandom();
        int radiusSqr = radius * radius;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int sample = 0; sample < samples; sample++) {
            int dx = random.nextInt(span) - radius;
            int dy = random.nextInt(span) - radius;
            int dz = random.nextInt(span) - radius;
            if (dx * dx + dy * dy + dz * dz > radiusSqr) {
                continue;
            }
            cursor.set(anchor.getX() + dx, anchor.getY() + dy, anchor.getZ() + dz);
            if (level.isOutsideBuildHeight(cursor) || !level.isLoaded(cursor)) {
                continue;
            }
            BlockState state = level.getBlockState(cursor);
            if (state.isRandomlyTicking() && isPlant(state)) {
                state.randomTick(level, cursor.immutable(), random);
            }
        }
    }

    private static boolean isPlant(BlockState state) {
        return state.getBlock() instanceof BonemealableBlock
                || state.getBlock() instanceof SugarCaneBlock
                || state.getBlock() instanceof CactusBlock
                || state.getBlock() instanceof NetherWartBlock;
    }

    @SubscribeEvent
    static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || !(player.level() instanceof ServerLevel level)) {
            return;
        }
        if (event.getHand() != InteractionHand.MAIN_HAND || !event.getItemStack().isEmpty()) {
            return;
        }
        if (!isActive(player)) {
            return;
        }
        if (!(event.getTarget() instanceof AgeableMob mob) || mob instanceof Enemy || mob.isBaby()) {
            return;
        }
        if (!coax(level, player, mob)) {
            return;
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    private static boolean coax(ServerLevel level, ServerPlayer player, AgeableMob mob) {
        if (mob instanceof Animal animal) {
            if (!animal.canFallInLove() || animal.getAge() != 0) {
                return false;
            }
            animal.setInLove(player);
            return true;
        }
        if (mob instanceof Villager villager) {
            if (villager.canBreed()) {
                return false;
            }
            villager.getInventory().addItem(new ItemStack(Items.BREAD, BREAD_TO_BREED));
            if (!villager.canBreed()) {
                return false;
            }
            level.sendParticles(ParticleTypes.HEART, villager.getX(), villager.getY() + 1.0D, villager.getZ(),
                    5, 0.4D, 0.4D, 0.4D, 0.0D);
            return true;
        }
        return false;
    }

    public static @Nullable BlockPos anchorOf(ServerPlayer player) {
        Bloom bloom = ACTIVE.get(player.getUUID());
        return bloom == null ? null : bloom.anchor();
    }
}
