package com.emiliomanco.vestigia.worldgen.structure;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.registry.ModStructurePieceTypes;
import com.emiliomanco.vestigia.worldgen.structure.kit.StableRandom;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class IncanLunarStonePiece extends StructurePieceBase {

    private static final long SALT = 0x1E7A_5709_0002L;

    private static final String TEMPLATE = "incan_lunar_stone";
    private static final int SIZE = 13;
    private static final int HEIGHT = 9;

    private static final int SINK = 1;

    public IncanLunarStonePiece(BlockPos origin) {
        super(ModStructurePieceTypes.INCAN_LUNAR_STONE.get(), origin, boundsAround(origin));
    }

    public IncanLunarStonePiece(CompoundTag tag) {
        super(ModStructurePieceTypes.INCAN_LUNAR_STONE.get(), tag);
    }

    private static BoundingBox boundsAround(BlockPos origin) {
        int half = SIZE / 2 + 1;
        int floor = origin.getY() - SINK;
        return new BoundingBox(
                origin.getX() - half, floor - 1, origin.getZ() - half,
                origin.getX() + half, floor + HEIGHT + 2, origin.getZ() + half);
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator,
                            RandomSource chunkRandom, BoundingBox chunkBB, ChunkPos chunkPos, BlockPos referencePos) {
        placeTemplate(level, chunkBB);
    }

    private void placeTemplate(WorldGenLevel level, BoundingBox chunkBB) {
        Optional<StructureTemplate> found =
                level.getLevel().getStructureManager().get(Vestigia.id(TEMPLATE));
        if (found.isEmpty()) {
            return;
        }
        StructureTemplate template = found.get();
        Vec3i size = template.getSize();

        Rotation rotation = Rotation.values()[
                Math.floorMod((int) (origin.asLong() >> 17), Rotation.values().length)];

        BlockPos corner = new BlockPos(
                origin.getX() - size.getX() / 2,
                origin.getY() - SINK,
                origin.getZ() - size.getZ() / 2);

        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setBoundingBox(chunkBB)
                .setRotation(rotation)
                .setRotationPivot(new BlockPos(size.getX() / 2, 0, size.getZ() / 2))
                .setIgnoreEntities(true);

        template.placeInWorld(level, corner, corner, settings,
                StableRandom.forOrigin(origin, SALT ^ 0x9E37L), 2);
    }
}
