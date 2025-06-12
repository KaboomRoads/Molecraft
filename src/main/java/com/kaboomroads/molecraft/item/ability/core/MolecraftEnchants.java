package com.kaboomroads.molecraft.item.ability.core;

import com.kaboomroads.molecraft.item.ability.Igniting;
import net.minecraft.network.chat.Component;

import java.util.HashMap;

public class MolecraftEnchants {
    public static HashMap<String, MolecraftEnchant<?, ?>> ENCHANTS = new HashMap<>();

    public static final MolecraftEnchant<?, ?> IGNITING = register(new Igniting("igniting", Component.literal("Igniting"), 3));

    public static <W extends When, C extends WhenContext<W>> MolecraftEnchant<W, C> register(MolecraftEnchant<W, C> ability) {
        ENCHANTS.put(ability.id, ability);
        return ability;
    }
}
