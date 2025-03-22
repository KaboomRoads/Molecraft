package com.kaboomroads.molecraft.init;

import com.kaboomroads.molecraft.ModConstants;
import com.kaboomroads.molecraft.item.ItemType;
import com.kaboomroads.molecraft.item.MolecraftItem;
import com.kaboomroads.molecraft.item.Rarity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class ModItems {
    public static final Item CREATION = registerItem(
            "creation", properties -> MolecraftItem.builder(-2.4F, EquipmentSlotGroup.MAINHAND, properties, Rarity.ADMIN, ItemType.SWORD)
                    .damage(75)
                    .spellDamage(65)
                    .maxMana(150)
                    .manaRegen(5)
                    .critDamage(125)
                    .critChance(50)
                    .maxHealth(100)
                    .healthRegen(5)
                    .defense(25)
                    .breakingPower(3)
                    .miningSpeed(100)
                    .miningFortune(25)
                    .build(), new Item.Properties().fireResistant()
    );

    public static final Item NOISE_PERMAFROST = registerBlock(ModBlocks.NOISE_PERMAFROST);

    private static Function<Item.Properties, Item> createBlockItemWithCustomItemName(Block block) {
        return properties -> new BlockItem(block, properties.useItemDescriptionPrefix());
    }

    private static ResourceKey<Item> itemId(String string) {
        return ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, string));
    }

    private static ResourceKey<Item> blockIdToItemId(ResourceKey<Block> resourceKey) {
        return ResourceKey.create(Registries.ITEM, resourceKey.location());
    }

    public static Item registerBlock(Block block) {
        return registerBlock(block, BlockItem::new);
    }

    public static Item registerBlock(Block block, Item.Properties properties) {
        return registerBlock(block, BlockItem::new, properties);
    }

    public static Item registerBlock(Block block, UnaryOperator<Item.Properties> unaryOperator) {
        return registerBlock(block, (blockx, properties) -> new BlockItem(blockx, unaryOperator.apply(properties)));
    }

    public static Item registerBlock(Block block, Block... blocks) {
        Item item = registerBlock(block);

        for (Block block2 : blocks) {
            Item.BY_BLOCK.put(block2, item);
        }

        return item;
    }

    public static Item registerBlock(Block block, BiFunction<Block, Item.Properties, Item> biFunction) {
        return registerBlock(block, biFunction, new Item.Properties());
    }

    public static Item registerBlock(Block block, BiFunction<Block, Item.Properties, Item> biFunction, Item.Properties properties) {
        return registerItem(blockIdToItemId(block.builtInRegistryHolder().key()), propertiesx -> biFunction.apply(block, propertiesx), properties.useBlockDescriptionPrefix());
    }

    public static Item registerItem(String string, Function<Item.Properties, Item> function) {
        return registerItem(itemId(string), function, new Item.Properties());
    }

    public static Item registerItem(String string, Function<Item.Properties, Item> function, Item.Properties properties) {
        return registerItem(itemId(string), function, properties);
    }

    public static Item registerItem(String string, Item.Properties properties) {
        return registerItem(itemId(string), Item::new, properties);
    }

    public static Item registerItem(String string) {
        return registerItem(itemId(string), Item::new, new Item.Properties());
    }

    public static Item registerItem(ResourceKey<Item> resourceKey, Function<Item.Properties, Item> function) {
        return registerItem(resourceKey, function, new Item.Properties());
    }

    public static Item registerItem(ResourceKey<Item> resourceKey, Function<Item.Properties, Item> function, Item.Properties properties) {
        Item item = function.apply(properties.setId(resourceKey));
        if (item instanceof BlockItem blockItem) blockItem.registerBlocks(Item.BY_BLOCK, item);
        return Registry.register(BuiltInRegistries.ITEM, resourceKey, item);
    }

    public static void init() {
    }
}
