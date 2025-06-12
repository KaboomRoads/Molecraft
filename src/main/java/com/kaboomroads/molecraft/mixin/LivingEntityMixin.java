package com.kaboomroads.molecraft.mixin;

import com.kaboomroads.molecraft.entity.*;
import com.kaboomroads.molecraft.item.*;
import com.kaboomroads.molecraft.item.ability.core.Ability;
import com.kaboomroads.molecraft.item.ability.core.MolecraftEnchant;
import com.kaboomroads.molecraft.item.ability.core.When;
import com.kaboomroads.molecraft.mixinimpl.ModLivingEntity;
import com.kaboomroads.molecraft.util.MolecraftUtil;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements ModLivingEntity {
    @Shadow
    @Final
    private static EntityDataAccessor<Float> DATA_HEALTH_ID;

    @Shadow
    public abstract double getAttributeValue(Holder<Attribute> attribute);

    @Shadow
    public abstract void setHealth(float health);

    @Shadow
    public abstract float getHealth();

    @Unique
    private double molecraftHealth;
    @Unique
    private double oMolecraftHealth;

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Unique
    private final StatsMap stats = molecraft$initStats().build();
    @Unique
    private final AbilitiesMap abilities = new AbilitiesMap();

    @Override
    public StatsMap.Builder molecraft$initStats() {
        return new StatsMap.Builder()
                .stat(StatType.MAX_HEALTH, 100)
                .stat(StatType.HEALTH_REGEN, 5)
                .stat(StatType.DEFENSE, 0)
                .stat(StatType.DAMAGE, 5)
                .stat(StatType.CRIT_DAMAGE, 0)
                .stat(StatType.CRIT_CHANCE, 0)
                .stat(StatType.SPELL_DAMAGE, 0)
                .stat(StatType.MAX_MANA, 100)
                .stat(StatType.MANA_REGEN, 5)
                ;
    }

    @WrapMethod(method = "startUsingItem")
    private void wrap_startUsingItem(InteractionHand hand, Operation<Void> original) {
        if ((Object) this instanceof Player player && player.getAbilities().instabuild) original.call(hand);
    }

    @Inject(method = "collectEquipmentChanges", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;forEachModifier(Lnet/minecraft/world/entity/EquipmentSlot;Ljava/util/function/BiConsumer;)V", ordinal = 0))
    private void updateStats(CallbackInfoReturnable<Map<EquipmentSlot, ItemStack>> cir, @Local(ordinal = 0) EquipmentSlot equipmentSlot, @Local(ordinal = 0) ItemStack itemStack) {
        CustomData customData = itemStack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            MolecraftData molecraftData = MolecraftData.parse(customData.getUnsafe());
            if (molecraftData != null) {
                MolecraftItem molecraftItem = molecraftData.getItem();
                if (molecraftItem != null) {
                    EquipmentSlotGroup group = molecraftItem.activeFor;
                    if (group.test(equipmentSlot)) {
                        DataPropertyMap propertyMap = molecraftData.propertyMap();
                        TreeMap<MolecraftEnchant<?, ?>, Integer> enchantsForSlot = propertyMap.get(DataPropertyTypes.ENCHANTS);
                        TreeSet<Ability<?, ?, ?>> abilitiesForSlot = molecraftItem.abilities;
                        boolean enchantMatter = enchantsForSlot != null && !enchantsForSlot.isEmpty();
                        boolean abilityMatter = abilitiesForSlot != null && !abilitiesForSlot.isEmpty();
                        if (enchantMatter || abilityMatter) {
                            TreeMap<When, TreeSet<AbilitiesMap.AbilityInstance<?, ?, ?>>> whenMap = abilities.map.getOrDefault(equipmentSlot, new TreeMap<>());
                            whenMap.clear();
                            if (enchantMatter) for (Map.Entry<MolecraftEnchant<?, ?>, Integer> entry : enchantsForSlot.entrySet()) {
                                MolecraftEnchant<?, ?> enchant = entry.getKey();
                                int enchantLevel = entry.getValue();
                                TreeSet<AbilitiesMap.AbilityInstance<?, ?, ?>> enchantSet = whenMap.getOrDefault(enchant.when, new TreeSet<>());
                                enchantSet.add(new AbilitiesMap.EnchantInstance<>(enchant, enchantLevel));
                                whenMap.put(enchant.when, enchantSet);
                            }
                            if (abilityMatter) for (Ability<?, ?, ?> ability : abilitiesForSlot) {
                                TreeSet<AbilitiesMap.AbilityInstance<?, ?, ?>> abilitySet = whenMap.getOrDefault(ability.when, new TreeSet<>());
                                abilitySet.add(new AbilitiesMap.AbilityInstance<>(ability));
                                whenMap.put(ability.when, abilitySet);
                            }
                            if (!whenMap.isEmpty()) abilities.map.put(equipmentSlot, whenMap);
                            else abilities.map.remove(equipmentSlot);
                        }
                        for (Map.Entry<StatType, ItemStat> entry : molecraftItem.stats.entrySet()) {
                            StatType statType = entry.getKey();
                            ItemStat stat = entry.getValue();
                            StatInstance statInstance = stats.get(statType);
                            if (statInstance != null) statInstance.putModifier(equipmentSlot.getSerializedName(), stat.value(), stat.operation());
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "collectEquipmentChanges", at = @At(value = "TAIL"))
    private void addUpChanges(CallbackInfoReturnable<Map<EquipmentSlot, ItemStack>> cir) {
        for (StatInstance statInstance : stats.map.values()) statInstance.addUpModifiers();
    }

    @Inject(method = "stopLocationBasedEffects", at = @At(value = "HEAD"))
    private void stopLocationBasedStats(ItemStack stack, EquipmentSlot slot, AttributeMap attributeMap, CallbackInfo ci) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            MolecraftData molecraftData = MolecraftData.parse(customData.getUnsafe());
            if (molecraftData != null) {
                MolecraftItem molecraftItem = molecraftData.getItem();
                if (molecraftItem != null) {
                    EquipmentSlotGroup group = molecraftItem.activeFor;
                    if (group.test(slot)) {
                        for (StatType statType : molecraftItem.stats.keySet()) {
                            StatInstance statInstance = stats.get(statType);
                            if (statInstance != null) statInstance.removeModifier(slot.getSerializedName());
                        }
                        TreeMap<When, TreeSet<AbilitiesMap.AbilityInstance<?, ?, ?>>> whenMap = abilities.map.get(slot);
                        if (whenMap != null) whenMap.clear();
                    }
                }
            }
        }
    }

    @WrapMethod(method = "actuallyHurt")
    private void wrap_actuallyHurt(ServerLevel level, DamageSource damageSource, float amount, Operation<Void> original) {
        MolecraftUtil.actuallyHurt((LivingEntity) (Object) this, level, damageSource, amount);
    }

    @Inject(method = "setHealth", at = @At("TAIL"))
    private void inject_setHealth(float health, CallbackInfo ci) {
        double molecraftMaxHealth = stats.get(StatType.MAX_HEALTH).cachedValue;
        molecraftHealth = Mth.clamp(health / getAttributeValue(Attributes.MAX_HEALTH) * molecraftMaxHealth, 0.0, molecraftMaxHealth);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void inject_tick(CallbackInfo ci) {
        setHealth(getHealth());
        if (this instanceof UpdatingNamedEntity namedEntity && namedEntity.updateName() && molecraftHealth != oMolecraftHealth) {
            Component component = MolecraftUtil.getEntityNameTag((LivingEntity) (Object) this);
            setCustomName(component);
            setCustomNameVisible(true);
        }
        if (stuckTicks > 0) {
            stuckTicks--;
            setDeltaMovement(new Vec3(0.0, 0.0, 0.0));
            markHurt();
            ServerLevel level = (ServerLevel) level();
            AABB bb = getBoundingBox();
            Vec3 center = bb.getCenter();
            float horizontal = getBbWidth() * 0.25F;
            float vertical = getBbHeight() * 0.25F;
            level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, Blocks.MUD.defaultBlockState()), center.x, center.y, center.z, 1, horizontal, vertical, horizontal, 1.0);
        }
        oMolecraftHealth = molecraftHealth;
    }

    @Override
    public StatsMap molecraft$getStats() {
        return stats;
    }

    @Override
    public AbilitiesMap molecraft$getEnchants() {
        return abilities;
    }

    @Override
    public double molecraft$getHealth() {
        return molecraftHealth;
    }

    @Override
    public void molecraft$setHealth(double health) {
        double molecraftMaxHealth = stats.get(StatType.MAX_HEALTH).cachedValue;
        double maxHealth = getAttributeValue(Attributes.MAX_HEALTH);
        this.molecraftHealth = Mth.clamp(health, 0.0, molecraftMaxHealth);
        entityData.set(DATA_HEALTH_ID, Mth.clamp((float) (health / molecraftMaxHealth * maxHealth), 0.0F, (float) maxHealth));
    }

    @Unique
    private int stuckTicks;

    @Override
    public void molecraft$makeStuck(int ticks) {
        stuckTicks = ticks;
    }
}
