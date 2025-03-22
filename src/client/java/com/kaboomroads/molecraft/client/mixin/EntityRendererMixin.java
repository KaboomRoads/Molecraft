package com.kaboomroads.molecraft.client.mixin;

import com.kaboomroads.molecraft.init.ModAttributes;
import com.kaboomroads.molecraft.mixinimpl.MolecraftLivingEntity;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin<T extends Entity, S extends EntityRenderState> {
    @WrapMethod(method = "getNameTag")
    private Component wrap_getNameTag(T entity, Operation<Component> original) {
        if (entity instanceof LivingEntity livingEntity) {
            NumberFormat format = NumberFormat.getCompactNumberInstance(Locale.US, NumberFormat.Style.SHORT);
            format.setRoundingMode(RoundingMode.DOWN);
            format.setMaximumFractionDigits(1);
            double health = ((MolecraftLivingEntity) livingEntity).molecraft$getHealth();
            double maxHealth = livingEntity.getAttributeValue(ModAttributes.MAX_HEALTH);
            return original.call(entity).copy().append(Component.literal(" " + format.format(health) + "/" + format.format(maxHealth)).withStyle(health <= maxHealth * 0.5 ? ChatFormatting.YELLOW : ChatFormatting.RED).append(Component.literal("❤").withStyle(ChatFormatting.RED)));
        } else return original.call(entity);
    }
}
