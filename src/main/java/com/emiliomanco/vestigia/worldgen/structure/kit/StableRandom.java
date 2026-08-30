package com.emiliomanco.vestigia.worldgen.structure.kit;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

public final class StableRandom {
    private StableRandom() {}

    public static RandomSource forOrigin(BlockPos origin, long salt) {
        long seed = origin.asLong() * 0x9E3779B97F4A7C15L ^ salt * 0xC2B2AE3D27D4EB4FL;
        seed ^= seed >>> 31;
        seed *= 0xBF58476D1CE4E5B9L;
        seed ^= seed >>> 29;
        return RandomSource.create(seed);
    }
}
