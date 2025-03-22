package com.kaboomroads.molecraft.util;

import com.kaboomroads.molecraft.init.ModAttributes;
import com.kaboomroads.molecraft.mixinimpl.MolecraftLivingEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.gameevent.GameEvent;

public class MolecraftUtil {
    public static double calculateDamageDealt(double damage, double critDamage, double critChance, RandomSource random) {
        if (random.nextDouble() < critChance * 0.01) damage *= (1 + critDamage * 0.01);
        return damage;
    }

    public static double calculateDamageTaken(double damage, double defense) {
        return damage * (1 - defense / (defense + 100));
    }

    public static void dealDamage(LivingEntity livingEntity, ServerLevel level, DamageSource damageSource, float amount) {
        if (!livingEntity.isInvulnerableTo(level, damageSource)) {
            Entity entity = damageSource.getEntity();
            if (entity instanceof LivingEntity attacker) {
                double damage = attacker.getAttributeValue(ModAttributes.DAMAGE);
                double critDamage = attacker.getAttributeValue(ModAttributes.CRIT_DAMAGE);
                double critChance = attacker.getAttributeValue(ModAttributes.CRIT_CHANCE);
                double molecraftAmount = MolecraftUtil.calculateDamageDealt(damage, critDamage, critChance, livingEntity.getRandom());
                double defense = livingEntity.getAttributeValue(ModAttributes.DEFENSE);
                molecraftAmount = MolecraftUtil.calculateDamageTaken(molecraftAmount, defense);
                if (molecraftAmount > 0.0) {
                    livingEntity.getCombatTracker().recordDamage(damageSource, (float) molecraftAmount);
                    ((MolecraftLivingEntity) livingEntity).molecraft$setHealth(((MolecraftLivingEntity) livingEntity).molecraft$getHealth() - molecraftAmount);
                    livingEntity.gameEvent(GameEvent.ENTITY_DAMAGE);
                }
            } else {
                amount = amount;
                if (amount > 0.0F) {
                    livingEntity.getCombatTracker().recordDamage(damageSource, amount);
                    livingEntity.setHealth(livingEntity.getHealth() - amount);
                    livingEntity.gameEvent(GameEvent.ENTITY_DAMAGE);
                }
            }
        }
    }
}
