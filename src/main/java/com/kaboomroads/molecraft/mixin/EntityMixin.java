package com.kaboomroads.molecraft.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    protected abstract Component getTypeName();

    @WrapMethod(method = "setCustomName")
    private void wrap_setCustomName(Component name, Operation<Void> original) {
    }

    @WrapMethod(method = "getCustomName")
    private Component wrap_getCustomName(Operation<Component> original) {
        return getTypeName();
    }

    @WrapMethod(method = "hasCustomName")
    private boolean wrap_hasCustomName(Operation<Component> original) {
        return (Object) this instanceof LivingEntity;
    }

    @WrapMethod(method = "isCustomNameVisible")
    private boolean wrap_isCustomNameVisible(Operation<Component> original) {
        return (Object) this instanceof LivingEntity;
    }
}
