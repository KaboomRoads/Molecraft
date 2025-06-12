package com.kaboomroads.molecraft.mixin;

import com.kaboomroads.molecraft.entity.custom.MolecraftCreaking;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Creaking.class)
public abstract class CreakingMixin {
    @Shadow
    public abstract void activate(Player player);

    @WrapOperation(method = "checkCanMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/creaking/Creaking;isLookingAtMe(Lnet/minecraft/world/entity/LivingEntity;DZZ[D)Z", ordinal = 0))
    private boolean wrapIsLookingAtMe(Creaking instance, LivingEntity livingEntity, double v, boolean b1, boolean b2, double[] doubles, Operation<Boolean> original, @Local(ordinal = 0) boolean bl) {
        if (instance instanceof MolecraftCreaking && livingEntity instanceof Player player) {
            if (!bl && livingEntity.distanceToSqr((Creaking) (Object) this) < 144.0) {
                activate(player);
                return false;
            }
        }
        return original.call(instance, livingEntity, v, b1, b2, doubles);
    }
}
