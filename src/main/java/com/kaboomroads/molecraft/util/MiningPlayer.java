package com.kaboomroads.molecraft.util;

import net.minecraft.world.entity.player.Player;

import java.util.Objects;

public class MiningPlayer {
    public Player player;
    public long lastTick;

    public MiningPlayer(Player player, long lastTick) {
        this.player = player;
        this.lastTick = lastTick;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (MiningPlayer) obj;
        return Objects.equals(this.player, that.player) &&
                this.lastTick == that.lastTick;
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, lastTick);
    }
}
