package com.kaboomroads.molecraft.item.ability.core;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Ability<W extends When, C extends WhenContext<W>, R extends What<W, C>> implements Comparable<Ability<?, ?, ?>> {
    public final String id;
    public final Component name;
    public final W when;
    public final R what;

    public Ability(String id, Component name, W when, R what) {
        this.id = id;
        this.name = name;
        this.when = when;
        this.what = what;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Ability<?, ?, ?> ability = (Ability<?, ?, ?>) object;
        return Objects.equals(id, ability.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public int compareTo(@NotNull Ability<?, ?, ?> ability) {
        return id.compareTo(ability.id);
    }
}
