package com.kaboomroads.molecraft.item.ability;

import com.kaboomroads.molecraft.item.ability.core.*;
import net.minecraft.network.chat.Component;

public class Igniting extends MolecraftEnchant<When.PostHit, PostHitWhenContext> {
    public Igniting(String id, Component name, int highestAchievableLevel) {
        super(id, name, WhenType.POST_HIT, new EnchantWhat<>() {
            @Override
            public void run(PostHitWhenContext context, int enchantLevel) {
                context.hit.igniteForSeconds(enchantLevel);
            }
        }, highestAchievableLevel);
    }
}
