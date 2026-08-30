package com.emiliomanco.vestigia.command;

import com.emiliomanco.vestigia.Civilization;
import com.emiliomanco.vestigia.Vestigia;
import com.emiliomanco.vestigia.registry.ModItemTags;
import com.emiliomanco.vestigia.registry.ModItems;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class VestigiaCommand {
    private VestigiaCommand() {}

    private static final SuggestionProvider<CommandSourceStack> CIVILIZATIONS =
            (context, builder) -> SharedSuggestionProvider.suggest(
                    java.util.Arrays.stream(Civilization.values()).map(Civilization::id), builder);

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("vestigia")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(giveTree())
                .then(literal("civilizations").executes(VestigiaCommand::listCivilizations)));
    }

    private static LiteralArgumentBuilder<CommandSourceStack> giveTree() {
        return literal("give")
                .then(literal("artifacts")
                        .then(Commands.argument("civilization", StringArgumentType.word())
                                .suggests(CIVILIZATIONS)
                                .executes(ctx -> giveArtifacts(ctx, StringArgumentType.getString(ctx, "civilization")))))
                .then(literal("vestige")
                        .then(Commands.argument("civilization", StringArgumentType.word())
                                .suggests(CIVILIZATIONS)
                                .executes(ctx -> giveVestige(ctx, StringArgumentType.getString(ctx, "civilization")))))
                .then(literal("everything").executes(VestigiaCommand::giveEverything));
    }

    private static int giveArtifacts(CommandContext<CommandSourceStack> ctx, String civilizationId) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        Civilization civilization = parseCivilization(ctx, civilizationId);
        if (civilization == null) {
            return 0;
        }
        int given = switch (civilization) {
            case MAYA -> grant(player, ModItems.MACUAHUITL.get());
            default -> 0;
        };
        if (given == 0) {
            ctx.getSource().sendSuccess(() -> Component.literal(
                    "No artifacts implemented yet for " + civilization.id()).withStyle(ChatFormatting.YELLOW), false);
        } else {
            final int count = given;
            ctx.getSource().sendSuccess(() -> Component.literal(
                    "Gave " + count + " " + civilization.id() + " artifacts"), false);
        }
        return given;
    }

    private static int giveVestige(CommandContext<CommandSourceStack> ctx, String civilizationId) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        Civilization civilization = parseCivilization(ctx, civilizationId);
        if (civilization == null) {
            return 0;
        }
        int given = switch (civilization) {
            case INCA -> grant(player, ModItems.PUNCHAO.get());
            default -> 0;
        };
        if (given == 0) {
            ctx.getSource().sendSuccess(() -> Component.literal(
                    "No vestige implemented yet for " + civilization.id()).withStyle(ChatFormatting.YELLOW), false);
        }
        return given;
    }

    private static int giveEverything(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayerOrException();
        int given = 0;
        for (var tag : new net.minecraft.tags.TagKey[] {
                ModItemTags.ARTIFACTS, ModItemTags.VESTIGES, ModItemTags.GOD_ITEMS, ModItemTags.CARVED_STONE}) {
            @SuppressWarnings("unchecked")
            var itemTag = (net.minecraft.tags.TagKey<Item>) tag;
            for (var holder : BuiltInRegistries.ITEM.getTagOrEmpty(itemTag)) {
                given += grant(player, holder.value());
            }
        }
        given += grant(player, ModItems.VESTIGE_TABLE.get());
        final int count = given;
        ctx.getSource().sendSuccess(() -> Component.literal("Gave " + count + " Vestigia items"), false);
        return given;
    }

    private static int listCivilizations(CommandContext<CommandSourceStack> ctx) {
        ctx.getSource().sendSuccess(() -> Component.literal("Civilizations, in ritual order:")
                .withStyle(ChatFormatting.GOLD), false);
        for (Civilization civilization : Civilization.values()) {
            ctx.getSource().sendSuccess(() -> Component.literal("  " + civilization.id()), false);
        }
        return Civilization.values().length;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> literal(String name) {
        return Commands.literal(name);
    }

    private static @org.jspecify.annotations.Nullable Civilization parseCivilization(
            CommandContext<CommandSourceStack> ctx, String id) {
        for (Civilization civilization : Civilization.values()) {
            if (civilization.id().equals(id.toLowerCase(Locale.ROOT))) {
                return civilization;
            }
        }
        ctx.getSource().sendFailure(Component.literal("Unknown civilization: " + id));
        return null;
    }

    private static int grant(ServerPlayer player, Item... items) {
        for (Item item : items) {
            ItemStack stack = new ItemStack(item);
            if (!player.getInventory().add(stack)) {
                player.drop(stack, false);
            }
        }
        Vestigia.LOGGER.debug("Granted {} debug items to {}", items.length, player.getName().getString());
        return items.length;
    }
}
