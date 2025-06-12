package com.kaboomroads.molecraft.entity.custom;

import com.kaboomroads.molecraft.mixinimpl.ModLivingEntity;
import com.kaboomroads.molecraft.util.MolecraftUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class HealthDisplay extends ArmorStand {
    @Nullable
    public UUID track;
    private Entity oTrack = null;
    private double oMolecraftHealth;
    
    public HealthDisplay(Level level) {
        super(EntityType.ARMOR_STAND, level);
        setNoGravity(true);
        setMarker(true);
        setInvisible(true);
        setInvulnerable(true);
    }

    @Override
    public void tick() {
        super.tick();
        Entity trackEntity = track != null ? ((ServerLevel) level()).getEntity(track) : null;
        if (trackEntity == null || trackEntity.isRemoved() || !(trackEntity instanceof LivingEntity livingEntity)) remove(RemovalReason.DISCARDED);
        else {
            double molecraftHealth = ((ModLivingEntity) livingEntity).molecraft$getHealth();
            if (molecraftHealth != oMolecraftHealth) {
                setCustomName(MolecraftUtil.getEntityNameTag(livingEntity));
                setCustomNameVisible(true);
                oMolecraftHealth = molecraftHealth;
            }
            if (trackEntity != oTrack) startRiding(trackEntity, true);
        }
        oTrack = trackEntity;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (track != null) tag.putUUID("tracked_entity", track);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        track = tag.contains("tracked_entity", Tag.TAG_INT_ARRAY) ? tag.getUUID("tracked_entity") : null;
    }
}
