package com.emiliomanco.vestigia.worldgen.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;

public abstract class StructurePieceBase extends StructurePiece {

    protected final BlockPos origin;

    protected StructurePieceBase(StructurePieceType type, BlockPos origin, BoundingBox bounds) {
        super(type, 0, bounds);
        this.origin = origin;
    }

    protected StructurePieceBase(StructurePieceType type, CompoundTag tag) {
        super(type, tag);
        this.origin = new BlockPos(
                tag.getIntOr("OriginX", 0),
                tag.getIntOr("OriginY", 0),
                tag.getIntOr("OriginZ", 0));
    }

    @Override
    protected void addAdditionalSaveData(StructurePieceSerializationContext context, CompoundTag tag) {
        tag.putInt("OriginX", origin.getX());
        tag.putInt("OriginY", origin.getY());
        tag.putInt("OriginZ", origin.getZ());
    }

    protected void place(WorldGenLevel level, BlockState state, int x, int y, int z, BoundingBox chunkBB) {
        BlockPos pos = new BlockPos(x, y, z);
        if (chunkBB.isInside(pos)) {
            level.setBlock(pos, state, 2);
        }
    }

    protected void placeRelative(WorldGenLevel level, BlockState state, int dx, int dy, int dz, BoundingBox chunkBB) {
        place(level, state, origin.getX() + dx, origin.getY() + dy, origin.getZ() + dz, chunkBB);
    }

    protected void fillRelative(WorldGenLevel level, BlockState state,
                                int x0, int y0, int z0, int x1, int y1, int z1, BoundingBox chunkBB) {
        for (int y = y0; y <= y1; y++) {
            for (int x = x0; x <= x1; x++) {
                for (int z = z0; z <= z1; z++) {
                    placeRelative(level, state, x, y, z, chunkBB);
                }
            }
        }
    }
}
