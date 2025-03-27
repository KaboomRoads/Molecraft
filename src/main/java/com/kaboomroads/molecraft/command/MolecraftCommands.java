package com.kaboomroads.molecraft.command;

import com.kaboomroads.molecraft.item.MolecraftItem;
import com.kaboomroads.molecraft.item.MolecraftItems;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;

public class MolecraftCommands {
    public static final SuggestionProvider<CommandSourceStack> SUGGEST_MOLECRAFT_ITEM = (commandContext, suggestionsBuilder) -> SharedSuggestionProvider.suggest(
            MolecraftItems.ITEMS.keySet(), suggestionsBuilder
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
    }

    private static int giveItem(CommandSourceStack source, String itemId, Collection<ServerPlayer> targets, int count) throws CommandSyntaxException {
        if (!MolecraftItems.ITEMS.containsKey(itemId)) return 0;
        MolecraftItem molecraftItem = MolecraftItems.ITEMS.get(itemId);
        ItemStack itemStack = molecraftItem.construct(count);
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
                    ItemStack itemStack2 = molecraftItem.construct(l);
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
}
