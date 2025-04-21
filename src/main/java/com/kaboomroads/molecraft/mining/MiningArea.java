package com.kaboomroads.molecraft.mining;

import com.kaboomroads.molecraft.util.SoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MiningArea {
    public final HashMap<Block, BlockType> types;
    public HashSet<String> presets = null;
    public IAABB bounds;

    public MiningArea(HashMap<Block, BlockType> types, IAABB bounds) {
        this.types = types;
        this.bounds = bounds;
    }

    @Override
    public String toString() {
        return "MiningArea{" +
                "types=" + types +
                ", bounds=" + bounds +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MiningArea area = (MiningArea) o;
        return Objects.equals(types, area.types) && Objects.equals(bounds, area.bounds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(types, bounds);
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        ListTag typesTag = saveTypesMap(types);
        tag.put("types", typesTag);
        if (presets != null) {
            ListTag presetsTag = new ListTag();
            for (String preset : presets) presetsTag.add(StringTag.valueOf(preset));
            tag.put("presets", presetsTag);
        }
        tag.putIntArray("bounds", new int[]{bounds.minX, bounds.minY, bounds.minZ, bounds.maxX, bounds.maxY, bounds.maxZ});
        return tag;
    }

    @NotNull
    public static ListTag saveTypesMap(Map<Block, BlockType> types) {
        ListTag typesTag = new ListTag();
        for (Map.Entry<Block, BlockType> entry : types.entrySet()) {
            Block block = entry.getKey();
            BlockType blockType = entry.getValue();
            CompoundTag entryTag = new CompoundTag();
            entryTag.putString("block", BuiltInRegistries.BLOCK.getKey(block).toString());
            entryTag.putInt("breaking_power", blockType.breakingPower());
            entryTag.putDouble("block_health", blockType.blockHealth());
            entryTag.putInt("resonant_frequency", blockType.resonantFrequency());
            SoundInstance soundInstance = blockType.breakSound();
            entryTag.putString("break_sound", soundInstance.sound().getRegisteredName());
            entryTag.putFloat("break_sound_volume", soundInstance.volume());
            entryTag.putFloat("break_sound_pitch", soundInstance.pitch());
            entryTag.putString("loot", blockType.loot());
            typesTag.add(entryTag);
        }
        return typesTag;
    }

    public static MiningArea load(CompoundTag tag, HolderGetter<Block> blockLookup, HolderGetter<SoundEvent> soundEventLookup) {
        ListTag typesTag = tag.getList("types", Tag.TAG_COMPOUND);
        HashMap<Block, BlockType> loadedTypes = loadTypesMap(typesTag, blockLookup, soundEventLookup);
        int[] array = tag.getIntArray("bounds");
        IAABB loadedBounds = new IAABB(array[0], array[1], array[2], array[3], array[4], array[5]);
        MiningArea miningArea = new MiningArea(loadedTypes, loadedBounds);
        if (tag.contains("presets", Tag.TAG_LIST)) {
            ListTag presetsTag = tag.getList("presets", Tag.TAG_STRING);
            HashSet<String> loadedPresets = new HashSet<>(presetsTag.size());
            for (Tag preset : presetsTag) loadedPresets.add(preset.getAsString());
            miningArea.presets = loadedPresets;
        }
        return miningArea;
    }

    @NotNull
    public static HashMap<Block, BlockType> loadTypesMap(ListTag typesTag, HolderGetter<Block> blockLookup, HolderGetter<SoundEvent> soundEventLookup) {
        HashMap<Block, BlockType> loadedTypes = new HashMap<>(typesTag.size());
        for (int i = 0; i < typesTag.size(); i++) {
            CompoundTag entryTag = typesTag.getCompound(i);
            ResourceLocation resourceLocation = ResourceLocation.parse(entryTag.getString("block"));
            Optional<Holder.Reference<Block>> optional = blockLookup.get(ResourceKey.create(Registries.BLOCK, resourceLocation));
            loadedTypes.put(optional.get().value(), new BlockType(entryTag.getInt("breaking_power"), entryTag.getDouble("block_health"), entryTag.getInt("resonant_frequency"), new SoundInstance(soundEventLookup.getOrThrow(ResourceKey.create(Registries.SOUND_EVENT, ResourceLocation.parse(entryTag.getString("break_sound")))), entryTag.getFloat("break_sound_volume"), entryTag.getFloat("break_sound_pitch")), entryTag.getString("loot")));
        }
        return loadedTypes;
    }
}
