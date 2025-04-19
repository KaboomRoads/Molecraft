package com.kaboomroads.molecraft.mixin;

import com.kaboomroads.molecraft.mining.Mining;
import com.kaboomroads.molecraft.mixinimpl.ModServerLevelData;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.ServerLevelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(DerivedLevelData.class)
public abstract class DerivedLevelDataMixin implements ModServerLevelData {
    @Shadow
    @Final
    private ServerLevelData wrapped;

    @Override
    public Mining molecraft$getMining() {
        return ((ModServerLevelData) wrapped).molecraft$getMining();
    }

    @Override
    public void molecraft$setMining(Mining mining) {
        ((ModServerLevelData) wrapped).molecraft$setMining(mining);
    }
}
