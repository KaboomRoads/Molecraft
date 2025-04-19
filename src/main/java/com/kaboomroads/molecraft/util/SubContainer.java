package com.kaboomroads.molecraft.util;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public abstract class SubContainer implements Iterable<ItemStack> {
    public final Container container;

    public SubContainer(Container container) {
        this.container = container;
    }

    public abstract ItemStack get(int index);

    public abstract void set(int index, ItemStack itemStack);

    @Override
    public @NotNull Iterator<ItemStack> iterator() {
        return new Iterator<>() {
            private int index = 0;

            @Override
            public boolean hasNext() {
                int len = 22;
                return index < len - 1;
            }

            @Override
            public ItemStack next() {
                return get(index++);
            }
        };
    }
}
