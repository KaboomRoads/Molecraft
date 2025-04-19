package com.kaboomroads.molecraft.mixin;

import com.kaboomroads.molecraft.mixinimpl.ModServerLevel;
import com.kaboomroads.molecraft.util.DelayedTask;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.PriorityQueue;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin extends Level implements ModServerLevel {
    protected ServerLevelMixin(WritableLevelData levelData, ResourceKey<Level> dimension, RegistryAccess registryAccess, Holder<DimensionType> dimensionTypeRegistration, boolean isClientSide, boolean isDebug, long biomeZoomSeed, int maxChainedNeighborUpdates) {
        super(levelData, dimension, registryAccess, dimensionTypeRegistration, isClientSide, isDebug, biomeZoomSeed, maxChainedNeighborUpdates);
    }

    @Unique
    private final PriorityQueue<DelayedTask> scheduledTasks = new PriorityQueue<>();
    @Unique
    private long earliestExecutionTime = Long.MIN_VALUE;

    @Override
    public void molecraft$schedule(long delay, Consumer<ServerLevel> runnable) {
        scheduledTasks.offer(new DelayedTask(getGameTime() + delay, runnable));
        earliestExecutionTime = scheduledTasks.peek().executionTime();
    }

    @Override
    public PriorityQueue<DelayedTask> molecraft$getScheduledTasks() {
        return scheduledTasks;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void molecraftTick(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        long gameTime = getGameTime();
        if (gameTime >= earliestExecutionTime && !scheduledTasks.isEmpty()) {
            DelayedTask current = scheduledTasks.poll();
            while (true) {
                current.theBigScaryEvilTaskToExecuteWhenExecutionTimeIsReached().accept((ServerLevel) (Object) this);
                current = scheduledTasks.peek();
                if (current == null || current.executionTime() > gameTime) break;
                else scheduledTasks.poll();
            }
            DelayedTask task = scheduledTasks.peek();
            earliestExecutionTime = task != null ? task.executionTime() : Long.MIN_VALUE;
        }
        Scoreboard scoreboard = getScoreboard();
        Objective objective = scoreboard.getObjective("molecraft_sidebar");
        if (objective == null) {
            objective = scoreboard.addObjective(
                    "molecraft_sidebar",
                    ObjectiveCriteria.DUMMY,
                    Component.literal("MOLECRAFT").withStyle(ChatFormatting.AQUA, ChatFormatting.BOLD),
                    ObjectiveCriteria.RenderType.INTEGER,
                    false,
                    null
            );
            ScoreAccess scoreAccess = scoreboard.getOrCreatePlayerScore(() -> "coins", objective, false);
            scoreAccess.display(Component.literal("Coins: ").append(Component.literal("X").withStyle(ChatFormatting.GOLD, ChatFormatting.OBFUSCATED)));
            scoreAccess.set(0);
            scoreboard.setDisplayObjective(DisplaySlot.SIDEBAR, objective);
        }
    }
}
