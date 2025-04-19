package com.kaboomroads.molecraft.mixin;

import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class ServerGamePacketListenerImplMixin {
    @Inject(method = "handleSignUpdate", at = @At("HEAD"))
    private void molecraftSignUpdate(ServerboundSignUpdatePacket packet, CallbackInfo ci) {
        
    }
}
