package com.kaboomroads.molecraft.util;

import com.kaboomroads.molecraft.entity.StatType;
import com.kaboomroads.molecraft.entity.StatsMap;
import com.kaboomroads.molecraft.mixinimpl.ModLivingEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.gameevent.GameEvent;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

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
                StatsMap attackerStats = ((ModLivingEntity) attacker).molecraft$getStats();
                StatsMap livingEntityStats = ((ModLivingEntity) livingEntity).molecraft$getStats();
                double damage = attackerStats.get(StatType.DAMAGE).cachedValue;
                double critDamage = attackerStats.get(StatType.CRIT_DAMAGE).cachedValue;
                double critChance = attackerStats.get(StatType.CRIT_CHANCE).cachedValue;
                double molecraftAmount = MolecraftUtil.calculateDamageDealt(damage, critDamage, critChance, livingEntity.getRandom());
                double defense = livingEntityStats.get(StatType.DEFENSE).cachedValue;
                molecraftAmount = MolecraftUtil.calculateDamageTaken(molecraftAmount, defense);
                if (molecraftAmount > 0.0) {
                    livingEntity.getCombatTracker().recordDamage(damageSource, (float) molecraftAmount);
                    ((ModLivingEntity) livingEntity).molecraft$setHealth(((ModLivingEntity) livingEntity).molecraft$getHealth() - molecraftAmount);
                    livingEntity.gameEvent(GameEvent.ENTITY_DAMAGE);
                }
            } else {
                if (amount > 0.0F) {
                    livingEntity.getCombatTracker().recordDamage(damageSource, amount);
                    livingEntity.setHealth(livingEntity.getHealth() - amount);
                    livingEntity.gameEvent(GameEvent.ENTITY_DAMAGE);
                }
            }
        }
    }

    public static Component getEntityNameTag(LivingEntity entity) {
        NumberFormat format = NumberFormat.getCompactNumberInstance(Locale.US, NumberFormat.Style.SHORT);
        format.setRoundingMode(RoundingMode.DOWN);
        format.setMaximumFractionDigits(1);
        ModLivingEntity krtek = (ModLivingEntity) entity;
        double health = krtek.molecraft$getHealth();
        StatsMap stats = krtek.molecraft$getStats();
        double maxHealth = stats.get(StatType.MAX_HEALTH).cachedValue;
        double defense = stats.get(StatType.DEFENSE).cachedValue;
        Component molecraftName = krtek.molecraft$getName();
        return (molecraftName != null ? molecraftName : entity.getTypeName()).copy().append(Component.literal(" " + format.format(health) + "/" + format.format(maxHealth)).withStyle(health <= maxHealth * 0.5 ? ChatFormatting.YELLOW : ChatFormatting.RED).append(Component.literal(StatType.MAX_HEALTH.icon).withStyle(ChatFormatting.RED)).append(Component.literal(" " + format.format(defense) + StatType.DEFENSE.icon).withStyle(ChatFormatting.GREEN)));
    }
}
