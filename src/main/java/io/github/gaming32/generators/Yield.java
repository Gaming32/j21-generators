package io.github.gaming32.generators;

import io.github.gaming32.generators.internal.Accessors;

public class Yield {
    public static void yield_(Object value) {
        final GeneratorImpl<?> generator = Generators.GENERATORS.get(Accessors.getCurrentContinuation(Generators.SCOPE));
        if (generator == null) {
            throw new IllegalStateException("Current generator not in generator map?");
        }
        generator.state = GeneratorImpl.GeneratorState.READY;
        generator.next = value;
        Accessors.yieldContinuation(Generators.SCOPE);
    }

    public static void yieldAll(Iterable<?> values) {
        for (final Object value : values) {
            yield_(value);
        }
    }
}
