package com.kaboomroads.molecraft.entity.custom;

import com.kaboomroads.molecraft.entity.PlayerEntity;
import com.kaboomroads.molecraft.entity.Skin;
import com.kaboomroads.molecraft.entity.StatType;
import com.kaboomroads.molecraft.entity.StatsMap;
import com.kaboomroads.molecraft.mixinimpl.ModLivingEntity;
import com.kaboomroads.molecraft.util.MolecraftUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;

import java.util.List;

public class LivingMud extends Zombie implements PlayerEntity {
    public boolean kaboom = false;

    public LivingMud(Level level) {
        super(level);
        StatsMap map = ((ModLivingEntity) this).molecraft$getStats();
        map.setBase(StatType.MAX_HEALTH, 40);
        map.setBase(StatType.HEALTH_REGEN, 0);
        map.setBase(StatType.DEFENSE, 0);
        map.setBase(StatType.DAMAGE, 20);
        map.setBase(StatType.CRIT_DAMAGE, 0);
        map.setBase(StatType.CRIT_CHANCE, 0);
        map.setBase(StatType.SPELL_DAMAGE, 0);
        map.setBase(StatType.MAX_MANA, 0);
        map.setBase(StatType.MANA_REGEN, 0);
    }

    public BlockState particleState() {
        return Blocks.PACKED_MUD.defaultBlockState();
    }

    @Override
    public void tick() {
        super.tick();
        BlockPos pos = blockPosition();
        ServerLevel level = (ServerLevel) level();
        BlockState blockState = level.getBlockState(pos);
        AABB bb = getBoundingBox();
        if (blockState.isSolidRender() && blockState.getCollisionShape(level, pos, CollisionContext.of(this)).toAabbs().stream().anyMatch(aabb -> aabb.move(pos).intersects(bb))) {
            setDeltaMovement(Vec3.ZERO);
            Vec3 newPos = position().add(0, 0.1, 0);
            setPos(newPos);
            BlockPos.MutableBlockPos particlePos = pos.mutable();
            for (int i = 0; i < 3; i++) {
                BlockState curState = level.getBlockState(particlePos);
                if (!curState.isSolidRender()) break;
                particlePos.move(Direction.UP);
            }
            level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, blockState), newPos.x, particlePos.getY(), newPos.z, 25, 0.1, 0.1, 0.1, 0);
        }
        ModLivingEntity krtek = (ModLivingEntity) this;
        StatsMap stats = krtek.molecraft$getStats();
        if (krtek.molecraft$getHealth() <= stats.get(StatType.MAX_HEALTH).cachedValue * 0.5) {
            if (!kaboom) {
//                setXRot(-90);
//                for (ServerPlayer player : level.players()) player.connection.send(ClientboundTeleportEntityPacket.teleport(getId(), PositionMoveRotation.of(this), Set.of(), onGround()));
//                autoSpinAttackTicks = 20;
//                setLivingEntityFlag(4, true);
                kaboom = true;
                kablooey();
            }
        }
    }

    public void kablooey() {
        ServerLevel level = (ServerLevel) level();
        Vec3 center = getBoundingBox().getCenter();
        level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, particleState()), center.x, center.y, center.z, 100, 0.25, 0.5, 0.25, 1.0);
        List<Entity> list = level.getEntities(this, getBoundingBox().inflate(5), entity -> EntitySelector.NO_SPECTATORS.test(entity) && !(entity instanceof LivingMud));
        if (!list.isEmpty()) for (Entity entity : list)
            if (entity instanceof LivingEntity livingEntity && !livingEntity.isInvulnerable()) {
                livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 255, false, false, false));
                ((ModLivingEntity) livingEntity).molecraft$makeStuck(20);
                double distance = livingEntity.position().distanceTo(center);
                Vec3 delta = livingEntity.position().subtract(center).normalize().scale(0.5);
                Vector3d pos = new Vector3d(center.x, center.y, center.z);
                for (int i = 0; i < (int) (distance * 2); i++) {
                    level.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, particleState()), pos.x, pos.y, pos.z, 10, 0, 0, 0, 0.0);
                    pos.add(delta.x, delta.y, delta.z);
                }
            }
    }

//    @Override
//    protected void checkAutoSpinAttack(AABB boundingBoxBeforeSpin, AABB boundingBoxAfterSpin) {
//        if (autoSpinAttackTicks <= 0) setLivingEntityFlag(4, false);
//        else {
//            setXRot(-90);
//            List<Entity> list = level().getEntities(this, getBoundingBox().inflate(1));
//            if (!list.isEmpty()) for (Entity entity : list) if (entity instanceof LivingEntity) doHurtTarget((ServerLevel) level(), entity);
//        }
//    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("kaboom", kaboom);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("kaboom", Tag.TAG_BYTE)) kaboom = tag.getBoolean("kaboom");
    }

    @Override
    public boolean isInvulnerableTo(ServerLevel level, DamageSource damageSource) {
        return damageSource.is(DamageTypes.IN_WALL) || super.isInvulnerableTo(level, damageSource);
    }

    @Override
    protected void dropAllDeathLoot(ServerLevel level, DamageSource damageSource) {
        MolecraftUtil.dropEntityLootAndXp(this, level, damageSource, "living_mud");
    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    public static final Skin SKIN = new Skin(
            "ewogICJ0aW1lc3RhbXAiIDogMTc0NTYwMDY1MzQzNSwKICAicHJvZmlsZUlkIiA6ICIzMzg2ZDZmZGYyZWY0ZGJjOGJiYTQ5NjVhNWUxMGI3NCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNdWNobXUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNThjNGNjNzk0ZmE4YzkyZmVjM2U3MGI3MTM5NWM4M2YyZWJlNmU1ZTUyOGM1N2U4MjE5NTI4NWNkODgwZDdjNyIKICAgIH0KICB9Cn0=",
            "LrRa2BXjgTMoXtRSZGbsASizzh+ftlNJY8noaUA0zp5Ylo1My7y74dcYBNXWFyRrGIDn201A9Z/AkA8Mu/7BbQXoOuq7KvzBIzud9SZYi9O9u1iNYShIsTWCv4LhM0kauhuAXdbG0BkiMtDU4OPbG+ZtNcpD1XOz1Zy0D5nEaWXlFARUDlpstI53LP18lxZwbRykPoaAY0dc0GAMntQrzOcf5m1hEWfcPvd++3AXIWTtD4Rbq2r0NIuGyAmnSqB+31Av985wvdFlF+MEtvLSsk+jPLCXMut8urrw139/nGXaoJg1AWZEnieKwh8Mng/OYUZgzEjZMO3saCh1u6gnwMosRVFcJzXUZyA7bYNYOyqAt6TJmFmsgBRfUCYV1w+RsvdLWxg0fkOH6uNGSCwVD+wKtMEe6565/GseU/sT2N6z6JGjRicYzJ/VTT3jrzSe4e6oSKGpcUic6trP+LL72I0qAiWbEpt2AWXSWHrXqe+AkK3ygzeNeHs7spuTKbZcL8CMnqcWKQondZRgA52fqxTpWgy+j0nhi4i7tr2PsTwoAkYDv1tA0A0HF5nA9C3oviABdqntCpL5nlAqaCmQzefj6L0Gsve5RyGRRlID+oHH+o05S03FqmPa4rXTsdvgdfk7X9zhw4cy6iN//CvTehpbQiUcqAdypX/kerpvXGs="
    );

    @Override
    public Skin getSkin() {
        return SKIN;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.MAGMA_CUBE_SQUISH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.MAGMA_CUBE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.MAGMA_CUBE_DEATH;
    }

    @NotNull
    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.MAGMA_CUBE_SQUISH_SMALL;
    }
}
