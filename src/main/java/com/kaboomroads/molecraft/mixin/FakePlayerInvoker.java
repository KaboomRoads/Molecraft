package com.kaboomroads.molecraft.mixin;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(FakePlayer.class)
public interface FakePlayerInvoker {
    @Invoker("<init>")
    static FakePlayer init(ServerLevel world, GameProfile profile) {
        throw new IllegalStateException();
    }
}
