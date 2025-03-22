package com.kaboomroads.molecraft.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum ItemType {
    NONE(Component.empty()),
    SWORD(Component.literal("SWORD"));

    public final Component name;

    ItemType(MutableComponent name) {
        this.name = name.withStyle(ChatFormatting.BOLD);
    }
}
