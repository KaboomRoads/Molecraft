package com.kaboomroads.molecraft.mixin;

import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Inventory.class)
public abstract class InventoryMixin {
//    @WrapOperation(method = "save", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;save(Lnet/minecraft/core/HolderLookup$Provider;Lnet/minecraft/nbt/Tag;)Lnet/minecraft/nbt/Tag;"))
//    private Tag modifySave(ItemStack instance, HolderLookup.Provider levelRegistryAccess, Tag outputTag, Operation<Tag> original) {
//        return MolecraftItem.save(instance, levelRegistryAccess, outputTag);
//    }
//
//    @WrapOperation(method = "load", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;parse(Lnet/minecraft/core/HolderLookup$Provider;Lnet/minecraft/nbt/Tag;)Ljava/util/Optional;"))
//    private Optional<ItemStack> modifyLoad(HolderLookup.Provider lookupProvider, Tag tag, Operation<Optional<ItemStack>> original) {
//        return MolecraftItem.parse(lookupProvider, tag);
//    }
}
