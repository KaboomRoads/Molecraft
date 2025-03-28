package com.kaboomroads.molecraft.entity;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.Function;

public enum StatType {
    DAMAGE("damage", "Damage", "💢", c -> c.withStyle(ChatFormatting.RED)),
    CRIT_DAMAGE("crit_damage", "Crit Damage", "✳", c -> c.withStyle(ChatFormatting.BLUE)),
    CRIT_CHANCE("crit_chance", "Crit Chance", "ψ", c -> c.withStyle(ChatFormatting.DARK_BLUE)),
    SPELL_DAMAGE("spell_damage", "Spell Damage", "🌀", c -> c.withStyle(ChatFormatting.DARK_PURPLE)),
    MAX_MANA("max_mana", "Max Mana", "📖", c -> c.withStyle(ChatFormatting.AQUA)),
    MANA_REGEN("mana_regen", "Mana Regen", "🌌", c -> c.withStyle(ChatFormatting.AQUA)),
    MAX_HEALTH("max_health", "Max Health", "❤", c -> c.withStyle(ChatFormatting.RED)),
    HEALTH_REGEN("health_regen", "Health Regen", "💞", c -> c.withStyle(ChatFormatting.RED)),
    DEFENSE("defense", "Defense", "🔰", c -> c.withStyle(ChatFormatting.GREEN)),
    BREAKING_POWER("breaking_power", "Breaking Power", "🔅", c -> c.withStyle(ChatFormatting.DARK_AQUA)),
    MINING_STRENGTH("mining_strength", "Mining Strength", "⛏", c -> c.withStyle(ChatFormatting.GOLD)),
    BRILLIANCE("brilliance", "Brilliance", "🍀", c -> c.withStyle(ChatFormatting.GOLD));

    public final String id;
    public final Component name;
    public final String icon;
    public final Function<MutableComponent, MutableComponent> stylize;

    StatType(String id, Component name, String icon, Function<MutableComponent, MutableComponent> stylize) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.stylize = stylize;
    }

    StatType(String id, String name, String icon, Function<MutableComponent, MutableComponent> stylize) {
        this(id, Component.literal(name).withStyle(ChatFormatting.GRAY), icon, stylize);
    }

    public MutableComponent format(MutableComponent in) {
        return stylize.apply(in.append(icon));
    }
}
