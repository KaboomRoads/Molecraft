package com.kaboomroads.molecraft.mixinimpl;

import com.kaboomroads.molecraft.entity.Skills;
import com.kaboomroads.molecraft.trading.PendingTrade;
import com.kaboomroads.molecraft.trading.TradeData;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

public interface ModPlayer {
    TradeData molecraft$getTickingTradeData();

    void molecraft$setTickingTradeData(TradeData tradeData);

    @Nullable
    PendingTrade molecraft$getSentTrade();

    void molecraft$setSentTrade(PendingTrade sentTrade);

    long molecraft$getCoins();

    void molecraft$setCoins(long coins);

    Skills molecraft$getSkills();

    void molecraft$displayActionBar(Component component, int duration);

    double molecraft$getMana();

    void molecraft$setMana(double mana);
}
