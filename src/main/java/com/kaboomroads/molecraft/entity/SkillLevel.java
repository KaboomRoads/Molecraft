package com.kaboomroads.molecraft.entity;

import java.util.Objects;

public class SkillLevel {
    public int level;
    public int currentLevelXp;

    public SkillLevel(int level, int currentLevelXp) {
        this.level = level;
        this.currentLevelXp = currentLevelXp;
    }

    public SkillLevel(long combined) {
        this.level = (int) (combined & 0xFFFFFFFFL);
        this.currentLevelXp = (int) (combined >> 32);
    }

    public long toLong() {
        return (((long) currentLevelXp) << 32) | (level & 0xFFFFFFFFL);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkillLevel that = (SkillLevel) o;
        return level == that.level && currentLevelXp == that.currentLevelXp;
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, currentLevelXp);
    }
}
