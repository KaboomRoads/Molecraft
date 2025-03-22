package com.kaboomroads.molecraft.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {
    @ModifyReturnValue(method = "getDestroySpeed", at = @At("RETURN"))
    private float modifyDestroySpeed(float original) {
        return -1.0F;
    }
}
