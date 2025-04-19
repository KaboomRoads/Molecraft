package com.kaboomroads.molecraft.mixinimpl;

import com.kaboomroads.molecraft.trading.PendingTrade;
import com.kaboomroads.molecraft.trading.TradeData;
import org.jetbrains.annotations.Nullable;

public interface ModPlayer {
    TradeData molecraft$getTickingTradeData();

    void molecraft$setTickingTradeData(TradeData tradeData);

    @Nullable
    PendingTrade molecraft$getSentTrade();

    void molecraft$setSentTrade(PendingTrade sentTrade);

    long molecraft$getCoins();

    void molecraft$setCoins(long coins);
}
