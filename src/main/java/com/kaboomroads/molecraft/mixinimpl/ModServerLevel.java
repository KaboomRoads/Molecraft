package com.kaboomroads.molecraft.mixinimpl;

import com.kaboomroads.molecraft.util.DelayedTask;
import net.minecraft.server.level.ServerLevel;

import java.util.PriorityQueue;
import java.util.function.Consumer;

public interface ModServerLevel {
    void molecraft$schedule(long delay, Consumer<ServerLevel> runnable);

    PriorityQueue<DelayedTask> molecraft$getScheduledTasks();
}
