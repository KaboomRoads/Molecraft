package com.kaboomroads.molecraft.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ExtraCodecs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(DataComponents.class)
public abstract class DataComponentsMixin {
    @ModifyExpressionValue(method = "method_58570", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ExtraCodecs;intRange(II)Lcom/mojang/serialization/Codec;", ordinal = 0))
    private static Codec<Integer> un99ify(Codec<Integer> original) {
        return ExtraCodecs.POSITIVE_INT;
    }
}
