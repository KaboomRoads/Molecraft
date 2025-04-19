package com.kaboomroads.molecraft.item;

import com.kaboomroads.molecraft.ModConstants;
import com.kaboomroads.molecraft.entity.StatType;
import com.kaboomroads.molecraft.util.PlaceholderContents;
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
import org.slf4j.Logger;

import java.text.NumberFormat;
import java.util.*;
import java.util.function.Function;

public class MolecraftItem {
    public final String id;
    public final Component name;
    public final Rarity rarity;
    public final ItemType itemType;
    public final boolean soulbound;

    public final ItemStack base;
    public final EquipmentSlotGroup activeFor;
    public final TreeMap<StatType, Double> stats;
    public final List<Component> lore;
    public final Function<HolderLookup.Provider, DataComponentPatch> dataComponents;
    public static final ResourceLocation STATS_LOCATION = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "stats");
    public static final ResourceLocation RARITY_LOCATION = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "rarity");
    public static final Component STATS = MutableComponent.create(new PlaceholderContents(STATS_LOCATION));
    public static final Component RARITY = MutableComponent.create(new PlaceholderContents(RARITY_LOCATION));
    public static final Codec<MolecraftItemInstance> CODEC = Codec.lazyInitialized(
            () -> RecordCodecBuilder.create(
                    instance -> instance.group(
                                    ExtraCodecs.optionalEmptyMap(CompoundTag.CODEC).fieldOf("molecraft_data").orElse(Optional.empty()).forGetter(MolecraftItemInstance::tag),
                                    ExtraCodecs.intRange(1, 99).fieldOf("count").orElse(1).forGetter(MolecraftItemInstance::count)
                            )
                            .apply(instance, MolecraftItemInstance::new)
            )
    );
    private static final Logger LOGGER = LogUtils.getLogger();

    public static DataResult<Tag> encode(ItemStack itemStack, HolderLookup.Provider levelRegistryAccess, Tag outputTag) {
        CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA);
        return CODEC.encode(new MolecraftItemInstance(customData == null ? Optional.empty() : Optional.of(customData.getUnsafe()), itemStack.getCount()), levelRegistryAccess.createSerializationContext(NbtOps.INSTANCE), outputTag);
    }

    public static DataResult<Tag> encodeStart(ItemStack itemStack, HolderLookup.Provider levelRegistryAccess) {
        CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA);
        return CODEC.encodeStart(levelRegistryAccess.createSerializationContext(NbtOps.INSTANCE), new MolecraftItemInstance(customData == null ? Optional.empty() : Optional.of(customData.getUnsafe()), itemStack.getCount()));
    }

    public static Optional<ItemStack> parse(HolderLookup.Provider lookupProvider, Tag tag) {
        Optional<MolecraftItemInstance> optional = CODEC.parse(lookupProvider.createSerializationContext(NbtOps.INSTANCE), tag)
                .resultOrPartial(string -> LOGGER.error("Tried to load invalid item: '{}'", string));
        if (optional.isPresent()) {
            MolecraftItemInstance instance = optional.get();
            Optional<CompoundTag> optionalTag = instance.tag();
            if (optionalTag.isPresent()) {
                MolecraftData data = MolecraftData.parse(optionalTag.get());
                return Optional.of(data.getItem().construct(instance.count(), lookupProvider));
            }
        }
        return Optional.empty();
    }

    public MolecraftItem(String id, Component name, Rarity rarity, ItemType itemType, boolean soulbound, ItemStack base, EquipmentSlotGroup activeFor, TreeMap<StatType, Double> stats, List<Component> lore, Function<HolderLookup.Provider, DataComponentPatch> dataComponents) {
        this.id = id;
        this.name = name;
        this.rarity = rarity;
        this.itemType = itemType;
        this.soulbound = soulbound;
        this.base = base;
        this.activeFor = activeFor;
        this.stats = stats;
        this.lore = lore;
        this.dataComponents = dataComponents;
    }

    public ItemStack construct(int count, HolderLookup.Provider lookupProvider) {
        ItemStack itemStack = base.copyWithCount(count);
        ArrayList<Component> list = new ArrayList<>(lore.size());
        NumberFormat format = NumberFormat.getInstance();
        for (Component component : lore) {
            if (component.getContents() instanceof PlaceholderContents placeholderContents) {
                ResourceLocation placeholder = placeholderContents.placeholderId();
                if (placeholder.equals(STATS_LOCATION)) {
                    stats.forEach((stat, value) -> addDeLoreified(list, stat.name.copy().append(": ").append(stat.format(Component.literal(format.format(value))))));
                } else if (placeholder.equals(RARITY_LOCATION)) {
                    if (soulbound) addDeLoreified(list, Component.literal("--soulbound--").withStyle(ChatFormatting.DARK_GRAY));
                    addDeLoreified(list, rarity.name.copy().append(CommonComponents.SPACE).append(itemType.name).withStyle(ChatFormatting.BOLD));
                }
            } else addDeLoreified(list, component.copy());
        }
        itemStack.applyComponents(dataComponents.apply(lookupProvider));
        itemStack.set(DataComponents.ITEM_NAME, name.copy().withStyle(rarity.name.getStyle()));
        itemStack.set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);
        itemStack.set(DataComponents.UNBREAKABLE, new Unbreakable(false));
        itemStack.set(DataComponents.LORE, new ItemLore(list, list));
        itemStack.set(DataComponents.CUSTOM_DATA, new CustomData(new MolecraftData(id).save()));
        return itemStack;
    }

    private void addDeLoreified(ArrayList<Component> list, MutableComponent component) {
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
        private boolean soulbound;
        private final ItemStack base;
        private final EquipmentSlotGroup activeFor;
        private final Function<HolderLookup.Provider, DataComponentPatch> dataComponents;
        private final TreeMap<StatType, Double> stats = new TreeMap<>();
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
            stats.put(stat, value);
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

        public MolecraftItem build() {
            return new MolecraftItem(id, name, rarity, itemType, soulbound, base, activeFor, stats, lore, dataComponents);
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
