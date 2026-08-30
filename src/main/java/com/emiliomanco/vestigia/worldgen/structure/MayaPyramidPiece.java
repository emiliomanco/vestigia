package com.emiliomanco.vestigia.worldgen.structure;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.entity.guardian.MayanNacom;
import com.emiliomanco.vestigia.registry.ModBlocks;
import com.emiliomanco.vestigia.registry.ModEntities;
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
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class MayaPyramidPiece extends StructurePieceBase {

    private static final long SALT = 0x4AC0_9011_5000L;
    private static final long ALTAR_SALT = 0x4A17_A200_0001L;

    private static final String TEMPLATE = "maya_pyramid";
    private static final int SIZE = 77;
    private static final int HEIGHT = 35;

    private static final int LIFT = 3;

    private static final int ALTAR_Y = 27;
    private static final int ALTAR_REACH = 2;

    public MayaPyramidPiece(BlockPos origin) {
        super(ModStructurePieceTypes.MAYA_PYRAMID.get(),
                origin.above(LIFT), boundsAround(origin.above(LIFT)));
    }

    public MayaPyramidPiece(CompoundTag tag) {
        super(ModStructurePieceTypes.MAYA_PYRAMID.get(), tag);
    }

    private static BoundingBox boundsAround(BlockPos origin) {
        int half = SIZE / 2 + 1;
        return new BoundingBox(
                origin.getX() - half, origin.getY() - 2, origin.getZ() - half,
                origin.getX() + half, origin.getY() + HEIGHT + 2, origin.getZ() + half);
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator,
                            RandomSource chunkRandom, BoundingBox chunkBB, ChunkPos chunkPos, BlockPos referencePos) {
        footing(level, chunkBB, StableRandom.forOrigin(origin, SALT));
        placeTemplate(level, chunkBB);
        summitAltar(level, chunkBB, StableRandom.forOrigin(origin, ALTAR_SALT));
    }

    private void summitAltar(WorldGenLevel level, BoundingBox chunkBB, RandomSource random) {
        int deckY = origin.getY() + ALTAR_Y;
        for (int dx = -ALTAR_REACH; dx <= ALTAR_REACH; dx++) {
            for (int dz = -ALTAR_REACH; dz <= ALTAR_REACH; dz++) {
                int x = origin.getX() + dx;
                int z = origin.getZ() + dz;
                if (!level.hasChunkAt(x, z)) {
                    continue;
                }
                boolean ring = Math.max(Math.abs(dx), Math.abs(dz)) == ALTAR_REACH;
                BlockState deck = ring ? stair(dx, dz, random)
                        : dx == 0 && dz == 0 ? Blocks.CHISELED_STONE_BRICKS.defaultBlockState()
                        : masonry(random);
                place(level, chunkBB, new BlockPos(x, deckY, z), deck);
                place(level, chunkBB, new BlockPos(x, deckY + 1, z), Blocks.AIR.defaultBlockState());
            }
        }
        place(level, chunkBB, new BlockPos(origin.getX(), deckY + 1, origin.getZ()),
                ModBlocks.VESTIGE_TABLE.get().defaultBlockState());
        garrisonTheSummit(level, chunkBB, deckY + 1);
    }

    private void garrisonTheSummit(WorldGenLevel level, BoundingBox chunkBB, int standY) {
        BlockPos lordPos = new BlockPos(origin.getX(), standY, origin.getZ() + 2);
        if (!chunkBB.isInside(lordPos) || !level.hasChunkAt(lordPos.getX(), lordPos.getZ())) {
            return;
        }
        if (!level.getEntitiesOfClass(MayanNacom.class,
                new AABB(lordPos).inflate(SIZE / 2.0D)).isEmpty()) {
            return;
        }
        summon(level, ModEntities.MAYAN_NACOM.get(), lordPos);
        summon(level, ModEntities.MAYAN_SHAMAN.get(), lordPos.offset(-3, 0, -1));
        summon(level, ModEntities.MAYAN_SHAMAN.get(), lordPos.offset(3, 0, -1));
    }

    private void summon(WorldGenLevel level, EntityType<? extends Mob> type, BlockPos pos) {
        Mob mob = type.create(level.getLevel(), EntitySpawnReason.STRUCTURE);
        if (mob == null) {
            return;
        }
        mob.snapTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, 180.0F, 0.0F);
        mob.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), EntitySpawnReason.STRUCTURE, null);
        mob.setPersistenceRequired();
        level.addFreshEntity(mob);
    }

    private void place(WorldGenLevel level, BoundingBox chunkBB, BlockPos pos, BlockState state) {
        if (chunkBB.isInside(pos)) {
            level.setBlock(pos, state, 2);
        }
    }

    private static BlockState stair(int dx, int dz, RandomSource random) {
        BlockState state = switch (random.nextInt(6)) {
            case 0, 1 -> Blocks.STONE_BRICK_STAIRS.defaultBlockState();
            case 2, 3 -> Blocks.MOSSY_STONE_BRICK_STAIRS.defaultBlockState();
            case 4 -> Blocks.COBBLESTONE_STAIRS.defaultBlockState();
            default -> Blocks.MOSSY_COBBLESTONE_STAIRS.defaultBlockState();
        };
        boolean corner = Math.abs(dx) == ALTAR_REACH && Math.abs(dz) == ALTAR_REACH;
        if (!corner) {
            Direction inward = Math.abs(dx) == ALTAR_REACH
                    ? (dx < 0 ? Direction.EAST : Direction.WEST)
                    : (dz < 0 ? Direction.SOUTH : Direction.NORTH);
            return state.setValue(StairBlock.FACING, inward);
        }
        Direction facing = dz < 0 ? Direction.SOUTH : Direction.NORTH;
        boolean left = (dz < 0) == (dx < 0);
        return state.setValue(StairBlock.FACING, facing)
                .setValue(StairBlock.SHAPE, left ? StairsShape.OUTER_LEFT : StairsShape.OUTER_RIGHT);
    }

    private static BlockState masonry(RandomSource random) {
        return switch (random.nextInt(8)) {
            case 0, 1 -> Blocks.STONE_BRICKS.defaultBlockState();
            case 2, 3 -> Blocks.MOSSY_STONE_BRICKS.defaultBlockState();
            case 4, 5 -> Blocks.CRACKED_STONE_BRICKS.defaultBlockState();
            case 6 -> Blocks.COBBLESTONE.defaultBlockState();
            default -> Blocks.MOSSY_COBBLESTONE.defaultBlockState();
        };
    }

    private void footing(WorldGenLevel level, BoundingBox chunkBB, RandomSource random) {
        int half = SIZE / 2;
        for (int dx = -half; dx <= half; dx++) {
            for (int dz = -half; dz <= half; dz++) {
                boolean rim = Math.max(Math.abs(dx), Math.abs(dz)) >= half - 2;
                column(level, chunkBB, random, origin.getX() + dx, origin.getZ() + dz, rim);
            }
        }
    }

    private void column(WorldGenLevel level, BoundingBox chunkBB, RandomSource random,
                        int x, int z, boolean rim) {
        if (!level.hasChunkAt(x, z)) {
            return;
        }
        int floor = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z);
        int from = origin.getY() - 1;
        int bottom = Math.max(floor - 2, from - 48);

        for (int y = from; y >= bottom; y--) {
            BlockPos pos = new BlockPos(x, y, z);
            if (!chunkBB.isInside(pos)) {
                continue;
            }
            BlockState state = y == from && !rim ? Blocks.GRASS_BLOCK.defaultBlockState() : rubble(random);
            level.setBlock(pos, state, 2);
        }
    }

    private static BlockState rubble(RandomSource random) {
        return switch (random.nextInt(8)) {
            case 0, 1, 2 -> Blocks.COBBLESTONE.defaultBlockState();
            case 3, 4 -> Blocks.MOSSY_COBBLESTONE.defaultBlockState();
            case 5, 6 -> Blocks.STONE.defaultBlockState();
            default -> Blocks.GRAVEL.defaultBlockState();
        };
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
                origin.getY(),
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
