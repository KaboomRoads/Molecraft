package com.kaboomroads.molecraft.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum ItemType {
    NONE(Component.empty()),
    SWORD(Component.literal("SWORD")),
    BOOTS(Component.literal("BOOTS")),
    BOW(Component.literal("BOW")),
    ;

    public final Component name;

    ItemType(MutableComponent name) {
        this.name = name;
    }
}
