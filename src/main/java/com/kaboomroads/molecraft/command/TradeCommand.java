package com.kaboomroads.molecraft.command;

import com.kaboomroads.molecraft.mixinimpl.ModPlayer;
import com.kaboomroads.molecraft.trading.PendingTrade;
import com.kaboomroads.molecraft.trading.TradeData;
import com.kaboomroads.molecraft.trading.TradeMenu;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;

public class TradeCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("trade")
                        .then(
                                Commands.argument("targets", EntityArgument.player())
                                        .executes(context -> {
                                            CommandSourceStack source = context.getSource();
                                            ServerPlayer player = source.getPlayer();
                                            ServerPlayer tradeulous = EntityArgument.getPlayer(context, "targets");
                                            if (player != null && !player.equals(tradeulous)) {
                                                ModPlayer modPlayer = (ModPlayer) player;
                                                PendingTrade sentTrade = modPlayer.molecraft$getSentTrade();
                                                if (sentTrade != null) {
                                                    source.sendFailure(Component.literal("You already have a trade pending!").withStyle(ChatFormatting.RED));
                                                    return 0;
                                                }
                                                PendingTrade otherSent = ((ModPlayer) tradeulous).molecraft$getSentTrade();
                                                if (otherSent != null && otherSent.receiver.equals(player.getUUID())) {
                                                    otherSent.accept(source.getLevel());
                                                    return 1;
                                                }
                                                PendingTrade pendingTrade = new PendingTrade(player.getUUID(), tradeulous.getUUID(), (sender, receiver) -> {
                                                    ((ModPlayer) sender).molecraft$setSentTrade(null);
                                                    ((ModPlayer) receiver).molecraft$setSentTrade(null);
                                                    SimpleContainer playerContainer = new SimpleContainer(20);
                                                    SimpleContainer tradeulousContainer = new SimpleContainer(20);
                                                    TradeData tradeData = new TradeData(sender, receiver, playerContainer, tradeulousContainer);
                                                    sender.openMenu(new SimpleMenuProvider((i, inventory, p) -> new TradeMenu(i, p.getInventory(), tradeData, true), Component.literal("Trade with ").append(tradeulous.getDisplayName())));
                                                    receiver.openMenu(new SimpleMenuProvider((i, inventory, p) -> new TradeMenu(i, p.getInventory(), tradeData, false), Component.literal("Trade with ").append(player.getDisplayName())));
                                                    ((ModPlayer) sender).molecraft$setTickingTradeData(tradeData);
                                                });
                                                modPlayer.molecraft$setSentTrade(pendingTrade);
                                                Component displayName = player.getDisplayName();
                                                source.sendSuccess(() -> Component.literal("Sent trade request to ").append(tradeulous.getDisplayName()).withStyle(ChatFormatting.GREEN).append(Component.literal(" (expires in 30s)").withStyle(ChatFormatting.GRAY)), false);
                                                tradeulous.sendSystemMessage(displayName.copy().append(" sent you a trade request. ").append(Component.literal("[click to accept]").withStyle(
                                                        style -> style.withColor(ChatFormatting.GREEN)
                                                                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/trade " + displayName.getString()))
                                                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal("accept trade")))
                                                )));
                                                return 1;
                                            }
                                            return 0;
                                        })
                        )
        );
    }
}
