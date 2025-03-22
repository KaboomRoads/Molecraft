package com.kaboomroads.molecraft.init;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;

public class ModEntityDataSerializers {
    public static final EntityDataSerializer<Double> DOUBLE = register(EntityDataSerializer.forValueType(ByteBufCodecs.DOUBLE));

    public static <T> EntityDataSerializer<T> register(EntityDataSerializer<T> entityDataSerializer) {
        EntityDataSerializers.registerSerializer(entityDataSerializer);
        return entityDataSerializer;
    }

    public static void init() {
    }
}
