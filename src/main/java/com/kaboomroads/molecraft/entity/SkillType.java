package com.kaboomroads.molecraft.entity;

import com.kaboomroads.molecraft.mixinimpl.ModLivingEntity;
import com.kaboomroads.molecraft.util.MolecraftUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public enum SkillType {
    COMBAT("combat", "Combat", new ItemStack(Items.IRON_SWORD), (skillLevel, player) -> levelUpStatIncrease("combat_level", StatType.DAMAGE, 0.05, skillLevel, player), (skillLevel, player) -> listLevelStatBonuses(StatType.DAMAGE, 0.05, skillLevel, player)),
    MINING("mining", "Mining", new ItemStack(Items.IRON_PICKAXE), (skillLevel, player) -> levelUpStatIncrease("mining_level", StatType.BRILLIANCE, 0.05, skillLevel, player), (skillLevel, player) -> listLevelStatBonuses(StatType.BRILLIANCE, 0.05, skillLevel, player)),
    FISHING("fishing", "Fishing", new ItemStack(Items.FISHING_ROD), (skillLevel, player) -> levelUpStatIncrease("fishing_level", StatType.FISHING_LUCK, 0.05, skillLevel, player), (skillLevel, player) -> listLevelStatBonuses(StatType.FISHING_LUCK, 0.05, skillLevel, player)),
    ;

    public static final Map<String, SkillType> BY_ID = Util.make(new HashMap<>(values().length), hashMap -> {
        for (SkillType type : values()) hashMap.put(type.id, type);
    });
    public final String id;
    public final Component name;
    public final ItemStack icon;
    public final BiConsumer<Integer, Player> onLevelChange;
    public final BiConsumer<Integer, Player> listLevelBonuses;

    SkillType(String id, Component name, ItemStack icon, BiConsumer<Integer, Player> onLevelChange, BiConsumer<Integer, Player> listLevelBonuses) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.onLevelChange = onLevelChange;
        this.listLevelBonuses = listLevelBonuses;
    }

    SkillType(String id, String name, ItemStack icon, BiConsumer<Integer, Player> onLevelChange, BiConsumer<Integer, Player> listLevelBonuses) {
        this(id, Component.literal(name), icon, onLevelChange, listLevelBonuses);
    }

    public static void listLevelStatBonuses(StatType statType, double multiplier, int skillLevel, Player player) {
        player.displayClientMessage(Component.literal("    " + MolecraftUtil.FORMAT.format((skillLevel - 1) * multiplier * 100) + "% -> " + MolecraftUtil.FORMAT.format(skillLevel * multiplier * 100) + "% ").append(statType.name).withStyle(ChatFormatting.GRAY), false);
    }

    public static void levelUpStatIncrease(String modifierId, StatType statType, double multiplier, int skillLevel, Player player) {
        StatsMap stats = ((ModLivingEntity) player).molecraft$getStats();
        StatInstance statInstance = stats.get(statType);
        if (skillLevel > 0) statInstance.putModifier(modifierId, 1.0 + skillLevel * multiplier, StatModifier.Operation.MULTIPLY);
        else statInstance.removeModifier(modifierId);
    }
}
