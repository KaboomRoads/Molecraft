package com.kaboomroads.molecraft.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {
    @WrapMethod(method = "useItemOn")
    private InteractionResult wrapUseItemOn(ItemStack stack, Level level, Player player, InteractionHand hand, BlockHitResult hitResult, Operation<InteractionResult> original) {
        if (player.getAbilities().instabuild) return original.call(stack, level, player, hand, hitResult);
        else return InteractionResult.FAIL;
    }

    @WrapMethod(method = "useWithoutItem")
    private InteractionResult wrapUseWithoutItem(Level level, Player player, BlockHitResult hitResult, Operation<InteractionResult> original) {
        if (player.getAbilities().instabuild) return original.call(level, player, hitResult);
        else return InteractionResult.FAIL;
    }
}
