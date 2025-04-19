package com.kaboomroads.molecraft.util;

import net.minecraft.world.entity.player.Player;

import java.util.Objects;

public class MiningPlayer {
    public Player player;
    public double blockHealth;

    public MiningPlayer(Player player, double blockHealth) {
        this.player = player;
        this.blockHealth = blockHealth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MiningPlayer that = (MiningPlayer) o;
        return Double.compare(blockHealth, that.blockHealth) == 0 && Objects.equals(player, that.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, blockHealth);
    }
}
