package com.kaboomroads.molecraft.util;

import com.kaboomroads.molecraft.item.StatInstance;
import com.kaboomroads.molecraft.item.StatType;
import com.kaboomroads.molecraft.mixinimpl.MolecraftLivingEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.TreeMap;

public class MolecraftUtil {
    public static double calculateDamageDealt(double damage, double critDamage, double critChance, RandomSource random) {
        if (random.nextDouble() < critChance * 0.01) damage *= (1 + critDamage * 0.01);
        return damage;
    }

    public static double calculateDamageTaken(double damage, double defense) {
        return damage * (1 - defense / (defense + 100));
    }

    public static void initStats(TreeMap<StatType, StatInstance> stats, StatType... types) {
        for (StatType type : types) stats.put(type, new StatInstance(type));
    }

    public static void dealDamage(LivingEntity livingEntity, ServerLevel level, DamageSource damageSource, float amount) {
        if (!livingEntity.isInvulnerableTo(level, damageSource)) {
            Entity entity = damageSource.getEntity();
            if (entity instanceof LivingEntity attacker) {
                TreeMap<StatType, StatInstance> attackerStats = ((MolecraftLivingEntity) attacker).molecraft$getStats();
                TreeMap<StatType, StatInstance> livingEntityStats = ((MolecraftLivingEntity) livingEntity).molecraft$getStats();
                double damage = attackerStats.get(StatType.DAMAGE).cachedValue;
                double critDamage = attackerStats.get(StatType.CRIT_DAMAGE).cachedValue;
                double critChance = attackerStats.get(StatType.CRIT_CHANCE).cachedValue;
                double molecraftAmount = MolecraftUtil.calculateDamageDealt(damage, critDamage, critChance, livingEntity.getRandom());
                double defense = livingEntityStats.get(StatType.DEFENSE).cachedValue;
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
