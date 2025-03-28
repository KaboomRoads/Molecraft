package com.kaboomroads.molecraft.mixinimpl;

import net.minecraft.network.chat.Component;

public interface ModEntity {
    Component molecraft$getName();

    void molecraft$setName(Component name);

    String molecraft$getId();

    void molecraft$setId(String name);
}
