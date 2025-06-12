package com.kaboomroads.molecraft.item.ability.core;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class When implements Comparable<When> {
    public final String id;
    public final Component name;

    private When(String id, Component name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        When when = (When) o;
        return Objects.equals(id, when.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public int compareTo(@NotNull When when) {
        return id.compareTo(when.id);
    }

    public static final class PostHit extends When {
        PostHit() {
            super("post_hit", Component.literal("Post-hit"));
        }
    }
}
