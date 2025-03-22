package com.kaboomroads.molecraft.mixin;

import com.kaboomroads.molecraft.init.ModAttributes;
import com.kaboomroads.molecraft.init.ModEntityDataSerializers;
import com.kaboomroads.molecraft.mixinimpl.MolecraftLivingEntity;
import com.kaboomroads.molecraft.util.MolecraftUtil;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.Holder;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements MolecraftLivingEntity {
    @Shadow
    @Final
    private static EntityDataAccessor<Float> DATA_HEALTH_ID;

    @Shadow
    public abstract double getAttributeValue(Holder<Attribute> attribute);

    @Shadow
    public abstract void setHealth(float health);

    @Shadow
    public abstract float getHealth();

    public LivingEntityMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Unique
    private static final EntityDataAccessor<Double> MOLECRAFT_HEALTH = SynchedEntityData.defineId(LivingEntity.class, ModEntityDataSerializers.DOUBLE);
    @Unique
    private static final EntityDataAccessor<Double> MANA = SynchedEntityData.defineId(LivingEntity.class, ModEntityDataSerializers.DOUBLE);

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void inject_defineSynchedData(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(MOLECRAFT_HEALTH, ModAttributes.MAX_HEALTH.value().getDefaultValue());
        builder.define(MANA, ModAttributes.MAX_MANA.value().getDefaultValue());
    }

    @ModifyReturnValue(method = "createLivingAttributes", at = @At("RETURN"))
    private static AttributeSupplier.Builder modify_addModAttributes(AttributeSupplier.Builder original) {
        return original
                .add(ModAttributes.MAX_HEALTH)
                .add(ModAttributes.HEALTH_REGEN)
                .add(ModAttributes.DEFENSE)
                .add(ModAttributes.DAMAGE)
                .add(ModAttributes.CRIT_DAMAGE)
                .add(ModAttributes.CRIT_CHANCE)
                .add(ModAttributes.SPELL_DAMAGE)
                .add(ModAttributes.MAX_MANA)
                .add(ModAttributes.MANA_REGEN)
                .add(ModAttributes.BREAKING_POWER)
                .add(ModAttributes.MINING_SPEED)
                .add(ModAttributes.MINING_FORTUNE)
                ;
    }

    @WrapMethod(method = "actuallyHurt")
    private void wrap_actuallyHurt(ServerLevel level, DamageSource damageSource, float amount, Operation<Void> original) {
        MolecraftUtil.dealDamage((LivingEntity) (Object) this, level, damageSource, amount);
    }

    @Inject(method = "setHealth", at = @At("TAIL"))
    private void inject_setHealth(float health, CallbackInfo ci) {
        double molecraftMaxHealth = getAttributeValue(ModAttributes.MAX_HEALTH);
        entityData.set(MOLECRAFT_HEALTH, Mth.clamp(health / getAttributeValue(Attributes.MAX_HEALTH) * molecraftMaxHealth, 0.0, molecraftMaxHealth));
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void inject_tick(CallbackInfo ci) {
        setHealth(getHealth());
    }

    @Override
    public double molecraft$getHealth() {
        return entityData.get(MOLECRAFT_HEALTH);
    }

    @Override
    public void molecraft$setHealth(double health) {
        double molecraftMaxHealth = getAttributeValue(ModAttributes.MAX_HEALTH);
        double maxHealth = getAttributeValue(Attributes.MAX_HEALTH);
        entityData.set(MOLECRAFT_HEALTH, Mth.clamp(health, 0.0, molecraftMaxHealth));
        entityData.set(DATA_HEALTH_ID, Mth.clamp((float) (health / molecraftMaxHealth * maxHealth), 0.0F, (float) maxHealth));
    }

    @Override
    public double molecraft$getMana() {
        return entityData.get(MANA);
    }

    @Override
    public void molecraft$setMana(double mana) {
        entityData.set(MANA, Mth.clamp(mana, 0.0, getAttributeValue(ModAttributes.MAX_MANA)));
    }
}
