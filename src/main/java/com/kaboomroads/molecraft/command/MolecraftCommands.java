package com.kaboomroads.molecraft.command;

import com.kaboomroads.molecraft.entity.MolecraftEntities;
import com.kaboomroads.molecraft.entity.MolecraftEntity;
import com.kaboomroads.molecraft.item.MolecraftItem;
import com.kaboomroads.molecraft.item.MolecraftItems;
import com.kaboomroads.molecraft.mixinimpl.ModPlayer;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;

public class MolecraftCommands {
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_MOLECRAFT_ITEM = (commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggest(
            MolecraftItems.ITEMS.keySet(), suggestionsBuilder
    );
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_MOLECRAFT_ENTITY = (commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggest(
            MolecraftEntities.ENTITIES.keySet(), suggestionsBuilder
    );

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("molecraftitem")
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                        .then(
                                Commands.argument("targets", EntityArgument.players())
                                        .then(
                                                Commands.argument("item", StringArgumentType.string()).suggests(SUGGEST_MOLECRAFT_ITEM)
                                                        .executes(
                                                                commandContext -> giveItem(
                                                                        commandContext.getSource(), StringArgumentType.getString(commandContext, "item"), EntityArgument.getPlayers(commandContext, "targets"), 1
                                                                )
                                                        )
                                                        .then(
                                                                Commands.argument("count", IntegerArgumentType.integer(1))
                                                                        .executes(
                                                                                commandContext -> giveItem(
                                                                                        commandContext.getSource(),
                                                                                        StringArgumentType.getString(commandContext, "item"),
                                                                                        EntityArgument.getPlayers(commandContext, "targets"),
                                                                                        IntegerArgumentType.getInteger(commandContext, "count")
                                                                                )
                                                                        )
                                                        )
                                        )
                        )
        );
        dispatcher.register(
                Commands.literal("molecraftentity")
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                        .then(
                                Commands.argument("entity", StringArgumentType.string()).suggests(SUGGEST_MOLECRAFT_ENTITY)
                                        .executes(
                                                commandContext -> spawnEntity(
                                                        commandContext.getSource(),
                                                        StringArgumentType.getString(commandContext, "entity"),
                                                        commandContext.getSource().getPosition()
                                                )
                                        )
                                        .then(
                                                Commands.argument("pos", Vec3Argument.vec3())
                                                        .executes(
                                                                commandContext -> spawnEntity(
                                                                        commandContext.getSource(),
                                                                        StringArgumentType.getString(commandContext, "entity"),
                                                                        Vec3Argument.getVec3(commandContext, "pos")
                                                                )
                                                        )
                                        )
                        )
        );
        dispatcher.register(
                Commands.literal("molecraftcoins")
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                        .then(
                                Commands.argument("targets", EntityArgument.players())
                                        .then(Commands.literal("add")
                                                .then(
                                                        Commands.argument("amount", LongArgumentType.longArg())
                                                                .executes(
                                                                        context -> {
                                                                            ServerPlayer player = context.getSource().getPlayer();
                                                                            if (player != null) {
                                                                                ((ModPlayer) player).molecraft$setCoins(((ModPlayer) player).molecraft$getCoins() + LongArgumentType.getLong(context, "amount"));
                                                                                return 1;
                                                                            }
                                                                            return 0;
                                                                        }
                                                                )
                                                )
                                        )
                                        .then(Commands.literal("set")
                                                .then(
                                                        Commands.argument("amount", LongArgumentType.longArg())
                                                                .executes(
                                                                        context -> {
                                                                            ServerPlayer player = context.getSource().getPlayer();
                                                                            if (player != null) {
                                                                                ((ModPlayer) player).molecraft$setCoins(LongArgumentType.getLong(context, "amount"));
                                                                                return 1;
                                                                            }
                                                                            return 0;
                                                                        }
                                                                )
                                                )
                                        )
                        )
        );
        dispatcher.register(
                Commands.literal("ce")
                        .requires(commandSourceStack -> commandSourceStack.hasPermission(2))
                        .executes(context -> {
                            Commands commands = context.getSource().getServer().getCommands();
                            String command = "kill @e[type=!minecraft:player]";
                            commands.performCommand(commands.getDispatcher().parse(command, context.getSource()), command);
                            return 1;
                        })
        );
    }

    private static int giveItem(CommandSourceStack source, String itemId, Collection<ServerPlayer> targets, int count) throws CommandSyntaxException {
        if (!MolecraftItems.ITEMS.containsKey(itemId)) return 0;
        MolecraftItem molecraftItem = MolecraftItems.ITEMS.get(itemId);
        ItemStack itemStack = molecraftItem.construct(count, source.registryAccess());
        int i = itemStack.getMaxStackSize();
        int j = i * 100;
        if (count > j) {
            source.sendFailure(Component.translatable("commands.give.failed.toomanyitems", j, itemStack.getDisplayName()));
            return 0;
        } else {
            for (ServerPlayer serverPlayer : targets) {
                int k = count;
                while (k > 0) {
                    int l = Math.min(i, k);
                    k -= l;
                    ItemStack itemStack2 = molecraftItem.construct(l, source.registryAccess());
                    boolean bl = serverPlayer.getInventory().add(itemStack2);
                    if (bl && itemStack2.isEmpty()) {
                        ItemEntity itemEntity = serverPlayer.drop(itemStack, false);
                        if (itemEntity != null) itemEntity.makeFakeItem();
                        serverPlayer.level()
                                .playSound(
                                        null,
                                        serverPlayer.getX(),
                                        serverPlayer.getY(),
                                        serverPlayer.getZ(),
                                        SoundEvents.ITEM_PICKUP,
                                        SoundSource.PLAYERS,
                                        0.2F,
                                        ((serverPlayer.getRandom().nextFloat() - serverPlayer.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F
                                );
                        serverPlayer.containerMenu.broadcastChanges();
                    } else {
                        ItemEntity itemEntity = serverPlayer.drop(itemStack2, false);
                        if (itemEntity != null) {
                            itemEntity.setNoPickUpDelay();
                            itemEntity.setTarget(serverPlayer.getUUID());
                        }
                    }
                }
            }
            if (targets.size() == 1) {
                source.sendSuccess(
                        () -> Component.translatable("commands.give.success.single", count, itemStack.getDisplayName(), ((ServerPlayer) targets.iterator().next()).getDisplayName()),
                        true
                );
            } else {
                source.sendSuccess(() -> Component.translatable("commands.give.success.single", count, itemStack.getDisplayName(), targets.size()), true);
            }
            return targets.size();
        }
    }

    private static int spawnEntity(CommandSourceStack source, String id, Vec3 pos) throws CommandSyntaxException {
        Entity entity = createEntity(source, id, pos);
        source.sendSuccess(() -> Component.translatable("commands.summon.success", entity.getDisplayName()), true);
        return 1;
    }

    private static final SimpleCommandExceptionType ERROR_DUPLICATE_UUID = new SimpleCommandExceptionType(Component.translatable("commands.summon.failed.uuid"));
    private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType(Component.translatable("commands.summon.invalidPosition"));

    public static Entity createEntity(CommandSourceStack source, String id, Vec3 pos) throws CommandSyntaxException {
        BlockPos blockPos = BlockPos.containing(pos);
        if (!Level.isInSpawnableBounds(blockPos)) {
            throw INVALID_POSITION.create();
        } else {
            MolecraftEntity molecraftEntity = MolecraftEntities.ENTITIES.get(id);
            ServerLevel serverLevel = source.getLevel();
            Entity entity = molecraftEntity.construct(serverLevel);
            entity.moveTo(pos.x, pos.y, pos.z);
            if (!serverLevel.tryAddFreshEntityWithPassengers(entity)) {
                throw ERROR_DUPLICATE_UUID.create();
            } else {
                return entity;
            }
        }
    }
}
