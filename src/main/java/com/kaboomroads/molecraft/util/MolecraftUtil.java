package com.kaboomroads.molecraft.util;

import com.kaboomroads.molecraft.entity.*;
import com.kaboomroads.molecraft.entity.custom.DamageIndicator;
import com.kaboomroads.molecraft.loot.Loot;
import com.kaboomroads.molecraft.loot.LootManager;
import com.kaboomroads.molecraft.mixinimpl.ModEntity;
import com.kaboomroads.molecraft.mixinimpl.ModLivingEntity;
import com.kaboomroads.molecraft.mixinimpl.ModPlayer;
import com.kaboomroads.molecraft.mixinimpl.ModServerLevelData;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.gameevent.GameEvent;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Locale;

public class MolecraftUtil {
    public static double calculateDamageDealt(double damage, double critDamage, double critChance, double roll) {
        if (roll < critChance * 0.01) damage *= (1 + critDamage * 0.01);
        return damage;
    }

    public static double calculateDamageTaken(double damage, double defense) {
        return damage * (1 - defense / (defense + 100));
    }

    public static Component formatDamage(DamageContext damageContext) {
        String string = MolecraftUtil.FORMAT.format(damageContext.damage());
        ChatFormatting style = ChatFormatting.WHITE;
        if (damageContext.crit()) style = ChatFormatting.RED;
        else if (damageContext.vanillaDamage()) style = ChatFormatting.DARK_AQUA;
        return Component.literal(string).withStyle(style);
    }

    public static final ThreadLocal<StatsMap> MOLECRAFT_DAMAGE_OVERRIDE = new ThreadLocal<>();

    public static void actuallyHurt(LivingEntity hurt, ServerLevel level, DamageSource damageSource, float amount) {
        DamageContext damageContext = MolecraftUtil.dealDamage(hurt, level, damageSource, amount);
        DamageIndicator damageIndicator = (DamageIndicator) MolecraftEntities.DAMAGE_INDICATOR.construct(level);
        damageIndicator.removeTime = level.getGameTime() + 20;
        RandomSource random = hurt.getRandom();
        damageIndicator.setPos(hurt.getBoundingBox().getCenter().add(random.nextDouble() - 0.5, random.nextDouble() - 0.5, random.nextDouble() - 0.5));
        damageIndicator.setCustomName(MolecraftUtil.formatDamage(damageContext));
        level.addFreshEntity(damageIndicator);
    }

    public static void molecraftHurtServer(Player player, StatsMap molecraftDamage, ServerLevel level, DamageSource damageSource) {
        MolecraftUtil.MOLECRAFT_DAMAGE_OVERRIDE.set(molecraftDamage);
        player.hurtServer(level, damageSource, 1.0F);
        MolecraftUtil.MOLECRAFT_DAMAGE_OVERRIDE.remove();
    }

    public static DamageContext dealDamage(LivingEntity livingEntity, ServerLevel level, DamageSource damageSource, float amount) {
        if (!livingEntity.isInvulnerableTo(level, damageSource)) {
            Entity entity = damageSource.getEntity();
            StatsMap override = MOLECRAFT_DAMAGE_OVERRIDE.get();
            if (override != null) return molecraftHurt(livingEntity, damageSource, override);
            if (entity instanceof LivingEntity attacker) {
                StatsMap attackerStats = ((ModLivingEntity) attacker).molecraft$getStats();
                return molecraftHurt(livingEntity, damageSource, attackerStats);
            } else {
                if (amount > 0.0F) {
                    livingEntity.getCombatTracker().recordDamage(damageSource, amount);
                    livingEntity.setHealth(livingEntity.getHealth() - amount);
                    livingEntity.gameEvent(GameEvent.ENTITY_DAMAGE);
                }
            }
        }
        return new DamageContext(amount, false, true);
    }

    public static DamageContext molecraftHurt(LivingEntity livingEntity, DamageSource damageSource, StatsMap attackerStats) {
        StatsMap livingEntityStats = ((ModLivingEntity) livingEntity).molecraft$getStats();
        if (!livingEntityStats.hits) {
            double damage = attackerStats.get(StatType.DAMAGE).cachedValue;
            double critDamage = attackerStats.get(StatType.CRIT_DAMAGE).cachedValue;
            double critChance = attackerStats.get(StatType.CRIT_CHANCE).cachedValue;
            double roll = livingEntity.getRandom().nextDouble();
            double molecraftAmount = MolecraftUtil.calculateDamageDealt(damage, critDamage, critChance, roll);
            double defense = livingEntityStats.get(StatType.DEFENSE).cachedValue;
            molecraftAmount = MolecraftUtil.calculateDamageTaken(molecraftAmount, defense);
            if (molecraftAmount > 0.0) {
                livingEntity.getCombatTracker().recordDamage(damageSource, (float) molecraftAmount);
                ((ModLivingEntity) livingEntity).molecraft$setHealth(((ModLivingEntity) livingEntity).molecraft$getHealth() - molecraftAmount);
                livingEntity.gameEvent(GameEvent.ENTITY_DAMAGE);
            }
            return new DamageContext(molecraftAmount, roll < critChance * 0.01, false);
        } else {
            livingEntity.getCombatTracker().recordDamage(damageSource, 1);
            ((ModLivingEntity) livingEntity).molecraft$setHealth(((ModLivingEntity) livingEntity).molecraft$getHealth() - 1.0000001);
            livingEntity.gameEvent(GameEvent.ENTITY_DAMAGE);
            return new DamageContext(1, false, false);
        }
    }

    public static final NumberFormat FORMAT = NumberFormat.getCompactNumberInstance(Locale.US, NumberFormat.Style.SHORT);

    static {
        FORMAT.setRoundingMode(RoundingMode.DOWN);
        FORMAT.setMaximumFractionDigits(1);
    }

    public static Component getEntityNameTag(LivingEntity entity) {
        ModLivingEntity krtek = (ModLivingEntity) entity;
        double health = krtek.molecraft$getHealth();
        StatsMap stats = krtek.molecraft$getStats();
        double maxHealth = stats.get(StatType.MAX_HEALTH).cachedValue;
        double defense = stats.get(StatType.DEFENSE).cachedValue;
        Component molecraftName = krtek.molecraft$getName();
        return (molecraftName != null ? molecraftName : entity.getTypeName()).copy().append(Component.literal(" " + FORMAT.format(health) + "/" + FORMAT.format(maxHealth)).withStyle(health <= maxHealth * 0.5 ? ChatFormatting.YELLOW : ChatFormatting.RED).append(Component.literal(stats.hits ? "💠" : StatType.MAX_HEALTH.icon).withStyle(stats.hits ? ChatFormatting.WHITE : ChatFormatting.RED)).append(Component.literal(" " + FORMAT.format(defense) + StatType.DEFENSE.icon).withStyle(ChatFormatting.GREEN)));
    }

    public static void dropEntityLootAndXp(Entity entity, ServerLevel level, DamageSource damageSource, String lootId) {
        dropEntityLoot(entity, level, damageSource, lootId);
        if (damageSource.getEntity() instanceof Player player) {
            String molecraftId = ((ModEntity) entity).molecraft$getId();
            MolecraftEntity molecraftEntity = MolecraftEntities.ENTITIES.get(molecraftId);
            if (molecraftEntity != null) ((ModPlayer) player).molecraft$getSkills().addXp(SkillType.COMBAT, molecraftEntity.xp);
        }
    }

    public static void dropEntityLoot(Entity entity, ServerLevel level, DamageSource damageSource, String lootId) {
        LootManager lootManager = ((ModServerLevelData) level.getLevelData()).molecraft$getLootManager();
        Loot dropLoot = lootManager.get(lootId);
        Iterator<ItemStack> iterator = dropLoot.collect(entity.getRandom(), (damageSource.getEntity() instanceof Player player) ? ((float) ((ModLivingEntity) player).molecraft$getStats().get(StatType.LOOTING).cachedValue * 0.01F + 1.0F) : 1.0F, entity.registryAccess());
        while (iterator.hasNext()) {
            ItemStack itemStack = iterator.next();
            ItemEntity itemEntity = new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), itemStack);
            itemEntity.setDefaultPickUpDelay();
            if (damageSource.getEntity() instanceof Player player) itemEntity.setTarget(player.getUUID());
            level.addFreshEntity(itemEntity);
        }
    }
}
