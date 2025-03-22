package com.kaboomroads.molecraft.init;

import com.kaboomroads.molecraft.ModConstants;
import com.kaboomroads.molecraft.attribute.MolecraftAttribute;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.function.Function;

public class ModAttributes {
    public static final Holder<Attribute> DAMAGE = registerMinZero(
            "damage", 1.0, "💢", c -> c.withStyle(ChatFormatting.RED)
    );
    public static final Holder<Attribute> CRIT_DAMAGE = registerMinZero(
            "crit_damage", 1.0, "✳", c -> c.withStyle(ChatFormatting.BLUE)
    );
    public static final Holder<Attribute> CRIT_CHANCE = registerMinZero(
            "crit_chance", 1.0, "ψ", c -> c.withStyle(ChatFormatting.BLUE)
    );
    public static final Holder<Attribute> SPELL_DAMAGE = registerMinZero(
            "spell_damage", 1.0, "🌀", c -> c.withStyle(ChatFormatting.DARK_PURPLE)
    );
    public static final Holder<Attribute> MAX_MANA = registerMinZero(
            "max_mana", 100.0, "📖", c -> c.withStyle(ChatFormatting.AQUA)
    );
    public static final Holder<Attribute> MANA_REGEN = registerMinZero(
            "mana_regen", 5.0, "🌌", c -> c.withStyle(ChatFormatting.AQUA)
    );
    public static final Holder<Attribute> MAX_HEALTH = registerMinZero(
            "max_health", 10.0, "❤", c -> c.withStyle(ChatFormatting.RED)
    );
    public static final Holder<Attribute> HEALTH_REGEN = registerMinZero(
            "health_regen", 5.0, "💞", c -> c.withStyle(ChatFormatting.RED)
    );
    public static final Holder<Attribute> DEFENSE = registerMinZero(
            "defense", 0.0, "🔰", c -> c.withStyle(ChatFormatting.GREEN)
    );
    public static final Holder<Attribute> BREAKING_POWER = registerMinZero(
            "breaking_power", 0.0, "🔅", c -> c.withStyle(ChatFormatting.DARK_AQUA)
    );
    public static final Holder<Attribute> MINING_SPEED = registerMinZero(
            "mining_speed", 0.0, "⛏", c -> c.withStyle(ChatFormatting.GOLD)
    );
    public static final Holder<Attribute> MINING_FORTUNE = registerMinZero(
            "mining_fortune", 0.0, "🍀", c -> c.withStyle(ChatFormatting.GOLD)
    );

    private static int ordinals = 0;

    private static Holder<Attribute> registerMinZero(String name, double defaultValue, String icon, Function<MutableComponent, MutableComponent> stylize) {
        return register(
                name, new MolecraftAttribute("attribute." + ModConstants.MOD_ID + ".name." + name, defaultValue, 0.0, Double.MAX_VALUE, name, icon, stylize, ordinals++).setSyncable(true)
        );
    }

    private static Holder<Attribute> register(String name, Attribute attribute) {
        return Registry.registerForHolder(BuiltInRegistries.ATTRIBUTE, ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, name), attribute);
    }

    public static void init() {
    }
}
