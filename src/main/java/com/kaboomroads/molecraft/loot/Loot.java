package com.kaboomroads.molecraft.loot;

import com.kaboomroads.molecraft.item.MolecraftData;
import net.minecraft.core.HolderLookup;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class Loot {
    public final HashMap<String, TreeMap<Float, HashSet<MolecraftData>>> rolls;
    public final boolean save;

    public Loot(HashMap<String, TreeMap<Float, HashSet<MolecraftData>>> rolls, boolean save) {
        this.rolls = rolls;
        this.save = save;
    }

    public Loot() {
        this(new HashMap<>(), true);
    }

    public Iterator<ItemStack> collect(RandomSource random, HolderLookup.Provider lookupProvider) {
        return new LootIterator(rolls, random, lookupProvider);
    }

    public static class LootIterator implements Iterator<ItemStack> {
        private final Iterator<Map.Entry<String, TreeMap<Float, HashSet<MolecraftData>>>> lootIterator;
        private Iterator<Map.Entry<Float, HashSet<MolecraftData>>> rollIterator;
        private Iterator<MolecraftData> dropIterator;
        private ItemStack nextItem;
        private Map.Entry<Float, HashSet<MolecraftData>> currentLootEntry;
        private final RandomSource random;
        private final HolderLookup.Provider lookupProvider;

        public LootIterator(HashMap<String, TreeMap<Float, HashSet<MolecraftData>>> loot, RandomSource random, HolderLookup.Provider lookupProvider) {
            this.lootIterator = loot.entrySet().iterator();
            this.random = random;
            this.lookupProvider = lookupProvider;
            nextRoll();
            nextDrop();
        }

        private void nextRoll() {
            if (lootIterator.hasNext()) {
                Map.Entry<String, TreeMap<Float, HashSet<MolecraftData>>> entry = lootIterator.next();
                rollIterator = entry.getValue().entrySet().iterator();
                nextEntry();
            } else {
                rollIterator = null;
                dropIterator = null;
                currentLootEntry = null;
            }
        }

        private void nextEntry() {
            if (rollIterator != null && rollIterator.hasNext()) {
                currentLootEntry = rollIterator.next();
                dropIterator = currentLootEntry.getValue().iterator();
            } else {
                dropIterator = null;
                currentLootEntry = null;
                nextRoll();
            }
        }

        private void nextDrop() {
            nextItem = null;
            while (nextItem == null) {
                if (dropIterator != null && dropIterator.hasNext()) {
                    MolecraftData candidate = dropIterator.next();
                    float chance = currentLootEntry.getKey();
                    float extraChance = chance;
                    float randomFloat = random.nextFloat();
                    int count = (int) extraChance;
                    extraChance = extraChance - count;
                    if (randomFloat < extraChance) count++;
                    if (randomFloat < chance) nextItem = candidate.getItem().construct(count, lookupProvider);
                } else if (rollIterator != null) {
                    nextEntry();
                    if (dropIterator == null) break;
                } else break;
            }
        }

        @Override
        public boolean hasNext() {
            return nextItem != null;
        }

        @Override
        public ItemStack next() {
            ItemStack result = nextItem;
            nextDrop();
            return result;
        }
    }
}
