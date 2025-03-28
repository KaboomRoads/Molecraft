package com.kaboomroads.molecraft.mixin;

import com.kaboomroads.molecraft.item.MolecraftItem;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.Tag;
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

import java.util.Optional;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements DataComponentHolder {
    @Shadow
    public abstract Item getItem();

    @WrapOperation(method = "save(Lnet/minecraft/core/HolderLookup$Provider;Lnet/minecraft/nbt/Tag;)Lnet/minecraft/nbt/Tag;", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/Codec;encode(Ljava/lang/Object;Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;", remap = false))
    private DataResult<Tag> wrapEncode1(Codec<ItemStack> instance, Object o1, DynamicOps<ItemStack> dynamicOps, Object o2, Operation<DataResult<ItemStack>> original, @Local(ordinal = 0, argsOnly = true) HolderLookup.Provider levelRegistryAccess) {
        return MolecraftItem.encode((ItemStack) o1, levelRegistryAccess, (Tag) o2);
    }

    @WrapOperation(method = "save(Lnet/minecraft/core/HolderLookup$Provider;)Lnet/minecraft/nbt/Tag;", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/Codec;encodeStart(Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;", remap = false))
    private DataResult<Tag> wrapEncode2(Codec<ItemStack> instance, DynamicOps<ItemStack> dynamicOps, Object o, Operation<DataResult<ItemStack>> original, @Local(ordinal = 0, argsOnly = true) HolderLookup.Provider levelRegistryAccess) {
        return MolecraftItem.encodeStart((ItemStack) o, levelRegistryAccess);
    }

    @WrapMethod(method = "parse")
    private static Optional<ItemStack> wrapParse(HolderLookup.Provider lookupProvider, Tag tag, Operation<Optional<ItemStack>> original) {
        return MolecraftItem.parse(lookupProvider, tag);
    }

    @WrapMethod(method = "useOn")
    private InteractionResult wrap_useOn(UseOnContext context, Operation<InteractionResult> original) {
        Player player = context.getPlayer();
        if (player != null) {
            if (has(DataComponents.EQUIPPABLE) || (!has(DataComponents.CUSTOM_DATA) && player.getAbilities().instabuild)) return original.call(context);
            player.containerMenu.sendAllDataToRemote();
        }
        return InteractionResult.PASS;
    }

    @WrapMethod(method = "use")
    private InteractionResult wrap_use(Level level, Player player, InteractionHand hand, Operation<InteractionResult> original) {
        if (player != null) {
            if (has(DataComponents.EQUIPPABLE) || (!has(DataComponents.CUSTOM_DATA) && player.getAbilities().instabuild)) return original.call(level, player, hand);
            player.containerMenu.sendAllDataToRemote();
        }
        return InteractionResult.PASS;
    }
}
