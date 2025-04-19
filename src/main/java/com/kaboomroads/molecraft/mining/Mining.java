package com.kaboomroads.molecraft.mining;

import com.kaboomroads.molecraft.util.MiningPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;

public class Mining {
    public final HashMap<String, MiningArea> areas;
    public final HashMap<String, HashMap<Block, BlockType>> presets;
    public final Octree areaOctree = new Octree();

    public final HashMap<BlockPos, MiningPlayer> currentlyMining = new HashMap<>();

    public Mining(HashMap<String, MiningArea> areas, HashMap<String, HashMap<Block, BlockType>> presets) {
        this.areas = areas;
        this.presets = presets;
    }

    public Mining() {
        this(new HashMap<>(), new HashMap<>());
    }

    public void buildOctree() {
        areaOctree.build(areas.values());
    }

    public BlockType getBlockType(MiningArea area, Block block) {
        if (area == null) return null;
        BlockType blockType = area.types.get(block);
        if (blockType != null) return blockType;
        else if (area.presets != null)
            for (String presetName : area.presets) {
                HashMap<Block, BlockType> preset = presets.get(presetName);
                if (preset != null) {
                    BlockType type = preset.get(block);
                    if (type != null) return type;
                }
            }
        return null;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        ListTag areasTag = new ListTag();
        for (Map.Entry<String, MiningArea> entry : areas.entrySet()) {
            String id = entry.getKey();
            MiningArea area = entry.getValue();
            CompoundTag entryTag = new CompoundTag();
            entryTag.putString("id", id);
            entryTag.put("area", area.save());
            areasTag.add(entryTag);
        }
        tag.put("areas", areasTag);
        ListTag presetsTag = new ListTag();
        for (Map.Entry<String, HashMap<Block, BlockType>> entry : presets.entrySet()) {
            String id = entry.getKey();
            HashMap<Block, BlockType> types = entry.getValue();
            CompoundTag entryTag = new CompoundTag();
            entryTag.putString("id", id);
            entryTag.put("types", MiningArea.saveTypesMap(types));
            presetsTag.add(entryTag);
        }
        tag.put("presets", presetsTag);
        return tag;
    }

    public static Mining load(CompoundTag tag, HolderGetter<Block> blockLookup, HolderGetter<SoundEvent> soundEventLookup) {
        ListTag areasTag = tag.getList("areas", ListTag.TAG_COMPOUND);
        HashMap<String, MiningArea> loadedAreas = new HashMap<>(areasTag.size());
        for (int i = 0; i < areasTag.size(); i++) {
            CompoundTag entryTag = areasTag.getCompound(i);
            String id = entryTag.getString("id");
            MiningArea area = MiningArea.load(entryTag.getCompound("area"), blockLookup, soundEventLookup);
            loadedAreas.put(id, area);
        }
        ListTag presetsTag = tag.getList("presets", ListTag.TAG_COMPOUND);
        HashMap<String, HashMap<Block, BlockType>> loadedPresets = new HashMap<>(presetsTag.size());
        for (int i = 0; i < presetsTag.size(); i++) {
            CompoundTag entryTag = presetsTag.getCompound(i);
            String id = entryTag.getString("id");
            HashMap<Block, BlockType> types = MiningArea.loadTypesMap(entryTag.getList("types", Tag.TAG_COMPOUND), blockLookup, soundEventLookup);
            loadedPresets.put(id, types);
        }
        Mining mining = new Mining(loadedAreas, loadedPresets);
        mining.areaOctree.build(loadedAreas.values());
        return mining;
    }
}
