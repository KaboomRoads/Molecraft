package com.kaboomroads.molecraft.item.ability.core;

public abstract class EnchantWhat<W extends When, C extends WhenContext<W>> extends What<W, C> {
    @Override
    public void run(C context) {
        run(context, 1);
    }

    public abstract void run(C context, int enchantLevel);
}
