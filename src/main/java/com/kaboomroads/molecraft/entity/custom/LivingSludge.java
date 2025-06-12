package com.kaboomroads.molecraft.entity.custom;

import com.kaboomroads.molecraft.entity.Skin;
import com.kaboomroads.molecraft.entity.StatType;
import com.kaboomroads.molecraft.entity.StatsMap;
import com.kaboomroads.molecraft.mixinimpl.ModLivingEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class LivingSludge extends LivingMud {
    public LivingSludge(Level level) {
        super(level);
        StatsMap map = ((ModLivingEntity) this).molecraft$getStats();
        map.setBase(StatType.MAX_HEALTH, 60);
        map.setBase(StatType.HEALTH_REGEN, 0);
        map.setBase(StatType.DEFENSE, 0);
        map.setBase(StatType.DAMAGE, 25);
        map.setBase(StatType.CRIT_DAMAGE, 0);
        map.setBase(StatType.CRIT_CHANCE, 0);
        map.setBase(StatType.SPELL_DAMAGE, 0);
        map.setBase(StatType.MAX_MANA, 0);
        map.setBase(StatType.MANA_REGEN, 0);
    }

    @Override
    public BlockState particleState() {
        return Blocks.MUD.defaultBlockState();
    }

    @Override
    public boolean doHurtTarget(ServerLevel level, Entity target) {
        if (target instanceof LivingEntity livingEntity) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 255, false, false, false));
            ((ModLivingEntity) livingEntity).molecraft$makeStuck(10);
        }
        return super.doHurtTarget(level, target);
    }

    public static final Skin SKIN = new Skin(
            "ewogICJ0aW1lc3RhbXAiIDogMTc0NTc3MDc1MTU3MSwKICAicHJvZmlsZUlkIiA6ICIzMzg2ZDZmZGYyZWY0ZGJjOGJiYTQ5NjVhNWUxMGI3NCIsCiAgInByb2ZpbGVOYW1lIiA6ICJNdWNobXUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzI5NzEyZjU4ZjM2NGE2NWQzZDk5NGNhY2FjM2VlN2MzNjJlOWU1MmFhYWVkZWZjZDI3ODZmNjI4YTRiNWM1YiIKICAgIH0KICB9Cn0=",
            "NBde2k5YJyBJuQOdjR9Xye6nU4NrNfesWK2nrq8S+6HEnbLgYcbSD4kw+WhgCnGQ/G/RGZwIJlMzn4zkRvoNbG/Eyvm8NUFQTuX/wBcqh0k/0D2LoiXCRRhdwIg7Tk36C+F7iuPk6XI17uKHi60BesZtaYPDwUKLfbId93nFBTwWzJEEtN2i+H2xWOQEU/YLBDGV3FsYtHCWlqStNFBOIzQuHqgs4a1WuFrB3TjzSktjXOe4OYVL/rUtO+YlA/2ezyhs9wmcDLQTcax9tP3wrGQjulc7WUGjB8uQIGZ4h+nwrK9Hn10BKo2osCVf3DK7JNcZWIGhNd+IRxLrUpnb7TH9S+xHPsTNeTaU8FdaYAYjHM0oMnC5u4g7nAKL2YODwqpNwihikYNuubC0HjatRvqCHetpGdof/M0UyuTdcZeHJd6yzf7+jMO+jlhOvIt8kb9Sy5fO5vizBT+Cr+uv4UXH6FlLUFzXvT/kNDYw6uUyqwLJ2c66sA6zSVMuLFR7U6P80uEjG1XVHMnox9gG/k3B2KHyO2PNcCNxQQZ/UpkWTDsbp7rm1B57EIlMn/EtexMPS3Zv/p7B6zqmhF/HMyALdC/hZE6uPZJQOC4ZUd0JGH5egOBLp9phaKlVqd83mojRAJPtGgsEaPaWJrirZPlN5UJ2dlEosuTtVLrhteY="
    );

    @Override
    public Skin getSkin() {
        return SKIN;
    }
}
