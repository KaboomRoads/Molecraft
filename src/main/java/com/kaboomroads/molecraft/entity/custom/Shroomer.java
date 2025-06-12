package com.kaboomroads.molecraft.entity.custom;

import com.kaboomroads.molecraft.entity.*;
import com.kaboomroads.molecraft.mixinimpl.ModLivingEntity;
import com.kaboomroads.molecraft.util.MolecraftUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class Shroomer extends Zombie implements PlayerEntity {
    public Shroomer(Level level) {
        super(level);
        StatsMap map = ((ModLivingEntity) this).molecraft$getStats();
        map.setBase(StatType.MAX_HEALTH, 60);
        map.setBase(StatType.HEALTH_REGEN, 0);
        map.setBase(StatType.DEFENSE, 0);
        map.setBase(StatType.DAMAGE, 20);
        map.setBase(StatType.CRIT_DAMAGE, 0);
        map.setBase(StatType.CRIT_CHANCE, 0);
        map.setBase(StatType.SPELL_DAMAGE, 0);
        map.setBase(StatType.MAX_MANA, 0);
        map.setBase(StatType.MANA_REGEN, 0);
    }

    @Override
    protected void dropAllDeathLoot(ServerLevel level, DamageSource damageSource) {
        MolecraftUtil.dropEntityLootAndXp(this, level, damageSource, red() ? "red_shroomer" : "brown_shroomer");
    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    public static final Skin RED = new Skin(
            "ewogICJ0aW1lc3RhbXAiIDogMTc0NTc1OTU2NDgzNSwKICAicHJvZmlsZUlkIiA6ICIzMzg2ZDZmZGYyZWY0ZGJjOGJiYTQ5NjVhNWUxMGI3NCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNdWNobXUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmIxYmQ2N2FjNTcwMzc0MjgzNzgyZTc2OGFiZTk2NTllYzg3MmJhZDViMjM5NmNjY2I1YWVkNGJmYzQ3MzcwMiIKICAgIH0KICB9Cn0=",
            "obHZ09hB4lAom34F1aORvOxg4tqNK3Qd4w2R7qEBx50JSDOQZhZdPmynXA98JRNRG6816rB7oozDYsOhoOQ01f1FrMdf4cmNYqez52tHt5iS4FSqQ/1NYlIAtmhL2/BiX5WOtkrn/5ySy+7n6o3SNqdJTSnclOAFbgnN0GPog7dX0/oLGo3XDXn4aQy3Fjk3ib8gnI7QttabJAWSrANJWH6wKACkFJdPzS/m8D8aSO306is/aj2OUbH3IAWZgeMFiwuhTsDP2RfNJN0bZvPuacYs2/6aZpDtwFD4yhdRytq89k68FoQF1Vjo2MOzjG4WGr2dFW1oK+x4YpDQElFj10tAaPbegPoW8QokArZvUHRMPi3lCkXNQgDedb0+qdX61dSWJrixVrILMaHNvh74wzcyav/vqnNmTcze82xmzaT1voqt4+SL14+X40rlHj9aoPx5gUnloU8IQ5WDAz5t9RDWCVESdbLfMX5cM+iw4txGqcrWZzolHipginrS2aesA4bmq94ZZ25t5TWZNe1CtPB4dSyLpIHPdHp2Og0pRD2Bx5hJcziRu0vWZwCQw9KE7XSFinv9LCB8r2zpnO1pjEmgdbK2JOHtHcRjesh9uNGQPPT32HIf+VP/hy0akeZEyi/CetfsH61F9ubQX7bn9DLqgNimqlJ8r9s4lwNM1YE="
    );
    public static final Skin BROWN = new Skin(
            "ewogICJ0aW1lc3RhbXAiIDogMTc0NTc1OTU5NTUwOSwKICAicHJvZmlsZUlkIiA6ICIzMzg2ZDZmZGYyZWY0ZGJjOGJiYTQ5NjVhNWUxMGI3NCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNdWNobXUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWE3M2Q3N2ZjZjJiYzZiMGVhZWI5MjM0ZTNiMTU0OTk2ZWUwZDllNjVlZGFmMjJkYTY1ODQ4NDM2OGNiZTc2NSIKICAgIH0KICB9Cn0=",
            "UuMFaEsFeRnds9S3a8qDnAEMpT415PXcGBlC0JKATpGsvximPzAwNmRtuLFq2AKZ5deegtmCZC0k2RMEWhE+n9LypT9UruTQl3dVwUIpEDjSdmUW2kcsqRUwgEZdyr2wOBxpW0B5Sl/lsp37o98SRadEtOJ3ZNghCohP/hckidl2O7KuvylLoDReuVBb7WKdjG8VWBEOivltgtpubJZu6IU8hKEyAOYUYY2d3AZIZYNep9ANLb9LhBOJFBKgrL+S8+BSAEfO66fNvv/EAsdn+KHuISIFtlmCVzQIVA9YZ/nbr+o7/IK2/NnFl9XxPB8ZiOvrFaKNclJtDx/NWwVqZYLvqx2emaTAUszSs+M7IROyqnqANP9b9HgOlINGU7lj8O1WGqUnwH751Sm4u7z/kzhjHQoGYhW+lLisnKMuc9gGbZKAy0hB3eiqRQuZQ8VxV5LNHE4sguC38UADpIzIrwJnD/Gel4qZWJvIYBsc0Q8SoFNqFE5B56KEnnMBASULFEwKj7LQ+HWGRfOhNkKCR+uNcMPbO5HA75E8nlDxUt9tHAcnVJtuXPhXZ6NfZZV5XHkT4jJ4UVifzQXM6aLLMQaCcZNxwRwYTChGkxZhUTtduhYNs7QF+fG2pUD/ntsf2R5h6nYMugbCMPdYQc3NYii/ujRrTaOP3Kw6ix69UqU="
    );

    @Override
    public void die(DamageSource damageSource) {
        super.die(damageSource);
        remove(RemovalReason.KILLED);
        BoomShroomer boomShroomer = (BoomShroomer) MolecraftEntities.BOOM_SHROOMER.construct(level());
        boomShroomer.setRed(red());
        boomShroomer.setPos(getEyePosition().add(0, -0.375, 0));
        boomShroomer.absRotateTo(getYHeadRot(), getXRot());
        level().addFreshEntity(boomShroomer);
    }

    public boolean red() {
        return uuid.hashCode() % 2 == 0;
    }

    @Override
    public Skin getSkin() {
        return red() ? RED : BROWN;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WOOD_STEP;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.WOOD_HIT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WOOD_BREAK;
    }

    @NotNull
    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.WOOD_STEP;
    }
}
