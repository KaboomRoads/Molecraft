package com.kaboomroads.molecraft.mixin;

import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    @Inject(method = "placeNewPlayer", at = @At(value = "TAIL"))
    private void onJoin(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci) {
        player.connection.send(new ClientboundUpdateMobEffectPacket(player.getId(), new MobEffectInstance(MobEffects.DIG_SLOWDOWN, -1, 255, false, false, false), false));
        player.connection.send(new ClientboundUpdateMobEffectPacket(player.getId(), new MobEffectInstance(MobEffects.DIG_SPEED, -1, 0, false, false, false), false));
    }
}
