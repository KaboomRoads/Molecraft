package com.kaboomroads.molecraft.entity;

import java.util.HashMap;
import java.util.Objects;
import java.util.TreeMap;

public class StatInstance {
    public StatType type;
    public double baseValue;
    public double cachedValue;
    private final HashMap<String, StatModifier> modifiersById = new HashMap<>();
    private final TreeMap<StatModifier.Operation, HashMap<String, StatModifier>> modifiersByOperation = new TreeMap<>();

    public StatInstance(StatType type, double baseValue) {
        this.type = type;
        this.baseValue = baseValue;
        addUpModifiers();
    }

    public void addUpModifiers() {
        cachedValue = baseValue;
        for (HashMap<String, StatModifier> map : modifiersByOperation.values())
            for (StatModifier modifier : map.values())
                modifier.modify(this);
    }

    public void putModifier(String id, double value, StatModifier.Operation operation) {
        StatModifier modifier = new StatModifier(id, value, operation);
        modifiersById.put(id, modifier);
        modifiersByOperation.computeIfAbsent(operation, m -> new HashMap<>()).put(id, modifier);
    }

    public void removeModifier(String id) {
        StatModifier modifier = modifiersById.remove(id);
        if (modifier != null) modifiersByOperation.get(modifier.operation()).remove(id);
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
