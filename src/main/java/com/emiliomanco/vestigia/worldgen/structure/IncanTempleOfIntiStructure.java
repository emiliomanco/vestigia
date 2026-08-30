package com.emiliomanco.vestigia.worldgen.structure;

import com.emiliomanco.vestigia.registry.ModStructureTypes;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public class IncanTempleOfIntiStructure extends SurfaceStructure {

    private static final int MIN_Y = 62;
    private static final int MAX_Y = 90;

    public static final MapCodec<IncanTempleOfIntiStructure> CODEC =
            simpleCodec(IncanTempleOfIntiStructure::new);

    public IncanTempleOfIntiStructure(StructureSettings settings) {
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
        builder.addPiece(new IncanTempleOfIntiPiece(origin));
    }

    @Override
    public StructureType<?> type() {
        return ModStructureTypes.INCAN_TEMPLE_OF_INTI.get();
    }
}
