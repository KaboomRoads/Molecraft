package com.kaboomroads.molecraft.entity;

import com.kaboomroads.molecraft.item.Rarity;
import com.kaboomroads.molecraft.mixinimpl.ModEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.function.Function;

public final class MolecraftEntity {
    public final String id;
    public final Component name;
    public final Function<Level, Entity> createEntity;
    public final Rarity rarity;
    public final boolean customNameTag;
    public final int xp;

    public MolecraftEntity(String id, Component name, Function<Level, Entity> createEntity, Rarity rarity, boolean customNameTag, int xp) {
        this.id = id;
        this.name = name;
        this.createEntity = createEntity;
        this.rarity = rarity;
        this.customNameTag = customNameTag;
        this.xp = xp;
    }

    public Entity construct(Level level) {
        Entity entity = createEntity.apply(level);
        ((ModEntity) entity).molecraft$setName(name.copy().withStyle(rarity.name.getStyle()));
        ((ModEntity) entity).molecraft$setId(id);
        entity.setCustomName(((ModEntity) entity).molecraft$getName());
        return entity;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (MolecraftEntity) obj;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
