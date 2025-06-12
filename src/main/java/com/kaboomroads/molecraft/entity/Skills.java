package com.kaboomroads.molecraft.entity;

import com.kaboomroads.molecraft.mixinimpl.ModPlayer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.TreeMap;

public class Skills {
    public static final int[] LEVEL_TO_XP;
    public final Player owner;
    public TreeMap<SkillType, SkillLevel> skillLevels;

    public Skills(Player owner, TreeMap<SkillType, SkillLevel> skillLevels) {
        this.owner = owner;
        this.skillLevels = skillLevels;
        for (SkillType type : SkillType.values()) skillLevels.putIfAbsent(type, new SkillLevel(0, 0));
    }

    public void addXp(SkillType type, int xp) {
        SkillLevel skillLevel = skillLevels.get(type);
        boolean changed = false;
        int xpLeft = xp;
        xpLeft += skillLevel.currentLevelXp;
        while (skillLevel.level < 100 && xpLeft >= LEVEL_TO_XP[skillLevel.level]) {
            xpLeft -= LEVEL_TO_XP[skillLevel.level];
            skillLevel.level++;
            changed = true;
            owner.displayClientMessage(Component.literal("---------- SKILL LEVEL UP! ----------").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD), false);
            owner.displayClientMessage(Component.literal("    ").append(type.name.copy().withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD)).append(" ").append(Component.literal(skillLevel.level - 1 + " -> " + skillLevel.level).withStyle(ChatFormatting.BLUE)), false);
            ((ServerPlayer) owner).connection.send(new ClientboundSoundEntityPacket(BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.PLAYER_LEVELUP), SoundSource.PLAYERS, owner, 1.0F, 1.0F, owner.getRandom().nextLong()));
        }
        skillLevel.currentLevelXp = xpLeft;
        if (changed) {
            type.onLevelChange.accept(skillLevel.level, owner);
            type.listLevelBonuses.accept(skillLevel.level, owner);
            owner.displayClientMessage(Component.literal("-----------------------------------").withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD), false);
        }
        ((ModPlayer) owner).molecraft$displayActionBar(Component.literal("+" + xp + " ").append(type.name).append(" (" + skillLevel.currentLevelXp + (skillLevel.level < 100 ? " / " + LEVEL_TO_XP[skillLevel.level] : "") + ")").withStyle(ChatFormatting.DARK_AQUA), 20);
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        for (Map.Entry<SkillType, SkillLevel> entry : skillLevels.entrySet()) tag.putLong(entry.getKey().id, entry.getValue().toLong());
        return tag;
    }

    public static Skills parse(Player player, CompoundTag tag) {
        TreeMap<SkillType, SkillLevel> skillLevels = new TreeMap<>();
        for (Map.Entry<String, Tag> entry : tag.tags.entrySet()) {
            SkillType type = SkillType.BY_ID.get(entry.getKey());
            if (type != null && entry.getValue() instanceof NumericTag numericTag) skillLevels.put(type, new SkillLevel(numericTag.getAsLong()));
        }
        return new Skills(player, skillLevels);
    }

    static {
        LEVEL_TO_XP = new int[]{
                50,
                150,
                250,
                450,
                650,
                900,
                1250,
                1750,
                2500,
                3300,
                4500,
                6000,
                8000,
                11000,
                15000,
                20000,
                26000,
                35000,
                46000,
                62000,
                83000,
                110000,
                150000,
                200000,
                250000,
                300000,
                350000,
                400000,
                450000,
                500000,
                550000,
                600000,
                650000,
                700000,
                750000,
                800000,
                850000,
                900000,
                950000,
                1000000,
                1050000,
                1100000,
                1150000,
                1200000,
                1250000,
                1300000,
                1350000,
                1400000,
                1450000,
                1500000,
                1550000,
                1600000,
                1650000,
                1700000,
                1750000,
                1800000,
                1850000,
                1900000,
                1950000,
                2000000,
                2050000,
                2100000,
                2150000,
                2200000,
                2250000,
                2300000,
                2350000,
                2400000,
                2450000,
                2500000,
                2550000,
                2600000,
                2650000,
                2700000,
                2750000,
                2800000,
                2850000,
                2900000,
                2950000,
                3000000,
                3050000,
                3100000,
                3150000,
                3200000,
                3250000,
                3300000,
                3350000,
                3400000,
                3450000,
                3500000,
                3550000,
                3600000,
                3650000,
                3700000,
                3750000,
                3800000,
                3850000,
                3900000,
                3950000,
                4000000,
        };
    }
}
