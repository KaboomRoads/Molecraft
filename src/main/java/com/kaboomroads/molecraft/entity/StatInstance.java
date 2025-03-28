package com.kaboomroads.molecraft.entity;

import java.util.HashMap;
import java.util.Objects;

public class StatInstance {
    public StatType type;
    public double baseValue;
    public double cachedValue;
    public final HashMap<String, Double> modifiers = new HashMap<>();

    public StatInstance(StatType type, double baseValue) {
        this.type = type;
        this.baseValue = baseValue;
        addUpModifiers();
    }

    public void addUpModifiers() {
        cachedValue = baseValue;
        for (Double value : modifiers.values()) cachedValue += value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatInstance that = (StatInstance) o;
        return Double.compare(baseValue, that.baseValue) == 0 && Double.compare(cachedValue, that.cachedValue) == 0 && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, baseValue, cachedValue);
    }
}
