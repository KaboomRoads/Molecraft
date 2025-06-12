package com.kaboomroads.molecraft.entity.custom;

import com.kaboomroads.molecraft.entity.StatType;
import com.kaboomroads.molecraft.entity.StatsMap;
import com.kaboomroads.molecraft.util.ItemUtils;
import com.kaboomroads.molecraft.util.MolecraftUtil;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.TreeMap;

public class BoomShroomer extends Display.ItemDisplay {
    public static final ItemStack RED = ItemUtils.createPlayerHeadFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmIxYmQ2N2FjNTcwMzc0MjgzNzgyZTc2OGFiZTk2NTllYzg3MmJhZDViMjM5NmNjY2I1YWVkNGJmYzQ3MzcwMiJ9fX0=");
    public static final ItemStack BROWN = ItemUtils.createPlayerHeadFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWE3M2Q3N2ZjZjJiYzZiMGVhZWI5MjM0ZTNiMTU0OTk2ZWUwZDllNjVlZGFmMjJkYTY1ODQ4NDM2OGNiZTc2NSJ9fX0=");
    private boolean red;
    private int timer = 0;
    private boolean kaboom = false;

    public BoomShroomer(Level level) {
        super(EntityType.ITEM_DISPLAY, level);
        entityData.set(DATA_SCALE_ID, new Vector3f(1.0F));
        entityData.set(DATA_TRANSLATION_ID, new Vector3f(0.0F, 0.5F, 0.0F));
    }

    public void setRed(boolean red) {
        this.red = red;
        setItemStack(red ? RED : BROWN);
    }

    @Override
    public void tick() {
        super.tick();
        setTransformationInterpolationDelay(0);
        setTransformationInterpolationDuration(2);
        setPosRotInterpolationDuration(2);
        entityData.set(DATA_SCALE_ID, new Vector3f(timer * 0.05F + 1.0F + 0.1F * (timer % 2 == 0 ? -1 : 1)));
        entityData.set(DATA_TRANSLATION_ID, new Vector3f(0, (timer * 0.05F + 1.0F + 0.1F * (timer % 2 == 0 ? -1 : 1)) * 0.5F, 0));
        ServerLevel level = (ServerLevel) level();
        Vec3 pos = position();
        level.sendParticles(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, 10, 0.0, 0.25, 0.0, 0.0);
        if (timer++ >= 20 && !kaboom) {
            remove(RemovalReason.DISCARDED);
            kaboom = true;
            level.sendParticles(ParticleTypes.SMOKE, pos.x, pos.y + 0.5, pos.z, 100, 0.0, 0.0, 0.0, 1.0);
            level.sendParticles(ParticleTypes.EXPLOSION, pos.x, pos.y + 0.5, pos.z, 1, 0.0, 0.0, 0.0, 1.0);
            level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.0F, 1.0F);
            for (Player player : level.getEntitiesOfClass(Player.class, getBoundingBox().inflate(3))) {
                StatsMap stats = new StatsMap(new TreeMap<>());
                stats.setBase(StatType.DAMAGE, 50);
                stats.setBase(StatType.CRIT_DAMAGE, 0);
                stats.setBase(StatType.CRIT_CHANCE, 0);
                DamageSource damageSource = level.damageSources().explosion(this, this);
                MolecraftUtil.molecraftHurtServer(player, stats, level, damageSource);
                player.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0));
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("red", red);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        setRed(tag.getBoolean("red"));
    }
}
