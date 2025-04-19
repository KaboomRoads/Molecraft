package com.kaboomroads.molecraft.mixinimpl;

import com.kaboomroads.molecraft.command.TradeCommand;
import com.kaboomroads.molecraft.util.TradeData;
import org.jetbrains.annotations.Nullable;

public interface ModPlayer {
    TradeData molecraft$getTickingTradeData();

    void molecraft$setTickingTradeData(TradeData tradeData);

    @Nullable
    TradeCommand.PendingTrade molecraft$getSentTrade();

    void molecraft$setSentTrade(TradeCommand.PendingTrade sentTrade);

    long molecraft$getCoins();

    void molecraft$setCoins(long coins);
}
