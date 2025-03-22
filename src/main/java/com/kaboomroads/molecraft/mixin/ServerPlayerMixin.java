package com.kaboomroads.molecraft.mixin;

import com.kaboomroads.molecraft.mixinimpl.MolecraftServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin implements MolecraftServerPlayer {
    @Unique
    private BlockPos currentlyMining;
    @Unique
    private long lastMineSwing;

    @Override
    public BlockPos molecraft$getCurrentlyMining() {
        return currentlyMining;
    }

    @Override
    public void molecraft$setCurrentlyMining(BlockPos currentlyMining) {
        this.currentlyMining = currentlyMining;
    }

    @Override
    public long molecraft$getLastMineSwing() {
        return lastMineSwing;
    }

    @Override
    public void molecraft$setLastMineSwing(long lastMineSwing) {
        this.lastMineSwing = lastMineSwing;
    }
}
