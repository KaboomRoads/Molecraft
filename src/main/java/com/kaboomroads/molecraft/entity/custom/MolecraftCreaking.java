package com.kaboomroads.molecraft.entity.custom;

import com.kaboomroads.molecraft.entity.StatType;
import com.kaboomroads.molecraft.entity.StatsMap;
import com.kaboomroads.molecraft.entity.UpdatingNamedEntity;
import com.kaboomroads.molecraft.mixinimpl.ModLivingEntity;
import com.kaboomroads.molecraft.util.MolecraftUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.creaking.Creaking;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class MolecraftCreaking extends Creaking implements UpdatingNamedEntity {
    public MolecraftCreaking(Level level) {
        super(EntityType.CREAKING, level);
        StatsMap map = ((ModLivingEntity) this).molecraft$getStats();
        map.hits = true;
        map.setBase(StatType.MAX_HEALTH, 5);
        map.setBase(StatType.HEALTH_REGEN, 0);
        map.setBase(StatType.DEFENSE, 0);
        map.setBase(StatType.DAMAGE, 50);
        map.setBase(StatType.CRIT_DAMAGE, 0);
        map.setBase(StatType.CRIT_CHANCE, 0);
        map.setBase(StatType.SPELL_DAMAGE, 0);
        map.setBase(StatType.MAX_MANA, 0);
        map.setBase(StatType.MANA_REGEN, 0);
    }

    @Override
    protected void dropAllDeathLoot(ServerLevel level, DamageSource damageSource) {
        MolecraftUtil.dropEntityLootAndXp(this, level, damageSource, "creaking");
    }

    @NotNull
    @Override
    protected AABB getAttackBoundingBox() {
        return super.getAttackBoundingBox().inflate(-0.25, 0, -0.25);
    }

    @Override
    public boolean isLookingAtMe(LivingEntity entity, double tolerance, boolean scaleByDistance, boolean visual, double... yValues) {
        Vec3 vec3 = entity.getViewVector(1.0F).normalize();
        return !super.isLookingAtMe(entity, tolerance, scaleByDistance, visual, yValues) && !getBoundingBox().clip(entity.getEyePosition(), entity.getEyePosition().add(vec3.scale(10))).isPresent();
    }

    @Override
    public void knockback(double strength, double x, double z) {
    }

    @Override
    public BlockPos getHomePos() {
        return null;
    }

    @Override
    public boolean updateName() {
        return true;
    }
}
