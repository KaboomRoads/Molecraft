package com.kaboomroads.molecraft.trading;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;
import java.util.UUID;

public class PendingTrade {
    public final UUID sender;
    public final UUID receiver;
    private final Trade trade;
    public int expireTime = 600;

    public PendingTrade(UUID sender, UUID receiver, Trade trade) {
        this.sender = sender;
        this.receiver = receiver;
        this.trade = trade;
    }

    public void accept(ServerLevel level) {
        trade.accept((ServerPlayer) level.getPlayerByUUID(sender), (ServerPlayer) level.getPlayerByUUID(receiver));
    }

    @FunctionalInterface
    public interface Trade {
        void accept(ServerPlayer sender, ServerPlayer receiver);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PendingTrade that = (PendingTrade) o;
        return Objects.equals(sender, that.sender) && Objects.equals(receiver, that.receiver);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, receiver);
    }
}