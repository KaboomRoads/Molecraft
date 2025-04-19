package com.kaboomroads.molecraft.util;

import com.kaboomroads.molecraft.menu.TradeMenu;
import com.kaboomroads.molecraft.mixinimpl.ModPlayer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class TradeData {
    public final ServerPlayer sender;
    public final ServerPlayer receiver;
    public final SimpleContainer senderContainer;
    public final SimpleContainer receiverContainer;
    public boolean traded = false;
    public boolean senderConfirm = false;
    public boolean receiverConfirm = false;
    public int tradeTime = getMaxTradeTime();
    public TradeMenu senderMenu;
    public TradeMenu receiverMenu;
    public int nextSeconds;

    public int getMaxTradeTime() {
        return 60;
    }

    public TradeData(ServerPlayer sender, ServerPlayer receiver, SimpleContainer senderContainer, SimpleContainer receiverContainer) {
        this.sender = sender;
        this.receiver = receiver;
        this.senderContainer = senderContainer;
        this.receiverContainer = receiverContainer;
    }

    public int left(int index) {
        return index / 4 * 9 + index % 4;
    }

    public int fromLeft(int index) {
        return (index / 9) * 4 + (index % 9 % 4);
    }

    public int right(int index) {
        return left(index) + 5;
    }

    public int fromRight(int index) {
        return fromLeft(index - 5);
    }

    public void trade() {
        if (!traded) {
            traded = true;
            ((ModPlayer) sender).molecraft$setTickingTradeData(null);
            ((ModPlayer) receiver).molecraft$setTickingTradeData(null);
            Component playerName = sender.getDisplayName();
            Component otherName = receiver.getDisplayName();
            sender.displayClientMessage(Component.literal("Trade completed with ").append(otherName).withStyle(ChatFormatting.GREEN), false);
            receiver.displayClientMessage(Component.literal("Trade completed with ").append(playerName).withStyle(ChatFormatting.GREEN), false);
            listTrade(receiverContainer, sender, receiver);
            listTrade(senderContainer, receiver, sender);
            TradeMenu.addOrDropBound(receiverContainer, sender);
            TradeMenu.addOrDropBound(senderContainer, receiver);
            sender.closeContainer();
            receiver.closeContainer();
        }
    }

    private void listTrade(Container self, Player tradeulous, Player player) {
        for (int i = 0; i < self.getContainerSize(); i++) {
            ItemStack itemStack = self.getItem(i);
            if (itemStack.isEmpty()) continue;
            Component stackComponent = Component.literal(itemStack.getCount() + "x ").append(itemStack.getDisplayName());
            tradeulous.displayClientMessage(Component.literal("+ ").withStyle(ChatFormatting.GRAY).append(stackComponent), false);
            player.displayClientMessage(Component.literal("- ").withStyle(ChatFormatting.RED).append(stackComponent), false);
        }
    }
}