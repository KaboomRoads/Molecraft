package com.kaboomroads.molecraft.entity;

import java.util.function.BiConsumer;

public record StatModifier(String id, double amount, Operation operation) {
    public void modify(StatInstance statInstance) {
        operation.operation.accept(statInstance, amount);
    }

    public enum Operation {
        MULTIPLY_BASE("multiply_base", (instance, value) -> instance.cachedValue *= value),
        ADD("add", (instance, value) -> instance.cachedValue += value),
        MULTIPLY("multiply", (instance, value) -> instance.cachedValue *= value);

        public final String id;
        public final BiConsumer<StatInstance, Double> operation;

        Operation(String id, BiConsumer<StatInstance, Double> operation) {
            this.id = id;
            this.operation = operation;
        }
    }
}
