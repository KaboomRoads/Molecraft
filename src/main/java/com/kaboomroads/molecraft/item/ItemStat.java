package com.kaboomroads.molecraft.item;

import com.kaboomroads.molecraft.entity.StatModifier;

public record ItemStat(double value, StatModifier.Operation operation) {
}
