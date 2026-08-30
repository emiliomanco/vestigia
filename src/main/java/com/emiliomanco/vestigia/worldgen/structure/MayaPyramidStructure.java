package com.emiliomanco.vestigia.worldgen.structure;

import com.emiliomanco.vestigia.registry.ModStructureTypes;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class MayaPyramidStructure extends SurfaceStructure {

    private static final int MIN_Y = 62;
    private static final int MAX_Y = 96;

    public static final MapCodec<MayaPyramidStructure> CODEC =
            simpleCodec(MayaPyramidStructure::new);

    public MayaPyramidStructure(StructureSettings settings) {
        super(settings);
    }

    @Override
    protected int minSurfaceY() {
        return MIN_Y;
    }

    @Override
    protected int maxSurfaceY() {
        return MAX_Y;
    }

    @Override
    protected void assemble(GenerationContext context, BlockPos origin, StructurePiecesBuilder builder) {
        builder.addPiece(new MayaPyramidPiece(origin));
    }

    @Override
    public StructureType<?> type() {
        return ModStructureTypes.MAYA_PYRAMID.get();
    }
}
