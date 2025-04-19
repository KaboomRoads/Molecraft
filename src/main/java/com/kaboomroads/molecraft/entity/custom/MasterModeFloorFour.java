package com.kaboomroads.molecraft.entity.custom;

import com.kaboomroads.molecraft.entity.PlayerEntity;
import com.kaboomroads.molecraft.entity.Skin;
import com.kaboomroads.molecraft.entity.StatType;
import com.kaboomroads.molecraft.entity.StatsMap;
import com.kaboomroads.molecraft.mixinimpl.ModLivingEntity;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MasterModeFloorFour extends Zombie implements PlayerEntity {
    public MasterModeFloorFour(Level level) {
        super(level);
        StatsMap map = ((ModLivingEntity) this).molecraft$getStats();
        map.setBase(StatType.MAX_HEALTH, 1000);
        map.setBase(StatType.HEALTH_REGEN, 0);
        map.setBase(StatType.DEFENSE, 100);
        map.setBase(StatType.DAMAGE, 100);
        map.setBase(StatType.CRIT_DAMAGE, 0);
        map.setBase(StatType.CRIT_CHANCE, 0);
        map.setBase(StatType.SPELL_DAMAGE, 0);
        map.setBase(StatType.MAX_MANA, 0);
        map.setBase(StatType.MANA_REGEN, 0);
    }

    @Override
    protected boolean isSunSensitive() {
        return false;
    }

    public static final Skin SKIN = new Skin(
            "ewogICJ0aW1lc3RhbXAiIDogMTY2MzE2Nzk3MzY4NiwKICAicHJvZmlsZUlkIiA6ICIzMzg2ZDZmZGYyZWY0ZGJjOGJiYTQ5NjVhNWUxMGI3NCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNdWNobXUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGRmMDM1MTIyNTZkY2Q0ODFhYjY2NDc5YWZmYjMzZjA2ZjE4MTYzOTk2MTU1NWRmNjU1NGEwZTQ2MTEyN2U2NiIKICAgIH0KICB9Cn0=",
            "crqzOB9kJxSztHh8AZ6IketTG2WkEnDF4fJg7Ww4vkGJJYyf/jxzbpgfx1N2S8wxBOBfwzvsNu+szF2zI1fTqNQaNzRwR0n8/B8NEXUSk2/7YWMnqKmDcrXu5iiay3UF7tHOesJXJ93W8YUMQcEhP5B2VrquPRPIgbzPfhz17QDz/SkLl/GrRm8HjkV82Uc7LuyMQmxmj2jNQ6PqK3SkmTz3P/6AyMWBzH0MpOnCpKQa1DgCuQI/TG3yBYxbsvaeg/3E4LAnW/BxGgp+J9ipRWmv3nRsEk6zfpD0MbYX458DgjpoMd2AxyscfAotyVjg4ynwusebGszhZSqR3ysSYhrUMbAh4XNneebRBbruBz2x9kyiAPet41PjDuipenVwin45JK9g4skCzuGsg7inOkBkae6CstlrxRd7LiLoqjYQGuOCNYQr/ryGpw6YdQfcrGv8CjilWR7P1SfTzBMdE3FvCU6AISoYqMBCvG4Ne4a4D7R7djES1c+cHk3b75eN1hmbcdMae9mPCEMu9VaNMZjhG20jOLuPmY0LuRSBW44TncOBtxXBfVkzcbxuYWBpf8OqCx/vA0czd2LfdcV213MZeZCbPYkkesznHmCvY/JtWCm+ueMCu10wUBboe5i9RvOEmUE96qeXfIVnxEFepL+fOEbNlQN6cVitlRWYFgo="
    );

    @Override
    public Skin getSkin() {
        return SKIN;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WITHER_SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.WITHER_SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WITHER_SKELETON_DEATH;
    }

    @NotNull
    @Override
    protected SoundEvent getStepSound() {
        return SoundEvents.WITHER_SKELETON_STEP;
    }
}
