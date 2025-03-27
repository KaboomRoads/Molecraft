package com.kaboomroads.molecraft.mixinimpl;

import com.kaboomroads.molecraft.item.StatInstance;
import com.kaboomroads.molecraft.item.StatType;

import java.util.TreeMap;

public interface MolecraftLivingEntity {
    TreeMap<StatType, StatInstance> molecraft$getStats();

    double molecraft$getHealth();

    void molecraft$setHealth(double health);

    double molecraft$getMana();

    void molecraft$setMana(double mana);
}
