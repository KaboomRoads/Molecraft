package com.kaboomroads.molecraft.mixin;

import com.kaboomroads.molecraft.init.ModAttributes;
import com.kaboomroads.molecraft.mixinimpl.MolecraftLivingEntity;
import com.kaboomroads.molecraft.util.MolecraftUtil;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Unique
    private int regenTick = 0;

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;tick(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private void noFood(FoodData instance, ServerPlayer player, Operation<Void> original) {
        regenTick++;
        if (regenTick > 40) {
            regenTick = 0;
            MolecraftLivingEntity krtek = (MolecraftLivingEntity) this;
            krtek.molecraft$setHealth(krtek.molecraft$getHealth() + getAttributeValue(ModAttributes.MAX_HEALTH) * getAttributeValue(ModAttributes.HEALTH_REGEN) * 0.01);
        }
    }

    @WrapOperation(method = "addAdditionalSaveData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
    private void noFood(FoodData instance, CompoundTag compoundTag, Operation<Void> original) {
    }

    @WrapMethod(method = "actuallyHurt")
    private void wrap_actuallyHurt(ServerLevel level, DamageSource damageSource, float amount, Operation<Void> original) {
        MolecraftUtil.dealDamage(this, level, damageSource, amount);
    }
}
