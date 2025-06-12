package com.kaboomroads.molecraft.item;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record MolecraftData(
        String id,
        @NotNull DataPropertyMap propertyMap
) {
    public MolecraftData(String id) {
        this(id, new DataPropertyMap());
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("molecraft_id", id);
        propertyMap.save(tag);
        return tag;
    }

    public static MolecraftData parse(CompoundTag tag) {
        if (!tag.contains("molecraft_id")) return null;
        return new MolecraftData(tag.getString("molecraft_id"), DataPropertyMap.load(tag));
    }

    public ItemStack construct(int count, HolderLookup.Provider lookupProvider) {
        return getItem().construct(count, this, lookupProvider);
    }

    public boolean exists() {
        return MolecraftItems.ITEMS.containsKey(id);
    }

    public MolecraftItem getItem() {
        return MolecraftItems.ITEMS.get(id);
    }

    public boolean is(MolecraftItem item) {
        return Objects.equals(id, item.id);
    }

    public boolean is(MolecraftData data) {
        return Objects.equals(id, data.id);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MolecraftData that = (MolecraftData) o;
        return Objects.equals(id, that.id) && Objects.equals(propertyMap, that.propertyMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, propertyMap);
    }
}
