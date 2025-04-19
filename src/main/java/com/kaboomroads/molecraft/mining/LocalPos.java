package com.kaboomroads.molecraft.mining;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public record LocalPos(ServerLevel level, BlockPos pos) {
}
