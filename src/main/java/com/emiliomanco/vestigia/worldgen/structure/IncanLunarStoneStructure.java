package com.emiliomanco.vestigia.worldgen.structure;

import com.emiliomanco.vestigia.registry.ModStructureTypes;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class IncanLunarStoneStructure extends SurfaceStructure {

    private static final int MIN_Y = 63;
    private static final int MAX_Y = 220;

    public static final MapCodec<IncanLunarStoneStructure> CODEC =
            simpleCodec(IncanLunarStoneStructure::new);

    public IncanLunarStoneStructure(StructureSettings settings) {
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
        builder.addPiece(new IncanLunarStonePiece(origin));
    }

    @Override
    public StructureType<?> type() {
        return ModStructureTypes.INCAN_LUNAR_STONE.get();
    }
}
