package com.kaboomroads.molecraft.item.ability.core;

import net.minecraft.world.entity.Entity;

public class PostHitWhenContext extends WhenContext<When.PostHit> {
    public final Entity attacker;
    public final Entity hit;

    public PostHitWhenContext(Entity attacker, Entity hit) {
        this.attacker = attacker;
        this.hit = hit;
    }
}
