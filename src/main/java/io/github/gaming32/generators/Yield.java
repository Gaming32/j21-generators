package io.github.gaming32.generators;

import io.github.gaming32.generators.internal.Accessors;

public class Yield {
    public static void yield_(Object value) {
        Generators.RESULTS.put(Accessors.getCurrentContinuation(Generators.SCOPE), value);
        Accessors.yieldContinuation(Generators.SCOPE);
    }

    public static void yieldAll(Iterable<?> values) {
        for (final Object value : values) {
            yield_(value);
        }
    }
}
