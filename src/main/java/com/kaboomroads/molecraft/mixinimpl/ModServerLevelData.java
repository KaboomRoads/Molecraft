package com.kaboomroads.molecraft.mixinimpl;

import com.kaboomroads.molecraft.loot.LootManager;
import com.kaboomroads.molecraft.mining.Mining;

public interface ModServerLevelData {
    Mining molecraft$getMining();

    void molecraft$setMining(Mining mining);

    LootManager molecraft$getLootManager();

    void molecraft$setLootManager(LootManager lootManager);
}
