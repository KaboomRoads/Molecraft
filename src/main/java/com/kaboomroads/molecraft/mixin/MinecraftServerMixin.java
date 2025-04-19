package com.kaboomroads.molecraft.mixin;

import com.kaboomroads.molecraft.mixinimpl.ModServerLevel;
import com.kaboomroads.molecraft.util.DelayedTask;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.PriorityQueue;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    @Shadow
    @Final
    private Map<ResourceKey<Level>, ServerLevel> levels;

    @Inject(method = "stopServer", at = @At("HEAD"))
    private void completeLevelTasks(CallbackInfo ci) {
        for (ServerLevel level : levels.values()) {
            PriorityQueue<DelayedTask> scheduledTasks = ((ModServerLevel) level).molecraft$getScheduledTasks();
            for (DelayedTask task : scheduledTasks) task.theBigScaryEvilTaskToExecuteWhenExecutionTimeIsReached().accept(level);
            scheduledTasks.clear();
        }
    }
}
