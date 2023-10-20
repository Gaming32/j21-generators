package io.github.gaming32.generators;

import io.github.gaming32.generators.internal.Accessors;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.WeakHashMap;

public class Generators {
    static final Object SCOPE = Accessors.newContinuationScope("Generators");
    static final Map<Object, GeneratorImpl<?>> GENERATORS = new WeakHashMap<>();

    public static <T> Iterable<T> createGenerator(Runnable body) {
        return () -> createGeneratorIterator(body);
    }

    public static <T> Generator<T> createGeneratorIterator(Runnable body) {
        final GeneratorImpl<T> impl = new GeneratorImpl<>(body);
        GENERATORS.put(impl.continuation, impl);
        return impl;
    }
}
