package com.kaboomroads.molecraft.mixin;

import com.kaboomroads.molecraft.mixinimpl.ModEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin implements ModEntity {
    @Unique
    private Component molecraftName = null;
    @Unique
    private String molecraftId = null;

    @Inject(method = "saveWithoutId", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getCustomName()Lnet/minecraft/network/chat/Component;", ordinal = 0))
    private void saveMolecraft(CompoundTag tag, CallbackInfoReturnable<CompoundTag> cir) {
        if (molecraftId != null) tag.putString("MolecraftId", molecraftId);
    }

    @Inject(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;setRot(FF)V", ordinal = 0))
    private void loadMolecraft(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("MolecraftId", Tag.TAG_STRING)) molecraftId = tag.getString("MolecraftId");
    }

    @Override
    public Component molecraft$getName() {
        return molecraftName;
    }

    @Override
    public void molecraft$setName(Component name) {
        this.molecraftName = name;
    }

    @Override
    public String molecraft$getId() {
        return molecraftId;
    }

    @Override
    public void molecraft$setId(String name) {
        this.molecraftId = name;
    }
}
