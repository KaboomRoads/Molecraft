package com.kaboomroads.molecraft.loot;

import com.kaboomroads.molecraft.item.MolecraftData;
import net.minecraft.core.HolderLookup;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class Loot {
    public final HashMap<String, HashMap<Float, SimpleWeightedRandomList<MolecraftData>>> rolls;
    public final boolean save;

    public Loot(HashMap<String, HashMap<Float, SimpleWeightedRandomList<MolecraftData>>> rolls, boolean save) {
        this.rolls = rolls;
        this.save = save;
    }

    public Loot() {
        this(new HashMap<>(), true);
    }

    public Iterator<ItemStack> collect(RandomSource random, float multiplier, HolderLookup.Provider lookupProvider) {
        return new LootIterator(rolls, random, multiplier, lookupProvider);
    }

    public static Builder builder(boolean save) {
        return new Builder(save);
    }

    public static class Builder {
        public boolean save;
        public final HashMap<String, HashMap<Float, SimpleWeightedRandomList<MolecraftData>>> rolls = new HashMap<>();
        public HashMap<Float, SimpleWeightedRandomList.Builder<MolecraftData>> currentRoll = null;

        public Builder(boolean save) {
            this.save = save;
        }

        public Builder startRoll() {
            currentRoll = new HashMap<>();
            return this;
        }

        public Builder add(float chance, int weight, MolecraftData molecraftData) {
            if (currentRoll == null) throw new IllegalStateException("Cannot add item drop without starting a roll");
            SimpleWeightedRandomList.Builder<MolecraftData> builder = currentRoll.get(chance);
            if (builder == null) {
                builder = new SimpleWeightedRandomList.Builder<>();
                builder.add(molecraftData, weight);
                currentRoll.put(chance, builder);
            } else builder.add(molecraftData, weight);
            return this;
        }

        public Builder endRoll(String name) {
            HashMap<Float, SimpleWeightedRandomList<MolecraftData>> roll = new HashMap<>();
            for (Map.Entry<Float, SimpleWeightedRandomList.Builder<MolecraftData>> entry : currentRoll.entrySet()) {
                float chance = entry.getKey();
                SimpleWeightedRandomList.Builder<MolecraftData> builder = entry.getValue();
                roll.put(chance, builder.build());
            }
            rolls.put(name, roll);
            currentRoll = null;
            return this;
        }

        public Loot build() {
            return new Loot(rolls, save);
        }
    }

    public static class LootIterator implements Iterator<ItemStack> {
        private final Iterator<Map.Entry<String, HashMap<Float, SimpleWeightedRandomList<MolecraftData>>>> lootIterator;
        private Iterator<Map.Entry<Float, SimpleWeightedRandomList<MolecraftData>>> rollIterator;
        private SimpleWeightedRandomList<MolecraftData> currentDrop;
        private ItemStack nextItem;
        private Map.Entry<Float, SimpleWeightedRandomList<MolecraftData>> currentLootEntry;
        private final RandomSource random;
        private final float multiplier;
        private final HolderLookup.Provider lookupProvider;

        public LootIterator(HashMap<String, HashMap<Float, SimpleWeightedRandomList<MolecraftData>>> rolls, RandomSource random, float multiplier, HolderLookup.Provider lookupProvider) {
            this.lootIterator = rolls.entrySet().iterator();
            this.random = random;
            this.multiplier = multiplier;
            this.lookupProvider = lookupProvider;
            nextRoll();
            nextDrop();
        }

        private void nextRoll() {
            if (lootIterator.hasNext()) {
                Map.Entry<String, HashMap<Float, SimpleWeightedRandomList<MolecraftData>>> entry = lootIterator.next();
                rollIterator = entry.getValue().entrySet().iterator();
                nextEntry();
            } else {
                rollIterator = null;
                currentDrop = null;
                currentLootEntry = null;
            }
        }

        private void nextEntry() {
            if (rollIterator != null && rollIterator.hasNext()) {
                currentLootEntry = rollIterator.next();
                currentDrop = currentLootEntry.getValue();
            } else {
                currentDrop = null;
                currentLootEntry = null;
                nextRoll();
            }
        }

        private void nextDrop() {
            nextItem = null;
            while (true) {
                if (currentDrop != null) {
                    float chance = currentLootEntry.getKey() * multiplier;
                    float extraChance = chance;
                    float randomFloat = random.nextFloat();
                    int count = (int) extraChance;
                    extraChance = extraChance - count;
                    if (randomFloat < extraChance) count++;
                    if (randomFloat < chance) {
                        Optional<MolecraftData> candidate = currentDrop.getRandomValue(random);
                        if (candidate.isPresent()) nextItem = candidate.get().construct(count, lookupProvider);
                    }
                    nextEntry();
                    break;
                } else if (rollIterator != null) {
                    nextEntry();
                    if (currentDrop == null) break;
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
