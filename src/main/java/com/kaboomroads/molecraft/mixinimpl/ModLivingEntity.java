package com.kaboomroads.molecraft.mixinimpl;

import com.kaboomroads.molecraft.entity.AbilitiesMap;
import com.kaboomroads.molecraft.entity.StatsMap;

public interface ModLivingEntity extends ModEntity {
    StatsMap molecraft$getStats();
    
    AbilitiesMap molecraft$getEnchants();

    StatsMap.Builder molecraft$initStats();

    double molecraft$getHealth();

    void molecraft$setHealth(double health);

    void molecraft$makeStuck(int ticks);
}
