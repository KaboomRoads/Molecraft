package com.kaboomroads.molecraft.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Blocks.class)
public abstract class BlocksMixin {
    @WrapOperation(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;noCollission()Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;", ordinal = 0), slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=structure_void")))
    private static BlockBehaviour.Properties collisionAndSuffocation(BlockBehaviour.Properties instance, Operation<BlockBehaviour.Properties> original) {
        return instance.isSuffocating(Blocks::never);
    }
}
