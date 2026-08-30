package com.emiliomanco.vestigia.item.god;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.registry.ModItemTags;
import com.emiliomanco.vestigia.registry.ModStructureTags;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

@EventBusSubscriber(modid = Vestigia.MODID)
public final class MasonryLedger {
    private MasonryLedger() {}

    private static final int CAPACITY = 512;

    private static final Map<Identifier, Deque<Entry>> BROKEN = new ConcurrentHashMap<>();

    private record Entry(BlockPos pos, BlockState state) {}

    @SubscribeEvent
    static void onBlockBroken(BlockDropsEvent event) {
        BlockState state = event.getState();
        if (!state.getBlock().asItem().getDefaultInstance().is(ModItemTags.CARVED_STONE)) {
            return;
        }
        ServerLevel level = event.getLevel();
        BlockPos pos = event.getPos().immutable();

        if (!level.structureManager().getStructureWithPieceAt(pos, ModStructureTags.VESTIGIA).isValid()) {
            return;
        }
        remember(level, pos, state);
    }

    private static void remember(ServerLevel level, BlockPos pos, BlockState state) {
        Deque<Entry> ledger = BROKEN.computeIfAbsent(
                level.dimension().identifier(), key -> new ArrayDeque<>());
        synchronized (ledger) {
            ledger.addLast(new Entry(pos, state));
            while (ledger.size() > CAPACITY) {
                ledger.removeFirst();
            }
        }
    }

    public static int restoreAround(ServerLevel level, BlockPos centre, int radius) {
        Deque<Entry> ledger = BROKEN.get(level.dimension().identifier());
        if (ledger == null) {
            return 0;
        }
        int radiusSqr = radius * radius;
        int restored = 0;

        synchronized (ledger) {
            var iterator = ledger.iterator();
            while (iterator.hasNext()) {
                Entry entry = iterator.next();
                if (entry.pos().distSqr(centre) > radiusSqr) {
                    continue;
                }
                if (level.getBlockState(entry.pos()).isAir()) {
                    level.setBlockAndUpdate(entry.pos(), entry.state());
                    restored++;
                }
                iterator.remove();
            }
        }
        return restored;
    }

    public static void forget(Level level) {
        BROKEN.remove(level.dimension().identifier());
    }
}
