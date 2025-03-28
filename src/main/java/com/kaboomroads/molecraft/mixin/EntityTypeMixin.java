package com.kaboomroads.molecraft.mixin;

import com.kaboomroads.molecraft.entity.MolecraftEntities;
import com.kaboomroads.molecraft.entity.MolecraftEntity;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(EntityType.class)
public abstract class EntityTypeMixin<T extends Entity> {
    @Unique
    private static final ThreadLocal<String> MOLECRAFT_ID = new ThreadLocal<>();

    @Inject(method = "create(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/EntitySpawnReason;)Ljava/util/Optional;", at = @At("HEAD"))
    private static void preserveTaggedMolecraft(CompoundTag tag, Level level, EntitySpawnReason spawnReason, CallbackInfoReturnable<Optional<Entity>> cir) {
        if (tag.contains("MolecraftId", Tag.TAG_STRING)) {
            MOLECRAFT_ID.set(tag.getString("MolecraftId"));
        }
    }

    @WrapMethod(method = "create(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/EntitySpawnReason;)Lnet/minecraft/world/entity/Entity;")
    private T wrap_create(Level level, EntitySpawnReason spawnReason, Operation<T> original) {
        if (MOLECRAFT_ID.get() != null) {
            String molecraftId = MOLECRAFT_ID.get();
            MolecraftEntity molecraftEntity = MolecraftEntities.ENTITIES.get(molecraftId);
            return (T) molecraftEntity.construct(level);
        }
        return original.call(level, spawnReason);
    }

    @Inject(method = "create(Lnet/minecraft/nbt/CompoundTag;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/EntitySpawnReason;)Ljava/util/Optional;", remap = false, at = @At("TAIL"))
    private static void removeLocal(CompoundTag tag, Level level, EntitySpawnReason spawnReason, CallbackInfoReturnable<Optional<Entity>> cir) {
        MOLECRAFT_ID.remove();
    }
}
