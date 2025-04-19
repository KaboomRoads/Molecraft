package com.kaboomroads.molecraft.mixin;

import com.kaboomroads.molecraft.mixinimpl.ModPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public abstract class InventoryMixin {
    @Shadow
    @Final
    public Player player;

    @Inject(method = "add(ILnet/minecraft/world/item/ItemStack;)Z", at = @At("HEAD"), cancellable = true)
    private void coinAdd(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag tag = customData.getUnsafe();
            if (tag.contains("coins", Tag.TAG_LONG)) {
                long coins = tag.getLong("coins");
                ((ModPlayer) player).molecraft$setCoins(((ModPlayer) player).molecraft$getCoins() + coins);
                cir.setReturnValue(true);
            }
        }
    }
}
