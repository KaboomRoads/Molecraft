package com.kaboomroads.molecraft.mixin;

import com.kaboomroads.molecraft.entity.AbilitiesMap;
import com.kaboomroads.molecraft.item.ability.core.PostHitWhenContext;
import com.kaboomroads.molecraft.item.ability.core.WhenType;
import com.kaboomroads.molecraft.mixinimpl.ModLivingEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.TreeSet;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
    @Inject(method = "doPostAttackEffects", at = @At("HEAD"))
    private static void postHit(ServerLevel level, Entity hit, DamageSource damageSource, CallbackInfo ci) {
        Entity attacker = damageSource.getEntity();
        if (hit instanceof LivingEntity && attacker != null) {
            TreeSet<AbilitiesMap.AbilityInstance<?, ?, ?>> map = ((ModLivingEntity) attacker).molecraft$getEnchants().get(WhenType.POST_HIT);
            if (map != null) for (AbilitiesMap.AbilityInstance<?, ?, ?> entry : map)
                ((AbilitiesMap.AbilityInstance<?, PostHitWhenContext, ?>) entry).run(new PostHitWhenContext(attacker, hit));
        }
    }
}
