package com.kaboomroads.molecraft.mixin;

import com.kaboomroads.molecraft.attribute.MolecraftAttribute;
import com.kaboomroads.molecraft.item.MolecraftItem;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements DataComponentHolder {
    @Shadow
    public abstract void forEachModifier(EquipmentSlotGroup slotGroup, BiConsumer<Holder<Attribute>, AttributeModifier> action);

    @Shadow
    public abstract Component getStyledHoverName();

    @Shadow
    public abstract Item getItem();

    @Shadow
    public abstract Component getItemName();

    @WrapMethod(method = "getHoverName")
    private Component wrap_getHoverName(Operation<Component> original) {
        if (getItem() instanceof MolecraftItem molecraftItem) return getItemName().copy().withStyle(molecraftItem.rarity.name.getStyle());
        return getItemName();
    }

    @WrapMethod(method = "getCustomName")
    private Component wrap_getCustomName(Operation<Component> original) {
        return null;
    }

    @WrapMethod(method = "getTooltipLines")
    private List<Component> wrap_getTooltipLines(Item.TooltipContext tooltipContext, @Nullable Player player, TooltipFlag tooltipFlag, Operation<List<Component>> original) {
        List<Component> list = new ArrayList<>();
        Component name = getStyledHoverName();
        list.add(name);
        for (EquipmentSlotGroup equipmentSlotGroup : EquipmentSlotGroup.values()) {
            MutableBoolean mutableBoolean = new MutableBoolean(true);
            this.forEachModifier(equipmentSlotGroup, (attribute, modifier) -> {
                if (mutableBoolean.isTrue()) {
                    list.add(Component.translatable("item.modifiers." + equipmentSlotGroup.getSerializedName()).withStyle(ChatFormatting.DARK_GRAY));
                    mutableBoolean.setFalse();
                }
                double amount = modifier.amount();
                if (player != null && modifier.is(Item.BASE_ATTACK_SPEED_ID)) amount += player.getAttributeBaseValue(Attributes.ATTACK_SPEED);
                String prefix = switch (modifier.operation()) {
                    case ADD_VALUE -> amount < 0 ? "" : "+";
                    case ADD_MULTIPLIED_BASE -> "b*";
                    case ADD_MULTIPLIED_TOTAL -> "*";
                };
                NumberFormat format = NumberFormat.getInstance();
                MutableComponent line = Component.translatable(attribute.value().getDescriptionId()).append(": ").withStyle(ChatFormatting.GRAY);
                if (attribute.value() instanceof MolecraftAttribute attributeValue)
                    line.append(attributeValue.format(Component.literal(prefix + format.format(amount))));
                else if (attribute.is(Attributes.ATTACK_SPEED.unwrapKey().get())) line.append(Component.literal(prefix + format.format(amount)).append("☰").withStyle(ChatFormatting.YELLOW));
                else line.append(Component.literal(prefix + format.format(amount)));
                list.add(line);
            });
        }
        getItem().appendHoverText((ItemStack) (Object) this, tooltipContext, list, tooltipFlag);
        return list;
    }
}
