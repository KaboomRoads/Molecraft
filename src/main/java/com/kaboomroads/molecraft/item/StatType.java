package com.kaboomroads.molecraft.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.Function;

public enum StatType {
    DAMAGE("damage", "Damage", 5.0, "💢", c -> c.withStyle(ChatFormatting.RED)),
    CRIT_DAMAGE("crit_damage", "Crit Damage", 1.0, "✳", c -> c.withStyle(ChatFormatting.BLUE)),
    CRIT_CHANCE("crit_chance", "Crit Chance", 1.0, "ψ", c -> c.withStyle(ChatFormatting.BLUE)),
    SPELL_DAMAGE("spell_damage", "Spell Damage", 1.0, "🌀", c -> c.withStyle(ChatFormatting.DARK_PURPLE)),
    MAX_MANA("max_mana", "Max Mana", 100.0, "📖", c -> c.withStyle(ChatFormatting.AQUA)),
    MANA_REGEN("mana_regen", "Mana Regen", 5.0, "🌌", c -> c.withStyle(ChatFormatting.AQUA)),
    MAX_HEALTH("max_health", "Max Health", 100.0, "❤", c -> c.withStyle(ChatFormatting.RED)),
    HEALTH_REGEN("health_regen", "Health Regen", 5.0, "💞", c -> c.withStyle(ChatFormatting.RED)),
    DEFENSE("defense", "Defense", 0.0, "🔰", c -> c.withStyle(ChatFormatting.GREEN)),
    BREAKING_POWER("breaking_power", "Breaking Power", 0.0, "🔅", c -> c.withStyle(ChatFormatting.DARK_AQUA)),
    MINING_STRENGTH("mining_strength", "Mining Strength", 0.0, "⛏", c -> c.withStyle(ChatFormatting.GOLD)),
    BRILLIANCE("brilliance", "Brilliance", 0.0, "🍀", c -> c.withStyle(ChatFormatting.GOLD));

    public final String id;
    public final Component name;
    public final double defaultValue;
    public final String icon;
    public final Function<MutableComponent, MutableComponent> stylize;

    StatType(String id, Component name, double defaultValue, String icon, Function<MutableComponent, MutableComponent> stylize) {
        this.id = id;
        this.name = name;
        this.defaultValue = defaultValue;
        this.icon = icon;
        this.stylize = stylize;
    }

    StatType(String id, String name, double defaultValue, String icon, Function<MutableComponent, MutableComponent> stylize) {
        this(id, Component.literal(name).withStyle(ChatFormatting.GRAY), defaultValue, icon, stylize);
    }

    public MutableComponent format(MutableComponent in) {
        return stylize.apply(in.append(icon));
    }
}
