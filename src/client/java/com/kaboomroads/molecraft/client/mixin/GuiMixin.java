package com.kaboomroads.molecraft.client.mixin;

import com.kaboomroads.molecraft.attribute.MolecraftAttribute;
import com.kaboomroads.molecraft.init.ModAttributes;
import com.kaboomroads.molecraft.mixinimpl.MolecraftLivingEntity;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Shadow
    private int overlayMessageTime;

    @Shadow
    private @Nullable Component overlayMessageString;

    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "renderOverlayMessage", at = @At("HEAD"))
    private void inject_renderOverlayMessage(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (minecraft.player != null) {
            MolecraftLivingEntity krtek = (MolecraftLivingEntity) minecraft.player;
            double health = krtek.molecraft$getHealth();
            MolecraftAttribute maxHealthAttribute = (MolecraftAttribute) ModAttributes.MAX_HEALTH.value();
            MolecraftAttribute defenseAttribute = (MolecraftAttribute) ModAttributes.DEFENSE.value();
            MolecraftAttribute maxManaAttribute = (MolecraftAttribute) ModAttributes.MAX_MANA.value();
            double maxHealth = minecraft.player.getAttributeValue(ModAttributes.MAX_HEALTH);
            double defense = minecraft.player.getAttributeValue(ModAttributes.DEFENSE);
            double mana = krtek.molecraft$getMana();
            double maxMana = minecraft.player.getAttributeValue(ModAttributes.MAX_MANA);
            overlayMessageString = maxHealthAttribute.format(Component.literal(((int) health) + "/" + ((int) maxHealth))).append("    ").append(defenseAttribute.format(Component.literal("" + (int) defense))).append("    ").append(maxManaAttribute.format(Component.literal(((int) mana) + "/" + ((int) maxMana))));
            overlayMessageTime = 1000;
        }
    }
}
