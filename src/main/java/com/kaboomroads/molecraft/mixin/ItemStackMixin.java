package com.kaboomroads.molecraft.mixin;

import com.kaboomroads.molecraft.item.MolecraftItem;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements DataComponentHolder {
    @Shadow
    public abstract Item getItem();

    @ModifyExpressionValue(method = "save(Lnet/minecraft/core/HolderLookup$Provider;Lnet/minecraft/nbt/Tag;)Lnet/minecraft/nbt/Tag;", at = @At(value = "FIELD", target = "Lnet/minecraft/world/item/ItemStack;CODEC:Lcom/mojang/serialization/Codec;"))
    private Codec<ItemStack> modifyCodec1(Codec<ItemStack> original) {
        return MolecraftItem.CODEC;
    }

    @ModifyExpressionValue(method = "save(Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/nbt/Tag;", at = @At(value = "FIELD", target = "Lnet/minecraft/world/item/ItemStack;CODEC:Lcom/mojang/serialization/Codec;"))
    private Codec<ItemStack> modifyCodec2(Codec<ItemStack> original) {
        return MolecraftItem.CODEC;
    }

    @ModifyExpressionValue(method = "parse", at = @At(value = "FIELD", target = "Lnet/minecraft/world/item/ItemStack;CODEC:Lcom/mojang/serialization/Codec;"))
    private static Codec<ItemStack> modifyCodec3(Codec<ItemStack> original) {
        return MolecraftItem.CODEC;
    }

    @WrapMethod(method = "useOn")
    private InteractionResult wrap_useOn(UseOnContext context, Operation<InteractionResult> original) {
        Player player = context.getPlayer();
        if (player != null) {
            if (has(DataComponents.EQUIPPABLE) || (has(DataComponents.CUSTOM_DATA) && player.getAbilities().instabuild)) return original.call(context);
            player.containerMenu.sendAllDataToRemote();
        }
        return InteractionResult.PASS;
    }

    @WrapMethod(method = "use")
    private InteractionResult wrap_use(Level level, Player player, InteractionHand hand, Operation<InteractionResult> original) {
        if (player != null) {
            if (has(DataComponents.EQUIPPABLE) || (has(DataComponents.CUSTOM_DATA) && player.getAbilities().instabuild)) return original.call(level, player, hand);
            player.containerMenu.sendAllDataToRemote();
        }
        return InteractionResult.PASS;
    }
}
