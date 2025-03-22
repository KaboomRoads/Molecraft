package com.kaboomroads.molecraft.block;

import net.minecraft.world.level.block.Block;

public class MineableBlock extends Block {
    public final int interval;
    public final double maxHealth;

    public MineableBlock(Properties properties, int interval, double maxHealth) {
        super(properties);
        this.interval = interval;
        this.maxHealth = maxHealth;
    }
}
