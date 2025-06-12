package com.kaboomroads.molecraft.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.StructureVoidBlock;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(StructureVoidBlock.class)
public abstract class StructureVoidBlockMixin {
    @Unique
    private static final VoxelShape SHAPE = Shapes.block();

    @ModifyReturnValue(method = "getShape", at = @At("RETURN"))
    private VoxelShape modifyShape(VoxelShape original, @Local(ordinal = 0, argsOnly = true) CollisionContext context) {
        return context instanceof EntityCollisionContext entityContext && entityContext.getEntity() instanceof Player ? Shapes.empty() : SHAPE;
    }
}
