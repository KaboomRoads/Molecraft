package com.kaboomroads.molecraft.item.ability.core;

import net.minecraft.network.chat.Component;

public class MolecraftEnchant<W extends When, C extends WhenContext<W>> extends Ability<W, C, EnchantWhat<W, C>> {
    public final int highestAchievableLevel;

    public MolecraftEnchant(String id, Component name, W when, EnchantWhat<W, C> what, int highestAchievableLevel1) {
        super(id, name, when, what);
        this.highestAchievableLevel = highestAchievableLevel1;
    }
}
