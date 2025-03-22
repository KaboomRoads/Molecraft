package com.kaboomroads.molecraft.attribute;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

import java.util.function.Function;

public class MolecraftAttribute extends RangedAttribute {
    public final String name;
    public final String icon;
    public final Function<MutableComponent, MutableComponent> stylize;
    public final int ordinal;

    public MolecraftAttribute(String descriptionId, double defaultValue, double min, double max, String name, String icon, Function<MutableComponent, MutableComponent> stylize, int ordinal) {
        super(descriptionId, defaultValue, min, max);
        this.name = name;
        this.icon = icon;
        this.stylize = stylize;
        this.ordinal = ordinal;
    }

    public MutableComponent format(MutableComponent in) {
        return stylize.apply(in.append(icon));
    }
}
