package com.kaboomroads.molecraft.item.ability.core;

public abstract class What<W extends When, C extends WhenContext<W>> {
    public abstract void run(C context);
}
