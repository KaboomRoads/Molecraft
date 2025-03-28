package com.kaboomroads.molecraft.mixin;

import com.kaboomroads.molecraft.entity.StatType;
import com.kaboomroads.molecraft.entity.StatsMap;
import com.kaboomroads.molecraft.util.MolecraftUtil;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
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
public abstract class PlayerMixin extends LivingEntityMixin {
    public PlayerMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Unique
    private int regenTick = 0;

    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;tick(Lnet/minecraft/server/level/ServerPlayer;)V"))
    private void molecraftTick(FoodData instance, ServerPlayer player, Operation<Void> original) {
        regenTick++;
        if (regenTick > 40) {
            regenTick = 0;
            StatsMap stats = molecraft$getStats();
            molecraft$setHealth(molecraft$getHealth() + stats.get(StatType.MAX_HEALTH).cachedValue * stats.get(StatType.HEALTH_REGEN).cachedValue * 0.01);
        }
        if (tickCount % 5 == 0) {
            double health = molecraft$getHealth();
            StatsMap stats = molecraft$getStats();
            double maxHealth = stats.get(StatType.MAX_HEALTH).cachedValue;
            double defense = stats.get(StatType.DEFENSE).cachedValue;
            double maxMana = stats.get(StatType.MAX_MANA).cachedValue;
            double mana = molecraft$getMana();
            Component overlayMessageString = StatType.MAX_HEALTH.format(Component.literal(((int) health) + "/" + ((int) maxHealth))).append("    ").append(StatType.DEFENSE.format(Component.literal("" + (int) defense))).append("    ").append(StatType.MAX_MANA.format(Component.literal(((int) mana) + "/" + ((int) maxMana))));
            ClientboundSetActionBarTextPacket packet = new ClientboundSetActionBarTextPacket(overlayMessageString);
            ((ServerPlayer) (Object) this).connection.send(packet);
        }
    }

    @WrapOperation(method = "addAdditionalSaveData", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V"))
    private void noFood(FoodData instance, CompoundTag compoundTag, Operation<Void> original) {
    }

    @Override
    public StatsMap.Builder molecraft$initStats() {
        return super.molecraft$initStats()
                .stat(StatType.BREAKING_POWER, 0)
                .stat(StatType.MINING_STRENGTH, 10)
                .stat(StatType.BRILLIANCE, 0)
                ;
    }

    @WrapMethod(method = "actuallyHurt")
    private void wrap_actuallyHurt(ServerLevel level, DamageSource damageSource, float amount, Operation<Void> original) {
        MolecraftUtil.dealDamage((LivingEntity) (Object) this, level, damageSource, amount);
    }
}
