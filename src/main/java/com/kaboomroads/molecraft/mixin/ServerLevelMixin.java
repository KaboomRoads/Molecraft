package com.kaboomroads.molecraft.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
    @Unique
    private final HashMap<BlockPos, Double> blocksBeingMined = new HashMap<>();


}
