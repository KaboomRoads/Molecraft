package com.kaboomroads.molecraft.mixinimpl;

import com.kaboomroads.molecraft.entity.StatsMap;

public interface ModLivingEntity extends ModEntity {
    StatsMap molecraft$getStats();

    void molecraft$setStats(StatsMap statsMap);

    StatsMap.Builder molecraft$initStats();

    double molecraft$getHealth();

    void molecraft$setHealth(double health);

    double molecraft$getMana();

    void molecraft$setMana(double mana);
}
