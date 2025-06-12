package com.kaboomroads.molecraft.item.ability.core;

import java.util.HashMap;

public class Abilities {
    public static HashMap<String, Ability<?, ?, ?>> ABILITIES = new HashMap<>();
    
    public static <W extends When, C extends WhenContext<W>, R extends What<W, C>> Ability<W, C, R> register(Ability<W, C, R> ability) {
        ABILITIES.put(ability.id, ability);
        return ability;
    }
}
