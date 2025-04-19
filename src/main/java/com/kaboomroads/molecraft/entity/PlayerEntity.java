package com.kaboomroads.molecraft.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import java.util.UUID;

public interface PlayerEntity {
    Skin getSkin();

    default GameProfile createGameProfile(int id) {
        return PlayerEntity.initGameProfile(new GameProfile(UUID.randomUUID(), getInvisibleName(id)), getSkin());
    }

    static String getInvisibleName(int id) {
        return Integer.toHexString(id).replaceAll("(.)", "§$1");
    }

    static GameProfile initGameProfile(GameProfile gameProfile, Skin skin) {
        gameProfile.getProperties().put("textures", new Property("textures", skin.getValue(), skin.getSignature()));
        return gameProfile;
    }

    default boolean rotateBodyWithHead() {
        return true;
    }
}