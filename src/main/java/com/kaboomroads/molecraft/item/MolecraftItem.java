package com.kaboomroads.molecraft.item;

import com.kaboomroads.molecraft.ModConstants;
import com.kaboomroads.molecraft.attribute.MolecraftAttribute;
import com.kaboomroads.molecraft.init.ModAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.TreeMap;

public class MolecraftItem extends Item {
    public final Rarity rarity;
    public final ItemType itemType;

    public MolecraftItem(Properties properties, Rarity rarity, ItemType itemType) {
        super(properties);
        this.rarity = rarity;
        this.itemType = itemType;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(CommonComponents.EMPTY);
        list.add(rarity.name.copy().append(" ").append(itemType.name));
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        return true;
    }

    public static Builder builder(float attackSpeed, EquipmentSlotGroup slotGroup, Properties properties, Rarity rarity, ItemType itemType) {
        return new Builder(attackSpeed, slotGroup, properties, rarity, itemType);
    }

    public static class Builder {
        private final TreeMap<AttributeStat, Double> attributes = new TreeMap<>();
        private final float attackSpeed;
        private final Properties properties;
        private final EquipmentSlotGroup slotGroup;
        private final Rarity rarity;
        private final ItemType itemType;

        public record AttributeStat(Holder<Attribute> attribute) implements Comparable<AttributeStat> {
            @Override
            public int compareTo(@NotNull MolecraftItem.Builder.AttributeStat attr) {
                return Integer.compare(((MolecraftAttribute) attribute.value()).ordinal, ((MolecraftAttribute) attr.attribute.value()).ordinal);
            }
        }

        public Builder(float attackSpeed, EquipmentSlotGroup slotGroup, Properties properties, Rarity rarity, ItemType itemType) {
            this.attackSpeed = attackSpeed;
            this.slotGroup = slotGroup;
            this.properties = properties;
            this.rarity = rarity;
            this.itemType = itemType;
        }

        public Builder maxHealth(double maxHealth) {
            attributes.put(new AttributeStat(ModAttributes.MAX_HEALTH), maxHealth);
            return this;
        }

        public Builder healthRegen(double healthRegen) {
            attributes.put(new AttributeStat(ModAttributes.HEALTH_REGEN), healthRegen);
            return this;
        }

        public Builder defense(double defense) {
            attributes.put(new AttributeStat(ModAttributes.DEFENSE), defense);
            return this;
        }

        public Builder damage(double damage) {
            attributes.put(new AttributeStat(ModAttributes.DAMAGE), damage);
            return this;
        }

        public Builder critDamage(double critDamage) {
            attributes.put(new AttributeStat(ModAttributes.CRIT_DAMAGE), critDamage);
            return this;
        }

        public Builder critChance(double critChance) {
            attributes.put(new AttributeStat(ModAttributes.CRIT_CHANCE), critChance);
            return this;
        }

        public Builder spellDamage(double spellDamage) {
            attributes.put(new AttributeStat(ModAttributes.SPELL_DAMAGE), spellDamage);
            return this;
        }

        public Builder maxMana(double maxMana) {
            attributes.put(new AttributeStat(ModAttributes.MAX_MANA), maxMana);
            return this;
        }

        public Builder manaRegen(double manaRegen) {
            attributes.put(new AttributeStat(ModAttributes.MANA_REGEN), manaRegen);
            return this;
        }

        public Builder breakingPower(double manaRegen) {
            attributes.put(new AttributeStat(ModAttributes.BREAKING_POWER), manaRegen);
            return this;
        }

        public Builder miningSpeed(double manaRegen) {
            attributes.put(new AttributeStat(ModAttributes.MINING_SPEED), manaRegen);
            return this;
        }

        public Builder miningFortune(double manaRegen) {
            attributes.put(new AttributeStat(ModAttributes.MINING_FORTUNE), manaRegen);
            return this;
        }

        public MolecraftItem build() {
            ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
            attributes.forEach((stat, value) -> {
                ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(ModConstants.MOD_ID, "base_" + ((MolecraftAttribute) stat.attribute.value()).name);
                builder.add(stat.attribute, new AttributeModifier(resourceLocation, value, AttributeModifier.Operation.ADD_VALUE), slotGroup);
            });
            return new MolecraftItem(properties.attributes(builder.add(
                    Attributes.ATTACK_SPEED,
                    new AttributeModifier(
                            Item.BASE_ATTACK_SPEED_ID,
                            attackSpeed,
                            AttributeModifier.Operation.ADD_VALUE
                    ),
                    EquipmentSlotGroup.MAINHAND
            ).build()), rarity, itemType);
        }
    }
}
