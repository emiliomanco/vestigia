package com.emiliomanco.vestigia.worldgen.structure;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.entity.guardian.IncanApuskipay;
import com.emiliomanco.vestigia.registry.ModEntities;
import com.emiliomanco.vestigia.registry.ModItems;
import com.emiliomanco.vestigia.registry.ModStructurePieceTypes;
import com.emiliomanco.vestigia.worldgen.structure.kit.StableRandom;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.decoration.GlowItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;

public class IncanTempleOfIntiPiece extends StructurePieceBase {

    private static final long SALT = 0x1E7A_1470_0003L;
    private static final long GARRISON_SALT = 0x1E7A_1470_0004L;

    private static final String TEMPLATE = "incan_temple_of_inti";

    private static final int SIZE_X = 75;
    private static final int SIZE_Y = 23;
    private static final int SIZE_Z = 61;

    private static final BlockPos BOSS = new BlockPos(20, 16, 30);

    private static final BlockPos PUNCHAO_FRAME = new BlockPos(17, 19, 30);
    private static final Direction PUNCHAO_FACING = Direction.EAST;

    private static final BlockPos[] PRIESTS = {
            new BlockPos(38, 11, 13),
            new BlockPos(38, 11, 47),
            new BlockPos(8, 11, 12),
            new BlockPos(8, 11, 48),
    };

    private static final BlockPos[] WARRIORS = {
            new BlockPos(50, 8, 30),
            new BlockPos(6, 8, 30),
            new BlockPos(23, 8, 8),
            new BlockPos(23, 8, 52),
            new BlockPos(35, 11, 30),
            new BlockPos(25, 15, 14),
            new BlockPos(25, 15, 46),
            new BlockPos(12, 18, 19),
            new BlockPos(12, 18, 41),
            new BlockPos(10, 14, 24),
            new BlockPos(68, 3, 6),
            new BlockPos(68, 3, 54),
            new BlockPos(68, 3, 30),
            new BlockPos(48, 6, 6),
            new BlockPos(48, 6, 54),
            new BlockPos(6, 6, 6),
            new BlockPos(6, 6, 54),
            new BlockPos(59, 5, 18),
            new BlockPos(59, 5, 42),
            new BlockPos(35, 6, 6),
    };

    public IncanTempleOfIntiPiece(BlockPos origin) {
        super(ModStructurePieceTypes.INCAN_TEMPLE_OF_INTI.get(), origin, boundsAround(origin));
    }

    public IncanTempleOfIntiPiece(CompoundTag tag) {
        super(ModStructurePieceTypes.INCAN_TEMPLE_OF_INTI.get(), tag);
    }

    private static BoundingBox boundsAround(BlockPos origin) {
        int halfX = SIZE_X / 2 + 1;
        int halfZ = SIZE_Z / 2 + 1;
        return new BoundingBox(
                origin.getX() - halfX, origin.getY() - 1, origin.getZ() - halfZ,
                origin.getX() + halfX, origin.getY() + SIZE_Y + 2, origin.getZ() + halfZ);
    }

    private BlockPos corner() {
        return new BlockPos(
                origin.getX() - SIZE_X / 2,
                origin.getY(),
                origin.getZ() - SIZE_Z / 2);
    }

    private BlockPos world(BlockPos local) {
        return corner().offset(local);
    }

    @Override
    public void postProcess(WorldGenLevel level, StructureManager structureManager, ChunkGenerator generator,
                            RandomSource chunkRandom, BoundingBox chunkBB, ChunkPos chunkPos, BlockPos referencePos) {
        footing(level, chunkBB);
        placeTemplate(level, chunkBB);
        hangThePunchao(level, chunkBB);
        garrison(level, chunkBB);
    }

    private void footing(WorldGenLevel level, BoundingBox chunkBB) {
        BlockPos corner = corner();
        for (int lx = 0; lx < SIZE_X; lx++) {
            for (int lz = 0; lz < SIZE_Z; lz++) {
                column(level, chunkBB, corner.getX() + lx, corner.getZ() + lz);
            }
        }
    }

    private void column(WorldGenLevel level, BoundingBox chunkBB, int x, int z) {
        if (!level.hasChunkAt(x, z)) {
            return;
        }
        BlockState packedMud = Blocks.PACKED_MUD.defaultBlockState();
        int floor = level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, x, z);
        int from = origin.getY() - 1;
        int bottom = Math.max(floor - 2, from - 48);

        for (int y = from; y >= bottom; y--) {
            BlockPos pos = new BlockPos(x, y, z);
            if (chunkBB.isInside(pos)) {
                level.setBlock(pos, packedMud, 2);
            }
        }
    }

    private void placeTemplate(WorldGenLevel level, BoundingBox chunkBB) {
        Optional<StructureTemplate> found =
                level.getLevel().getStructureManager().get(Vestigia.id(TEMPLATE));
        if (found.isEmpty()) {
            return;
        }
        StructureTemplate template = found.get();
        Vec3i size = template.getSize();
        BlockPos corner = new BlockPos(
                origin.getX() - size.getX() / 2,
                origin.getY(),
                origin.getZ() - size.getZ() / 2);

        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setBoundingBox(chunkBB)
                .setIgnoreEntities(true);

        template.placeInWorld(level, corner, corner, settings,
                StableRandom.forOrigin(origin, SALT ^ 0x9E37L), 2);
    }

    private void hangThePunchao(WorldGenLevel level, BoundingBox chunkBB) {
        BlockPos pos = world(PUNCHAO_FRAME);
        if (!chunkBB.isInside(pos) || !level.hasChunkAt(pos.getX(), pos.getZ())) {
            return;
        }
        if (!level.getEntitiesOfClass(GlowItemFrame.class, new AABB(pos).inflate(1.0D)).isEmpty()) {
            return;
        }
        GlowItemFrame frame = new GlowItemFrame(level.getLevel(), pos, PUNCHAO_FACING);
        frame.setItem(new ItemStack(ModItems.PUNCHAO.get()), false);
        level.addFreshEntity(frame);
    }

    private void garrison(WorldGenLevel level, BoundingBox chunkBB) {
        for (BlockPos local : WARRIORS) {
            place(level, chunkBB, ModEntities.INCAN_WARRIOR.get(), world(local));
        }
        for (BlockPos local : PRIESTS) {
            place(level, chunkBB, ModEntities.INCAN_PRIEST.get(), world(local));
        }
        placeBoss(level, chunkBB);
    }

    private void placeBoss(WorldGenLevel level, BoundingBox chunkBB) {
        BlockPos pos = world(BOSS);
        if (!chunkBB.isInside(pos) || !level.hasChunkAt(pos.getX(), pos.getZ())) {
            return;
        }
        if (!level.getEntitiesOfClass(IncanApuskipay.class, new AABB(pos).inflate(SIZE_X / 2.0D)).isEmpty()) {
            return;
        }
        place(level, chunkBB, ModEntities.INCAN_APUSKIPAY.get(), pos);
    }

    private void place(WorldGenLevel level, BoundingBox chunkBB, EntityType<? extends Mob> type, BlockPos pos) {
        if (!chunkBB.isInside(pos) || !level.hasChunkAt(pos.getX(), pos.getZ())) {
            return;
        }
        Mob mob = type.create(level.getLevel(), EntitySpawnReason.STRUCTURE);
        if (mob == null) {
            return;
        }
        RandomSource random = StableRandom.forOrigin(pos, GARRISON_SALT);
        mob.snapTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, random.nextFloat() * 360.0F, 0.0F);
        mob.finalizeSpawn(level, level.getCurrentDifficultyAt(pos), EntitySpawnReason.STRUCTURE, null);
        mob.setPersistenceRequired();
        level.addFreshEntity(mob);
    }
}
