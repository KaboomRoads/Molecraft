package com.kaboomroads.molecraft.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.Equippable;

import java.util.HashMap;

public class MolecraftItems {
    public static HashMap<String, MolecraftItem> ITEMS = new HashMap<>();

    public static MolecraftItem CREATION = register(MolecraftItem.builder("creation",
                    "Creation",
                    Rarity.ADMIN,
                    ItemType.SWORD,
                    Items.ITEM_FRAME,
                    EquipmentSlotGroup.MAINHAND
            )
            .stat(StatType.DAMAGE, 25.0)
            .stat(StatType.CRIT_DAMAGE, 40.0)
            .stat(StatType.CRIT_CHANCE, 50.0)
            .stat(StatType.MAX_HEALTH, 50.0)
            .stat(StatType.DEFENSE, 100.0)
            .stat(StatType.HEALTH_REGEN, -5.0)
            .stat(StatType.SPELL_DAMAGE, 40.0)
            .stat(StatType.MAX_MANA, 120.0)
            .stat(StatType.MANA_REGEN, 10.0)
            .lore("The Bing...", ChatFormatting.DARK_GRAY)
            .lore(MolecraftItem.STATS)
            .lore()
            .lore("How Bingulous", ChatFormatting.DARK_AQUA, ChatFormatting.UNDERLINE)
            .lore()
            .lore(Component.literal("zoinks").withColor(0x32A852).withStyle(ChatFormatting.ITALIC))
            .lore(Component.literal("clean"))
            .lore(MolecraftItem.RARITY)
            .build());
    public static MolecraftItem BOOT = register(MolecraftItem.builder("boot",
                    "Boot",
                    Rarity.ADMIN,
                    ItemType.BOOTS,
                    Items.GOLDEN_APPLE,
                    EquipmentSlotGroup.FEET,
                    DataComponentPatch.builder().set(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.FEET).setEquipSound(ArmorMaterials.NETHERITE.equipSound()).setAsset(ArmorMaterials.NETHERITE.assetId()).build()).build()
            )
            .stat(StatType.DAMAGE, 25.0)
            .stat(StatType.CRIT_DAMAGE, 40.0)
            .stat(StatType.CRIT_CHANCE, 50.0)
            .stat(StatType.MAX_HEALTH, 50.0)
            .stat(StatType.DEFENSE, 100.0)
            .stat(StatType.HEALTH_REGEN, 10.0)
            .stat(StatType.SPELL_DAMAGE, 40.0)
            .stat(StatType.MAX_MANA, 120.0)
            .stat(StatType.MANA_REGEN, 10.0)
            .lore(MolecraftItem.STATS)
            .lore()
            .lore(MolecraftItem.RARITY)
            .build());
    public static MolecraftItem REN = register(MolecraftItem.builder("ren",
                    "Ren",
                    Rarity.ADMIN,
                    ItemType.BOW,
                    Items.BOW,
                    EquipmentSlotGroup.MAINHAND
            )
            .stat(StatType.DAMAGE, 60.0)
            .stat(StatType.CRIT_DAMAGE, 40.0)
            .stat(StatType.CRIT_CHANCE, 50.0)
            .lore(MolecraftItem.STATS)
            .lore(MolecraftItem.STATS)
            .lore(MolecraftItem.STATS)
            .lore(MolecraftItem.STATS)
            .lore(MolecraftItem.STATS)
            .lore()
            .lore(MolecraftItem.RARITY)
            .build());

    public static MolecraftItem register(MolecraftItem item) {
        ITEMS.put(item.id, item);
        return item;
    }
}
