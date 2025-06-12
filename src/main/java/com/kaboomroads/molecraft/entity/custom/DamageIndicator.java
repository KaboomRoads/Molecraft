package com.kaboomroads.molecraft.entity.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;

public class DamageIndicator extends ArmorStand {
    public long removeTime = Long.MIN_VALUE;

    public DamageIndicator(Level level) {
        super(EntityType.ARMOR_STAND, level);
        setCustomNameVisible(true);
        setNoGravity(true);
        setMarker(true);
        setInvisible(true);
        setInvulnerable(true);
    }

    @Override
    public void tick() {
        super.tick();
        if (level().getGameTime() >= removeTime) remove(RemovalReason.DISCARDED);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (removeTime != Long.MIN_VALUE) tag.putLong("remove_time", removeTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("remove_time", Tag.TAG_LONG)) removeTime = tag.getLong("remove_time");
    }
}
