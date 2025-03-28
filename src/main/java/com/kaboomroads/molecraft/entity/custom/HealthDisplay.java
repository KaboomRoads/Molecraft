package com.kaboomroads.molecraft.entity.custom;

import com.kaboomroads.molecraft.util.MolecraftUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class HealthDisplay extends ArmorStand {
    @Nullable
    public UUID track;

    public HealthDisplay(Level level) {
        super(EntityType.ARMOR_STAND, level);
        setCustomNameVisible(true);
        setNoGravity(true);
        setMarker(true);
        setInvisible(true);
    }

    @Override
    public void tick() {
        super.tick();
        Entity trackEntity = track != null ? ((ServerLevel) level()).getEntity(track) : null;
        if (trackEntity == null || trackEntity.isRemoved() || !(trackEntity instanceof LivingEntity livingEntity)) remove(RemovalReason.DISCARDED);
        else {
            if (tickCount % 5 == 0) setCustomName(MolecraftUtil.getEntityNameTag(livingEntity));
            setPos(new Vec3(trackEntity.position().x, trackEntity.getBoundingBox().maxY, trackEntity.position().z));
            setDeltaMovement(trackEntity.getDeltaMovement());
        }
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
