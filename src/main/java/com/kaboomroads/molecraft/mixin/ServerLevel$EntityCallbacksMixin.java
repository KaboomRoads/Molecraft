package com.kaboomroads.molecraft.mixin;

import com.kaboomroads.molecraft.entity.MolecraftEntities;
import com.kaboomroads.molecraft.entity.MolecraftEntity;
import com.kaboomroads.molecraft.entity.custom.HealthDisplay;
import com.kaboomroads.molecraft.mixinimpl.ModEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerLevel.EntityCallbacks.class)
public class ServerLevel$EntityCallbacksMixin {
    @Inject(method = "onCreated(Lnet/minecraft/world/entity/Entity;)V", at = @At("HEAD"))
    private void inject_onCreated(Entity entity, CallbackInfo ci) {
        if (entity instanceof LivingEntity livingEntity) {
            String id = ((ModEntity) entity).molecraft$getId();
            if (id != null) {
                MolecraftEntity molecraftEntity = MolecraftEntities.ENTITIES.get(id);
                if (molecraftEntity != null && molecraftEntity.customNameTag) {
                    HealthDisplay healthDisplay = (HealthDisplay) MolecraftEntities.HEALTH_DISPLAY.construct(livingEntity.level());
                    healthDisplay.track = livingEntity.getUUID();
                    livingEntity.level().addFreshEntity(healthDisplay);
                }
            }
        }
    }
}
