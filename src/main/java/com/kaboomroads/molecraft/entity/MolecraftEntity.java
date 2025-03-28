package com.kaboomroads.molecraft.entity;

import com.kaboomroads.molecraft.mixinimpl.ModEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.function.Function;

public record MolecraftEntity(String id, Component name, Function<Level, Entity> createEntity, boolean customNameTag) {
    public Entity construct(Level level) {
        Entity entity = createEntity.apply(level);
        ((ModEntity) entity).molecraft$setName(name);
        ((ModEntity) entity).molecraft$setId(id);
        return entity;
    }
}
