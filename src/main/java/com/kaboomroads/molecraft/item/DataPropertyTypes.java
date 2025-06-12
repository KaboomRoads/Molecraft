package com.kaboomroads.molecraft.item;

import com.kaboomroads.molecraft.item.ability.core.MolecraftEnchant;
import com.kaboomroads.molecraft.item.ability.core.MolecraftEnchants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class DataPropertyTypes {
    public static HashMap<String, DataPropertyType<?>> TYPES = new HashMap<>();

    public static final DataPropertyType<TreeMap<MolecraftEnchant<?, ?>, Integer>> ENCHANTS = register(new DataPropertyType<TreeMap<MolecraftEnchant<?, ?>, Integer>>("enchants") {
        @Override
        public Tag save(TreeMap<MolecraftEnchant<?, ?>, Integer> property) {
            CompoundTag enchantsTag = new CompoundTag();
            for (Map.Entry<MolecraftEnchant<?, ?>, Integer> entry : property.entrySet()) {
                MolecraftEnchant<?, ?> enchant = entry.getKey();
                Integer enchantLevel = entry.getValue();
                enchantsTag.putInt(enchant.id, enchantLevel);
            }
            return enchantsTag;
        }

        @Override
        public TreeMap<MolecraftEnchant<?, ?>, Integer> load(Tag tagIn) {
            if (tagIn instanceof CompoundTag tag) {
                TreeMap<MolecraftEnchant<?, ?>, Integer> enchants = new TreeMap<>();
                for (Map.Entry<String, Tag> entry : tag.tags.entrySet()) enchants.put(MolecraftEnchants.ENCHANTS.get(entry.getKey()), ((NumericTag) entry.getValue()).getAsInt());
                return enchants;
            }
            return null;
        }
    });

    public static <T> DataPropertyType<T> register(DataPropertyType<T> type) {
        TYPES.put(type.id, type);
        return type;
    }
}
