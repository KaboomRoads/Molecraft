package com.kaboomroads.molecraft.entity;

import com.kaboomroads.molecraft.item.ability.core.*;
import net.minecraft.world.entity.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;

public class AbilitiesMap {
    public final HashMap<EquipmentSlot, TreeMap<When, TreeSet<AbilityInstance<?, ?, ?>>>> map = new HashMap<>();

    public static class AbilityInstance<W extends When, C extends WhenContext<W>, R extends What<W, C>> implements Comparable<AbilityInstance<W, C, R>> {
        public final Ability<W, C, R> ability;

        public AbilityInstance(Ability<W, C, R> ability) {
            this.ability = ability;
        }

        public void run(C context) {
            ability.what.run(context);
        }

        @Override
        public int compareTo(@NotNull AbilitiesMap.AbilityInstance abilityInstance) {
            return ability.compareTo(abilityInstance.ability);
        }
    }

    public static class EnchantInstance<W extends When, C extends WhenContext<W>, R extends EnchantWhat<W, C>> extends AbilityInstance<W, C, R> {
        public final int enchantLevel;

        public EnchantInstance(Ability<W, C, R> ability, int enchantLevel) {
            super(ability);
            this.enchantLevel = enchantLevel;
        }

        @Override
        public void run(C context) {
            ability.what.run(context, enchantLevel);
        }
    }

    public TreeSet<AbilityInstance<?, ?, ?>> get(When when) {
        for (TreeMap<When, TreeSet<AbilityInstance<?, ?, ?>>> whenMap : map.values()) {
            TreeSet<AbilityInstance<?, ?, ?>> enchantMap = whenMap.get(when);
            if (enchantMap != null) return enchantMap;
        }
        return null;
    }
}
