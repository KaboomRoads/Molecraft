package com.kaboomroads.molecraft.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public interface PlayerEntity {
    Skin getSkin();

    GameProfile createGameProfile();

    static GameProfile initGameProfile(GameProfile gameProfile, Skin skin) {
        gameProfile.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));
        return gameProfile;
    }

    default boolean rotateBodyWithHead() {
        return true;
    }
}