package io.github.gaming32.generators;

import io.github.gaming32.generators.internal.Accessors;
import io.github.gaming32.generators.internal.MutableObject;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Supplier;

public class Generators {
    static final Object SCOPE = Accessors.newContinuationScope("Generators");
    static final Map<Object, GeneratorImpl<?, ?>> GENERATORS = new WeakHashMap<>();

    public static <T> Iterable<T> create(Runnable body) {
        return () -> createGenerator(body);
    }

    public static <T> Generator<T, ?> createGenerator(Runnable body) {
        final GeneratorImpl<T, ?> impl = new GeneratorImpl<>(body);
        GENERATORS.put(impl.continuation, impl);
        return impl;
    }

    public static <T, R> Generator<T, R> createWithResult(Supplier<R> body) {
        final MutableObject<GeneratorImpl<T, R>> reference = new MutableObject<>();
        final GeneratorImpl<T, R> impl = new GeneratorImpl<>(() -> reference.value.result = body.get());
        GENERATORS.put(impl.continuation, impl);
        return reference.value = impl;
    }
}
