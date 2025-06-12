package com.kaboomroads.molecraft.item;

import com.kaboomroads.molecraft.entity.StatType;
import com.kaboomroads.molecraft.item.ability.core.MolecraftEnchants;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.equipment.ArmorMaterials;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import net.minecraft.world.item.equipment.trim.TrimMaterials;
import net.minecraft.world.item.equipment.trim.TrimPatterns;

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
            .stat(StatType.BREAKING_POWER, 10.0)
            .stat(StatType.MINING_STRENGTH, 50.0)
            .stat(StatType.BRILLIANCE, 12.0)
            .lore("The Bing...", ChatFormatting.DARK_GRAY)
            .lore(MolecraftItem.STATS)
            .lore()
            .lore(MolecraftItem.ABILITIES)
            .lore()
            .lore(MolecraftItem.ENCHANTS)
            .lore()
            .lore("How Bingulous", ChatFormatting.DARK_AQUA, ChatFormatting.UNDERLINE)
            .lore()
            .lore(Component.literal("zoinks").withColor(0x32A852).withStyle(ChatFormatting.ITALIC))
            .lore(Component.literal("clean"))
            .lore(MolecraftItem.RARITY)
            .build());
    public static MolecraftItem DESTRUCTION = register(MolecraftItem.builder("destruction",
                    "Destruction",
                    Rarity.ADMIN,
                    ItemType.SWORD,
                    Items.NETHERITE_SWORD,
                    EquipmentSlotGroup.MAINHAND
            )
            .stat(StatType.DAMAGE, Double.MAX_VALUE)
            .lore("Florgus...", ChatFormatting.DARK_GRAY)
            .lore(MolecraftItem.STATS)
            .lore()
            .lore(MolecraftItem.ABILITIES)
            .lore()
            .lore(MolecraftItem.ENCHANTS)
            .lore()
            .lore(MolecraftItem.RARITY)
            .build());
    public static MolecraftItem FISH_INGROD = register(MolecraftItem.builder("fish_ingrod",
                    "Fish Ingrod",
                    Rarity.ADMIN,
                    ItemType.FISHING_ROD,
                    Items.COD,
                    EquipmentSlotGroup.MAINHAND
            )
            .allowUse()
            .stat(StatType.REEL_STRENGTH, 100.0)
            .stat(StatType.FISHING_LUCK, 50.0)
            .ability(MolecraftEnchants.IGNITING)
            .lore(MolecraftItem.STATS)
            .lore()
            .lore(MolecraftItem.ABILITIES)
            .lore()
            .lore(MolecraftItem.ENCHANTS)
            .lore()
            .lore(MolecraftItem.RARITY)
            .build());
    public static MolecraftItem BOOT = register(MolecraftItem.builder("boot",
                    "Boot",
                    Rarity.ADMIN,
                    ItemType.BOOTS,
                    Items.GOLDEN_APPLE,
                    EquipmentSlotGroup.FEET,
                    provider -> DataComponentPatch.builder()
                            .set(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.FEET)
                                    .setEquipSound(ArmorMaterials.NETHERITE.equipSound())
                                    .setAsset(ArmorMaterials.LEATHER.assetId()).build())
                            .set(DataComponents.DYED_COLOR, new DyedItemColor(0x32A852, true))
                            .set(DataComponents.MAX_STACK_SIZE, 1)
                            .set(DataComponents.TRIM, new ArmorTrim(provider.get(TrimMaterials.COPPER).get(), provider.get(TrimPatterns.FLOW).get(), false))
                            .build()
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
            .lore(MolecraftItem.ABILITIES)
            .lore()
            .lore(MolecraftItem.ENCHANTS)
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
            .lore(MolecraftItem.ABILITIES)
            .lore()
            .lore(MolecraftItem.ENCHANTS)
            .lore()
            .lore(MolecraftItem.RARITY)
            .soulbound()
            .build());
    public static MolecraftItem STONE = register(MolecraftItem.builder("stone",
                    "Stone",
                    Rarity.COMMON,
                    ItemType.NONE,
                    Items.COBBLESTONE,
                    EquipmentSlotGroup.MAINHAND
            )
            .lore("Rock and stone.")
            .lore()
            .lore(MolecraftItem.RARITY)
            .build());
    public static MolecraftItem PALEROCK = register(MolecraftItem.builder("palerock",
                    "Palerock",
                    Rarity.RARE,
                    ItemType.NONE,
                    Items.FIREWORK_STAR,
                    EquipmentSlotGroup.MAINHAND
            )
            .lore("Pale and cold.")
            .lore()
            .lore(MolecraftItem.RARITY)
            .build());
    public static MolecraftItem MUD = register(MolecraftItem.builder("mud",
                    "Mud",
                    Rarity.COMMON,
                    ItemType.NONE,
                    Items.MUD,
                    EquipmentSlotGroup.MAINHAND
            )
            .lore("It's just sludge.")
            .lore()
            .lore(MolecraftItem.RARITY)
            .build());
    public static MolecraftItem RED_MUSHROOM = register(MolecraftItem.builder("red_mushroom",
                    "Red Mushroom",
                    Rarity.COMMON,
                    ItemType.NONE,
                    Items.RED_MUSHROOM,
                    EquipmentSlotGroup.MAINHAND
            )
            .lore("Do not eat this.")
            .lore()
            .lore(MolecraftItem.RARITY)
            .build());
    public static MolecraftItem BROWN_MUSHROOM = register(MolecraftItem.builder("brown_mushroom",
                    "Brown Mushroom",
                    Rarity.COMMON,
                    ItemType.NONE,
                    Items.BROWN_MUSHROOM,
                    EquipmentSlotGroup.MAINHAND
            )
            .lore("Inedible.")
            .lore()
            .lore(MolecraftItem.RARITY)
            .build());
    public static MolecraftItem RESIN = register(MolecraftItem.builder("resin",
                    "Resin",
                    Rarity.UNCOMMON,
                    ItemType.NONE,
                    Items.RESIN_CLUMP,
                    EquipmentSlotGroup.MAINHAND
            )
            .lore("Tastes bitter.")
            .lore()
            .lore(MolecraftItem.RARITY)
            .build());
    public static MolecraftItem COAL = register(MolecraftItem.builder("coal",
                    "Coal",
                    Rarity.COMMON,
                    ItemType.NONE,
                    Items.COAL,
                    EquipmentSlotGroup.MAINHAND
            )
            .lore("They say this burns well.")
            .lore()
            .lore(MolecraftItem.RARITY)
            .build());
    public static MolecraftItem IRON = register(MolecraftItem.builder("iron",
                    "Iron",
                    Rarity.COMMON,
                    ItemType.NONE,
                    Items.IRON_INGOT,
                    EquipmentSlotGroup.MAINHAND
            )
            .lore("How is it so shiny,")
            .lore("I literally just pulled")
            .lore("it out of this rock.")
            .lore()
            .lore(MolecraftItem.RARITY)
            .build());
    public static MolecraftItem COPPER = register(MolecraftItem.builder("copper",
                    "Copper",
                    Rarity.COMMON,
                    ItemType.NONE,
                    Items.COPPER_INGOT,
                    EquipmentSlotGroup.MAINHAND
            )
            .lore("Nice conductor.")
            .lore()
            .lore(MolecraftItem.RARITY)
            .build());

    public static MolecraftItem register(MolecraftItem item) {
        ITEMS.put(item.id, item);
        return item;
    }
}
