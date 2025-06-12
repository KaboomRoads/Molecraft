package com.kaboomroads.molecraft.item;

import com.kaboomroads.molecraft.ModConstants;
import com.kaboomroads.molecraft.entity.StatModifier;
import com.kaboomroads.molecraft.entity.StatType;
import com.kaboomroads.molecraft.item.ability.core.Ability;
import com.kaboomroads.molecraft.item.ability.core.MolecraftEnchant;
import com.kaboomroads.molecraft.util.MolecraftUtil;
import com.kaboomroads.molecraft.util.PlaceholderContents;
import com.kaboomroads.molecraft.util.RomanNumber;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.Unbreakable;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.Function;

public class MolecraftItem {
    public final String id;
    public final Component name;
    public final Rarity rarity;
    public final ItemType itemType;
    public final boolean soulbound;
    public final boolean allowUse;
    public final ItemStack base;
    public final EquipmentSlotGroup activeFor;
    public final TreeMap<StatType, ItemStat> stats;
    public final List<Component> lore;
    public final Function<HolderLookup.Provider, DataComponentPatch> dataComponents;
    public final @Nullable TreeSet<Ability<?, ?, ?>> abilities;
    public static final ResourceLocation STATS_LOCATION = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "stats");
    public static final ResourceLocation ABILITIES_LOCATION = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "abilities");
    public static final ResourceLocation ENCHANTS_LOCATION = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "enchants");
    public static final ResourceLocation RARITY_LOCATION = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "rarity");
    public static final Component STATS = MutableComponent.create(new PlaceholderContents(STATS_LOCATION));
    public static final Component ABILITIES = MutableComponent.create(new PlaceholderContents(ABILITIES_LOCATION));
    public static final Component ENCHANTS = MutableComponent.create(new PlaceholderContents(ENCHANTS_LOCATION));
    public static final Component RARITY = MutableComponent.create(new PlaceholderContents(RARITY_LOCATION));
    public static final Codec<SavedMolecraftItem> CODEC = Codec.lazyInitialized(
            () -> RecordCodecBuilder.create(
                    instance -> instance.group(
                                    ExtraCodecs.optionalEmptyMap(CompoundTag.CODEC).fieldOf("data").orElse(Optional.empty()).forGetter(SavedMolecraftItem::tag),
                                    ExtraCodecs.intRange(1, 99).fieldOf("count").orElse(1).forGetter(SavedMolecraftItem::count)
                            )
                            .apply(instance, SavedMolecraftItem::new)
            )
    );
    private static final Logger LOGGER = LogUtils.getLogger();

    public static DataResult<Tag> encode(ItemStack itemStack, HolderLookup.Provider levelRegistryAccess, Tag outputTag) {
        CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            DataResult<Tag> savedItemStack = ItemStack.SINGLE_ITEM_CODEC.encode(itemStack, levelRegistryAccess.createSerializationContext(NbtOps.INSTANCE), new CompoundTag());
            return CODEC.encode(new SavedMolecraftItem(savedItemStack.isSuccess() ? Optional.of(((CompoundTag) savedItemStack.getOrThrow())) : Optional.empty(), itemStack.getCount()), levelRegistryAccess.createSerializationContext(NbtOps.INSTANCE), outputTag);
        }
        return CODEC.encode(new SavedMolecraftItem(Optional.of(customData.getUnsafe()), itemStack.getCount()), levelRegistryAccess.createSerializationContext(NbtOps.INSTANCE), outputTag);
    }

    public static DataResult<Tag> encodeStart(ItemStack itemStack, HolderLookup.Provider levelRegistryAccess) {
        CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            DataResult<Tag> savedItemStack = ItemStack.SINGLE_ITEM_CODEC.encodeStart(levelRegistryAccess.createSerializationContext(NbtOps.INSTANCE), itemStack);
            return CODEC.encodeStart(levelRegistryAccess.createSerializationContext(NbtOps.INSTANCE), new SavedMolecraftItem(savedItemStack.isSuccess() ? Optional.of(((CompoundTag) savedItemStack.getOrThrow())) : Optional.empty(), itemStack.getCount()));
        }
        return CODEC.encodeStart(levelRegistryAccess.createSerializationContext(NbtOps.INSTANCE), new SavedMolecraftItem(Optional.of(customData.getUnsafe()), itemStack.getCount()));
    }

    public static Optional<ItemStack> parse(HolderLookup.Provider lookupProvider, Tag tag) {
        Optional<SavedMolecraftItem> optional = CODEC.parse(lookupProvider.createSerializationContext(NbtOps.INSTANCE), tag).resultOrPartial(MolecraftItem::invalidItemError);
        if (optional.isPresent()) {
            SavedMolecraftItem instance = optional.get();
            Optional<CompoundTag> optionalTag = instance.tag();
            if (optionalTag.isPresent()) {
                CompoundTag molecraftDataTag = optionalTag.get();
                MolecraftData data = MolecraftData.parse(molecraftDataTag);
                if (data == null) return ItemStack.SINGLE_ITEM_CODEC.parse(lookupProvider.createSerializationContext(NbtOps.INSTANCE), molecraftDataTag).resultOrPartial(MolecraftItem::invalidItemError);
                else return Optional.of(data.construct(instance.count(), lookupProvider));
            }
        }
        return Optional.empty();
    }

    private static void invalidItemError(String string) {
        LOGGER.error("Tried to load invalid item: '{}'", string);
    }

    public MolecraftItem(String id, Component name, Rarity rarity, ItemType itemType, boolean soulbound, boolean allowUse, ItemStack base, EquipmentSlotGroup activeFor, TreeMap<StatType, ItemStat> stats, List<Component> lore, Function<HolderLookup.Provider, DataComponentPatch> dataComponents, @Nullable TreeSet<Ability<?, ?, ?>> abilities) {
        this.id = id;
        this.name = name;
        this.rarity = rarity;
        this.itemType = itemType;
        this.soulbound = soulbound;
        this.allowUse = allowUse;
        this.base = base;
        this.activeFor = activeFor;
        this.stats = stats;
        this.lore = lore;
        this.dataComponents = dataComponents;
        this.abilities = abilities;
    }

    public ItemStack construct(int count, MolecraftData molecraftData, HolderLookup.Provider lookupProvider) {
        ItemStack itemStack = base.copyWithCount(count);
        ArrayList<Component> list = new ArrayList<>(lore.size());
        DataPropertyMap propertyMap = molecraftData.propertyMap();
        TreeMap<MolecraftEnchant<?, ?>, Integer> enchants = propertyMap.get(DataPropertyTypes.ENCHANTS);
        for (Component component : lore) {
            if (component.getContents() instanceof PlaceholderContents(ResourceLocation placeholder)) {
                if (placeholder.equals(STATS_LOCATION)) {
                    stats.forEach((stat, modifier) -> {
                        double value = modifier.value();
                        String prefix = switch (modifier.operation()) {
                            case ADD -> value < 0 ? "" : "+";
                            case MULTIPLY -> "x";
                            case MULTIPLY_BASE -> "basemul";
                        };
                        addDesc(list, stat.name.copy().append(": ").append(stat.format(Component.literal(prefix + MolecraftUtil.FORMAT.format(value)))));
                    });
                } else if (placeholder.equals(ENCHANTS_LOCATION) && enchants != null) {
                    enchants.forEach((enchant, enchantLevel) -> addDesc(list, enchant.name.copy().append(" ").append(RomanNumber.toRoman(enchantLevel)).withStyle(enchantLevel < enchant.highestAchievableLevel ? ChatFormatting.BLUE : enchantLevel == enchant.highestAchievableLevel ? ChatFormatting.GOLD : ChatFormatting.RED)));
                } else if (placeholder.equals(ABILITIES_LOCATION) && abilities != null) {
                    abilities.forEach(ability -> addDesc(list, Component.literal("Ability: ").withStyle(ChatFormatting.GOLD).append(ability.name.copy().withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD)).append(CommonComponents.SPACE).append(Component.literal("[").append(ability.when.name).append(Component.literal("]")).withStyle(ChatFormatting.DARK_PURPLE, ChatFormatting.BOLD))));
                } else if (placeholder.equals(RARITY_LOCATION)) {
                    if (soulbound) addDesc(list, Component.literal("--soulbound--").withStyle(ChatFormatting.DARK_GRAY));
                    addDesc(list, rarity.name.copy().append(CommonComponents.SPACE).append(itemType.name).withStyle(ChatFormatting.BOLD));
                }
            } else addDesc(list, component.copy());
        }
        itemStack.applyComponents(dataComponents.apply(lookupProvider));
        itemStack.set(DataComponents.ITEM_NAME, name.copy().withStyle(rarity.name.getStyle()));
        itemStack.set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);
        itemStack.set(DataComponents.UNBREAKABLE, new Unbreakable(false));
        itemStack.set(DataComponents.LORE, new ItemLore(list, list));
        itemStack.set(DataComponents.CUSTOM_DATA, new CustomData(molecraftData.save()));
        return itemStack;
    }

    private void addDesc(ArrayList<Component> list, MutableComponent component) {
        Style style = component.getStyle();
        component = style.isItalic() ? component : component.withStyle(style.withItalic(false));
        TextColor color = style.getColor();
        list.add(color == null ? component.withStyle(ChatFormatting.WHITE) : component);
    }

    public static Builder builder(String id, Component name, Rarity rarity, ItemType itemType, ItemStack base, EquipmentSlotGroup activeFor, Function<HolderLookup.Provider, DataComponentPatch> dataComponents) {
        return new Builder(id, name, rarity, itemType, base, activeFor, dataComponents);
    }

    public static Builder builder(String id, String name, Rarity rarity, ItemType itemType, Item base, EquipmentSlotGroup activeFor, Function<HolderLookup.Provider, DataComponentPatch> dataComponents) {
        return builder(id, Component.literal(name), rarity, itemType, new ItemStack(base), activeFor, dataComponents);
    }

    public static Builder builder(String id, String name, Rarity rarity, ItemType itemType, Item base, EquipmentSlotGroup activeFor) {
        return builder(id, Component.literal(name), rarity, itemType, new ItemStack(base), activeFor, provider -> DataComponentPatch.EMPTY);
    }

    public static class Builder {
        private final String id;
        private final Component name;
        private final Rarity rarity;
        private final ItemType itemType;
        private boolean allowUse = false;
        private boolean soulbound = false;
        private final ItemStack base;
        private final EquipmentSlotGroup activeFor;
        private final Function<HolderLookup.Provider, DataComponentPatch> dataComponents;
        private @Nullable TreeSet<Ability<?, ?, ?>> abilities = null;
        private final TreeMap<StatType, ItemStat> stats = new TreeMap<>();
        private final ArrayList<Component> lore = new ArrayList<>();

        public Builder(String id, Component name, Rarity rarity, ItemType itemType, ItemStack base, EquipmentSlotGroup activeFor, Function<HolderLookup.Provider, DataComponentPatch> dataComponents) {
            this.id = id;
            this.name = name;
            this.rarity = rarity;
            this.itemType = itemType;
            this.base = base;
            this.activeFor = activeFor;
            this.dataComponents = dataComponents;
        }

        public Builder stat(StatType stat, double value) {
            return stat(stat, value, StatModifier.Operation.ADD);
        }

        public Builder stat(StatType stat, double value, StatModifier.Operation operation) {
            stats.put(stat, new ItemStat(value, operation));
            return this;
        }

        public Builder lore(Component component) {
            lore.add(component);
            return this;
        }

        public Builder lore() {
            return lore(CommonComponents.EMPTY);
        }

        public Builder lore(String string, ChatFormatting... style) {
            return lore(Component.literal(string).withStyle(style));
        }

        public Builder lore(String string) {
            return lore(string, ChatFormatting.GRAY);
        }

        public Builder soulbound() {
            soulbound = true;
            return this;
        }

        public Builder allowUse() {
            allowUse = true;
            return this;
        }

        public Builder ability(Ability<?, ?, ?> ability) {
            if (abilities == null) abilities = new TreeSet<>();
            abilities.add(ability);
            return this;
        }

        public MolecraftItem build() {
            return new MolecraftItem(id, name, rarity, itemType, soulbound, allowUse, base, activeFor, stats, lore, dataComponents, abilities);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MolecraftItem that = (MolecraftItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
