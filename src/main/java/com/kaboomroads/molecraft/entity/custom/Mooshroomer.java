package com.kaboomroads.molecraft.entity.custom;

import com.kaboomroads.molecraft.entity.StatType;
import com.kaboomroads.molecraft.entity.StatsMap;
import com.kaboomroads.molecraft.entity.UpdatingNamedEntity;
import com.kaboomroads.molecraft.mixinimpl.ModLivingEntity;
import com.kaboomroads.molecraft.util.MolecraftUtil;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class Mooshroomer extends MushroomCow implements UpdatingNamedEntity {
    public Mooshroomer(Level level) {
        super(EntityType.MOOSHROOM, level);
        StatsMap map = ((ModLivingEntity) this).molecraft$getStats();
        map.setBase(StatType.MAX_HEALTH, 30);
        map.setBase(StatType.HEALTH_REGEN, 0);
        map.setBase(StatType.DEFENSE, 0);
        map.setBase(StatType.DAMAGE, 15);
        map.setBase(StatType.CRIT_DAMAGE, 0);
        map.setBase(StatType.CRIT_CHANCE, 0);
        map.setBase(StatType.SPELL_DAMAGE, 0);
        map.setBase(StatType.MAX_MANA, 0);
        map.setBase(StatType.MANA_REGEN, 0);
        setVariant(red() ? Variant.RED : Variant.BROWN);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 2.0, false) {
            @Override
            public void stop() {
                super.stop();
                Mooshroomer.this.setAggressive(false);
            }

            @Override
            public void start() {
                super.start();
                Mooshroomer.this.setAggressive(true);
            }
        });
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    @Override
    public double getAttributeValue(Holder<Attribute> attribute) {
        if (attribute.equals(Attributes.ATTACK_DAMAGE)) return 1.0;
        return getAttributes().getValue(attribute);
    }

    @Override
    protected void dropAllDeathLoot(ServerLevel level, DamageSource damageSource) {
        MolecraftUtil.dropEntityLootAndXp(this, level, damageSource, red() ? "red_shroomer" : "brown_shroomer");
    }

    public boolean red() {
        return uuid.hashCode() % 2 == 0;
    }

    @Override
    public boolean updateName() {
        return true;
    }
}
