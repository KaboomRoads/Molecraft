package com.kaboomroads.molecraft.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum Rarity {
    COMMON(Component.literal("COMMON").withStyle(ChatFormatting.WHITE)),
    UNCOMMON(Component.literal("UNCOMMON").withStyle(ChatFormatting.GREEN)),
    RARE(Component.literal("RARE").withStyle(ChatFormatting.BLUE)),
    EPIC(Component.literal("EPIC").withStyle(ChatFormatting.DARK_PURPLE)),
    LEGENDARY(Component.literal("LEGENDARY").withStyle(ChatFormatting.GOLD)),
    MYTHIC(Component.literal("MYTHIC").withStyle(ChatFormatting.LIGHT_PURPLE)),
    DIVINE(Component.literal("DIVINE").withStyle(ChatFormatting.AQUA)),
    ULTIMATE(Component.literal("ULTIMATE").withStyle(ChatFormatting.RED)),
    ADMIN(Component.literal("ADMIN").withStyle(ChatFormatting.DARK_RED));

    public final Component name;

    Rarity(MutableComponent name) {
        this.name = name.withStyle(ChatFormatting.BOLD);
    }
}
