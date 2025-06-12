package com.kaboomroads.molecraft.entity.custom;

import com.kaboomroads.molecraft.entity.MolecraftEntities;
import com.kaboomroads.molecraft.entity.MolecraftEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class Spawner extends ArmorStand {
    public String spawnId = null;
    public int interval = 20;
    public HashSet<Vec3> positions = new HashSet<>();
    public HashMap<Vec3, UUID> situation = new HashMap<>();

    public Spawner(Level level) {
        super(EntityType.ARMOR_STAND, level);
        setNoGravity(true);
        setMarker(true);
        setInvisible(true);
        setInvulnerable(true);
    }

    @Override
    public void tick() {
        if (spawnId != null && tickCount % interval == 0) {
            MolecraftEntity molecraftEntity = MolecraftEntities.ENTITIES.get(spawnId);
            for (Vec3 offset : positions) {
                if (mysteriouslyGoneOrRemovedOrMaybeAvailable(offset)) {
                    Entity entity = molecraftEntity.construct(level());
                    entity.setPos(position().add(offset));
                    entity.setYRot(random.nextFloat() * 360.0F);
                    level().addFreshEntity(entity);
                    situation.put(offset, entity.getUUID());
                }
            }
        }
    }

    public boolean mysteriouslyGoneOrRemovedOrMaybeAvailable(Vec3 offset) {
        if (!situation.containsKey(offset)) return true;
        Entity entity = ((ServerLevel) level()).getEntity(situation.get(offset));
        if (entity == null) return true;
        return entity.isRemoved();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (spawnId != null) tag.putString("spawn_id", spawnId);
        tag.putInt("interval", interval);
        ListTag positionsTag = new ListTag();
        for (Vec3 pos : positions) {
            CompoundTag posTag = new CompoundTag();
            posTag.putDouble("x", pos.x);
            posTag.putDouble("y", pos.y);
            posTag.putDouble("z", pos.z);
            positionsTag.add(posTag);
        }
        ListTag situationTag = new ListTag();
        for (Map.Entry<Vec3, UUID> entry : situation.entrySet()) {
            Vec3 pos = entry.getKey();
            UUID uuid = entry.getValue();
            CompoundTag entryTag = new CompoundTag();
            CompoundTag posTag = new CompoundTag();
            posTag.putDouble("x", pos.x);
            posTag.putDouble("y", pos.y);
            posTag.putDouble("z", pos.z);
            entryTag.put("pos", posTag);
            entryTag.putUUID("uuid", uuid);
            situationTag.add(entryTag);
        }
        tag.put("situation", situationTag);
        tag.put("spawn_offsets", positionsTag);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        spawnId = tag.getString("spawn_id");
        int loadedInterval = tag.getInt("interval");
        if (loadedInterval > 0) interval = loadedInterval;
        ListTag positionsTag = tag.getList("spawn_offsets", Tag.TAG_COMPOUND);
        positions.clear();
        for (Tag t : positionsTag) {
            CompoundTag posTag = (CompoundTag) t;
            Vec3 pos = new Vec3(posTag.getDouble("x"), posTag.getDouble("y"), posTag.getDouble("z"));
            positions.add(pos);
        }
        ListTag situationTag = tag.getList("situation", Tag.TAG_COMPOUND);
        situation.clear();
        for (Tag t : situationTag) {
            CompoundTag entryTag = (CompoundTag) t;
            CompoundTag posTag = entryTag.getCompound("pos");
            Vec3 pos = new Vec3(posTag.getDouble("x"), posTag.getDouble("y"), posTag.getDouble("z"));
            if (positions.contains(pos)) {
                UUID uuid = entryTag.getUUID("uuid");
                situation.put(pos, uuid);
            }
        }
    }
}
