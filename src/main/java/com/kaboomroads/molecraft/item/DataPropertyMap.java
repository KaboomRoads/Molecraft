package com.kaboomroads.molecraft.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.HashMap;
import java.util.Map;

public class DataPropertyMap {
    public HashMap<DataPropertyType<?>, Object> map = new HashMap<>();

    public <T> T get(DataPropertyType<T> type) {
        return (T) map.get(type);
    }

    public <T> void set(DataPropertyType<T> type, T value) {
        map.put(type, value);
    }

    public void save(CompoundTag tag) {
        for (Map.Entry<DataPropertyType<?>, Object> entry : map.entrySet()) {
            DataPropertyType<?> type = entry.getKey();
            tag.put(type.id, type.saveUnsafe(entry.getValue()));
        }
    }

    public static DataPropertyMap load(CompoundTag tag) {
        DataPropertyMap propertyMap = new DataPropertyMap();
        for (Map.Entry<String, Tag> entry : tag.tags.entrySet()) {
            String key = entry.getKey();
            Tag value = entry.getValue();
            DataPropertyType<?> type = DataPropertyTypes.TYPES.get(key);
            if (type != null) {
                Object object = type.load(value);
                if (object != null) propertyMap.map.put(type, object);
            }
        }
        return propertyMap;
    }
}
