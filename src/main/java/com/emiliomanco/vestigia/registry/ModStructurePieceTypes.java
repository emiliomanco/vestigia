package com.emiliomanco.vestigia.registry;

import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.worldgen.structure.IncanLunarStonePiece;
import com.emiliomanco.vestigia.worldgen.structure.IncanTempleOfIntiPiece;
import com.emiliomanco.vestigia.worldgen.structure.MayaPyramidPiece;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModStructurePieceTypes {
    private ModStructurePieceTypes() {}

    public static final DeferredRegister<StructurePieceType> PIECE_TYPES =
            DeferredRegister.create(Registries.STRUCTURE_PIECE, Vestigia.MODID);

    public static final DeferredHolder<StructurePieceType, StructurePieceType> MAYA_PYRAMID =
            PIECE_TYPES.register("maya_pyramid",
                    () -> (StructurePieceType.ContextlessType) MayaPyramidPiece::new);

    public static final DeferredHolder<StructurePieceType, StructurePieceType> INCAN_LUNAR_STONE =
            PIECE_TYPES.register("incan_lunar_stone",
                    () -> (StructurePieceType.ContextlessType) IncanLunarStonePiece::new);

    public static final DeferredHolder<StructurePieceType, StructurePieceType> INCAN_TEMPLE_OF_INTI =
            PIECE_TYPES.register("incan_temple_of_inti",
                    () -> (StructurePieceType.ContextlessType) IncanTempleOfIntiPiece::new);

    public static void register(IEventBus modEventBus) {
        PIECE_TYPES.register(modEventBus);
    }
}
