package com.kaboomroads.molecraft.loot;

import com.kaboomroads.molecraft.item.MolecraftData;
import com.kaboomroads.molecraft.item.MolecraftItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.*;

public class LootManager {
    public final HashMap<String, Loot> lootMap;

    public LootManager(HashMap<String, Loot> lootMap) {
        this.lootMap = lootMap;
        lootMap.put("master_mode_floor_four", new Loot(new HashMap<>(Map.of(
                "roll", new TreeMap<>(Map.of(
                        0.5F, new HashSet<>(Set.of(
                                new MolecraftData(MolecraftItems.REN.id)
                        ))
                ))
        )), false));
    }

    public LootManager() {
        this(new HashMap<>());
    }

    public Loot get(String id) {
        return lootMap.get(id);
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        ListTag lootMapTag = new ListTag();
        for (Map.Entry<String, Loot> entry : lootMap.entrySet()) {
            String id = entry.getKey();
            Loot loot = entry.getValue();
            if (loot.save) {
                CompoundTag entryTag = new CompoundTag();
                ListTag lootTag = new ListTag();
                for (Map.Entry<String, TreeMap<Float, HashSet<MolecraftData>>> rollEntry : loot.rolls.entrySet()) {
                    CompoundTag rollTag = new CompoundTag();
                    String rollId = rollEntry.getKey();
                    TreeMap<Float, HashSet<MolecraftData>> rolls = rollEntry.getValue();
                    ListTag setTag = new ListTag();
                    for (Map.Entry<Float, HashSet<MolecraftData>> lootEntry : rolls.entrySet()) {
                        CompoundTag lootEntryTag = new CompoundTag();
                        float chance = lootEntry.getKey();
                        Set<MolecraftData> drops = lootEntry.getValue();
                        ListTag dropsTag = new ListTag();
                        for (MolecraftData data : drops) dropsTag.add(data.save());
                        lootEntryTag.putFloat("chance", chance);
                        lootEntryTag.put("drops", dropsTag);
                        setTag.add(lootEntryTag);
                    }
                    rollTag.putString("id", rollId);
                    rollTag.put("entries", setTag);
                    lootTag.add(rollTag);
                }
                entryTag.putString("id", id);
                entryTag.put("loot", lootTag);
                lootMapTag.add(entryTag);
            }
        }
        tag.put("loot_map", lootMapTag);
        return tag;
    }

    public static LootManager load(CompoundTag tag) {
        HashMap<String, Loot> lootMap = new HashMap<>();
        ListTag lootMapTag = tag.getList("loot_map", Tag.TAG_COMPOUND);
        for (Tag t1 : lootMapTag) {
            CompoundTag entryTag = (CompoundTag) t1;
            String id = entryTag.getString("id");
            ListTag lootTag = entryTag.getList("loot", Tag.TAG_COMPOUND);
            Loot loot = new Loot();
            for (Tag t2 : lootTag) {
                CompoundTag rollTag = (CompoundTag) t2;
                String rollId = rollTag.getString("id");
                ListTag setTag = rollTag.getList("entries", Tag.TAG_COMPOUND);
                TreeMap<Float, HashSet<MolecraftData>> rolls = new TreeMap<>();
                for (Tag t3 : setTag) {
                    CompoundTag lootEntryTag = (CompoundTag) t3;
                    float chance = lootEntryTag.getFloat("chance");
                    ListTag dropsTag = lootEntryTag.getList("drops", Tag.TAG_COMPOUND);
                    HashSet<MolecraftData> set = new HashSet<>();
                    for (Tag t4 : dropsTag) {
                        MolecraftData data = MolecraftData.parse((CompoundTag) t4);
                        if (data != null) set.add(data);
                    }
                    rolls.put(chance, set);
                }
                loot.rolls.put(rollId, rolls);
            }
            lootMap.put(id, loot);
        }
        return new LootManager(lootMap);
    }
}
