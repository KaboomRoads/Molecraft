package com.kaboomroads.molecraft.mixinimpl;

import net.minecraft.core.BlockPos;

public interface MolecraftServerPlayer {
    BlockPos molecraft$getCurrentlyMining();

    void molecraft$setCurrentlyMining(BlockPos pos);

    long molecraft$getLastMineSwing();

    void molecraft$setLastMineSwing(long time);
}
