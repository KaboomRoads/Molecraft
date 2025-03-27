package com.kaboomroads.molecraft.item;

import net.minecraft.nbt.CompoundTag;

public record MolecraftData(String id) {
    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", id);
        return tag;
    }

    public static MolecraftData parse(CompoundTag tag) {
        if (!tag.contains("id")) return null;
        return new MolecraftData(tag.getString("id"));
    }

    public boolean exists() {
        return MolecraftItems.ITEMS.containsKey(id);
    }

    public MolecraftItem getItem() {
        return MolecraftItems.ITEMS.get(id);
    }
}
