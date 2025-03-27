package com.kaboomroads.molecraft.item;

import java.util.HashMap;
import java.util.Objects;

public class StatInstance {
    public StatType type;
    public double cachedValue;
    public final HashMap<String, Double> modifiers = new HashMap<>();

    public StatInstance(StatType type) {
        this.type = type;
        addUpModifiers();
    }

    public void addUpModifiers() {
        cachedValue = type.defaultValue;
        for (Double value : modifiers.values()) cachedValue += value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatInstance that = (StatInstance) o;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(type);
    }
}
