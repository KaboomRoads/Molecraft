package com.kaboomroads.molecraft.mixin;

import com.kaboomroads.molecraft.mixinimpl.ModItemEntity;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements ModItemEntity {
    public ItemEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract void setTarget(@Nullable UUID target);

    @Shadow
    @Nullable
    public UUID target = null;

    @Shadow
    public abstract ItemStack getItem();

    @Shadow
    public abstract void setItem(ItemStack stack);

    @Unique
    private boolean telekinetic = false;

    @Override
    public boolean molecraft$getTelekinetic() {
        return telekinetic;
    }

    @Override
    public void molecraft$setTelekinetic(boolean telekinetic) {
        this.telekinetic = telekinetic;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void saveMolecraft(CompoundTag tag, CallbackInfo ci) {
        if (telekinetic) tag.putBoolean("telekinetic", true);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void loadMolecraft(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("telekinetic", CompoundTag.TAG_BYTE)) telekinetic = tag.getBoolean("telekinetic");
    }

    @Inject(method = "setThrower", at = @At("TAIL"))
    private void alwaysSetTarget(Entity thrower, CallbackInfo ci) {
        if (thrower instanceof Player) setTarget(thrower.getUUID());
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/item/ItemEntity;getX()D", ordinal = 0))
    private void tick(CallbackInfo ci) {
        if (telekinetic && target != null) {
            Player player = level().getPlayerByUUID(target);
            if (player != null) moveTo(player.position());
        }
    }

    @WrapMethod(method = "hurtServer")
    private boolean wrap_hurtServer(ServerLevel level, DamageSource damageSource, float amount, Operation<Boolean> original) {
        return false;
    }

    @WrapMethod(method = "merge(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;I)Lnet/minecraft/world/item/ItemStack;")
    private static ItemStack amountGlorg(ItemStack destinationStack, ItemStack originStack, int amount, Operation<ItemStack> original) {
        return original.call(destinationStack, originStack, Integer.MAX_VALUE);
    }

    @ModifyExpressionValue(method = "isMergable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getMaxStackSize()I"))
    private int maxStackSize_1(int original) {
        return Integer.MAX_VALUE;
    }

    @ModifyExpressionValue(method = "areMergable", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getMaxStackSize()I"))
    private static int maxStackSize_2(int original) {
        return Integer.MAX_VALUE;
    }

    @ModifyExpressionValue(method = "merge(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;I)Lnet/minecraft/world/item/ItemStack;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getMaxStackSize()I"))
    private static int maxStackSize_3(int original) {
        return Integer.MAX_VALUE;
    }
}
