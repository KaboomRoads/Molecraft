package com.kaboomroads.molecraft.util;

import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public record DelayedTask(long executionTime, Consumer<ServerLevel> theBigScaryEvilTaskToExecuteWhenExecutionTimeIsReached) implements Comparable<DelayedTask> {
    @Override
    public int compareTo(@NotNull DelayedTask delayedTask) {
        return Long.compare(executionTime, delayedTask.executionTime);
    }
}
