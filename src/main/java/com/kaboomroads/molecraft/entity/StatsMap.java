package com.kaboomroads.molecraft.entity;

import java.util.TreeMap;

public class StatsMap {
    public boolean hits = false;
    public final TreeMap<StatType, StatInstance> map;

    public StatsMap(TreeMap<StatType, StatInstance> map) {
        this.map = map;
    }

    public StatInstance get(StatType statType) {
        return map.get(statType);
    }

    public void setBase(StatType statType, double baseValue) {
        map.put(statType, new StatInstance(statType, baseValue));
    }

    public static class Builder {
        private final TreeMap<StatType, StatInstance> map = new TreeMap<>();

        public Builder stat(StatType statType, double baseValue) {
            map.put(statType, new StatInstance(statType, baseValue));
            return this;
        }

        public StatsMap build() {
            return new StatsMap(map);
        }
    }
}
