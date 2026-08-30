package com.emiliomanco.vestigia.worldgen.structure;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;

public abstract class SurfaceStructure extends Structure {

    protected SurfaceStructure(StructureSettings settings) {
        super(settings);
    }

    protected abstract int minSurfaceY();

    protected abstract int maxSurfaceY();

    protected abstract void assemble(GenerationContext context, BlockPos origin, StructurePiecesBuilder builder);

    @Override
    public Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        int x = context.chunkPos().getMinBlockX();
        int z = context.chunkPos().getMinBlockZ();

        int surface = context.chunkGenerator().getFirstOccupiedHeight(
                x, z, Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState());

        if (surface < minSurfaceY() || surface > maxSurfaceY()) {
            return Optional.empty();
        }

        BlockPos origin = new BlockPos(x, surface, z);
        return Optional.of(new GenerationStub(origin, builder -> assemble(context, origin, builder)));
    }
}
