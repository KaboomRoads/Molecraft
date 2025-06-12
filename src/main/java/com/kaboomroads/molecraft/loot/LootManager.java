package com.kaboomroads.molecraft.loot;

import com.kaboomroads.molecraft.item.MolecraftData;
import com.kaboomroads.molecraft.item.MolecraftItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;

import java.util.HashMap;
import java.util.Map;

public class LootManager {
    public final HashMap<String, Loot> lootMap;

    public LootManager(HashMap<String, Loot> lootMap) {
        this.lootMap = lootMap;
        lootMap.put("master_mode_floor_four", Loot.builder(false)
                .startRoll()
                .add(1.0F, 1, new MolecraftData(MolecraftItems.CREATION.id))
                .add(1.0F, 1, new MolecraftData(MolecraftItems.DESTRUCTION.id))
                .endRoll("roll_1")
                .build()
        );
        lootMap.put("living_mud", Loot.builder(false)
                .startRoll()
                .add(0.5F, 1, new MolecraftData(MolecraftItems.MUD.id))
                .endRoll("roll")
                .build()
        );
        lootMap.put("red_shroomer", Loot.builder(false)
                .startRoll()
                .add(0.5F, 1, new MolecraftData(MolecraftItems.RED_MUSHROOM.id))
                .endRoll("roll")
                .build()
        );
        lootMap.put("brown_shroomer", Loot.builder(false)
                .startRoll()
                .add(0.5F, 1, new MolecraftData(MolecraftItems.BROWN_MUSHROOM.id))
                .endRoll("roll")
                .build()
        );
        lootMap.put("creaking", Loot.builder(false)
                .startRoll()
                .add(0.25F, 1, new MolecraftData(MolecraftItems.RESIN.id))
                .endRoll("roll")
                .build()
        );
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
                for (Map.Entry<String, HashMap<Float, SimpleWeightedRandomList<MolecraftData>>> rollEntry : loot.rolls.entrySet()) {
                    CompoundTag rollTag = new CompoundTag();
                    String rollId = rollEntry.getKey();
                    HashMap<Float, SimpleWeightedRandomList<MolecraftData>> rolls = rollEntry.getValue();
                    ListTag setTag = new ListTag();
                    for (Map.Entry<Float, SimpleWeightedRandomList<MolecraftData>> lootEntry : rolls.entrySet()) {
                        CompoundTag lootEntryTag = new CompoundTag();
                        float chance = lootEntry.getKey();
                        SimpleWeightedRandomList<MolecraftData> drops = lootEntry.getValue();
                        ListTag dropsTag = new ListTag();
                        for (WeightedEntry.Wrapper<MolecraftData> wrapper : drops.unwrap()) {
                            CompoundTag wrapperTag = new CompoundTag();
                            wrapperTag.put("data", wrapper.data().save());
                            wrapperTag.putInt("weight", wrapper.weight().asInt());
                            dropsTag.add(wrapperTag);
                        }
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
                HashMap<Float, SimpleWeightedRandomList<MolecraftData>> rolls = new HashMap<>();
                for (Tag t3 : setTag) {
                    CompoundTag lootEntryTag = (CompoundTag) t3;
                    float chance = lootEntryTag.getFloat("chance");
                    ListTag dropsTag = lootEntryTag.getList("drops", Tag.TAG_COMPOUND);
                    SimpleWeightedRandomList.Builder<MolecraftData> builder = new SimpleWeightedRandomList.Builder<>();
                    for (Tag t4 : dropsTag) {
                        CompoundTag wrapperTag = (CompoundTag) t4;
                        MolecraftData data = MolecraftData.parse(wrapperTag.getCompound("data"));
                        if (data != null) builder.add(data, wrapperTag.getInt("weight"));
                    }
                    rolls.put(chance, builder.build());
                }
                loot.rolls.put(rollId, rolls);
            }
            lootMap.put(id, loot);
        }
        return new LootManager(lootMap);
    }
}
