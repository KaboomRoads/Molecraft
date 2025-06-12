package com.kaboomroads.molecraft.item;

import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class DataPropertyType<T> {
    public final String id;

    public DataPropertyType(String id) {
        this.id = id;
    }

    public Tag saveUnsafe(Object object) {
        return save((T) object);
    }

    public abstract Tag save(T property);

    @Nullable
    public abstract T load(Tag tag);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataPropertyType<?> that = (DataPropertyType<?>) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
