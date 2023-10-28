package io.github.gaming32.generators;

import io.github.gaming32.generators.internal.Accessors;
import io.github.gaming32.generators.internal.MutableObject;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Supplier;

/**
 * Factories for generators.
 */
public class Generators {
    static final Object SCOPE = Accessors.newContinuationScope("Generators");
    static final Map<Object, GeneratorImpl<?, ?, ?>> GENERATORS = new WeakHashMap<>();

    /**
     * The simplest generator factory, which is the most common use case.
     * <br>
     * Example:
     * <pre>
     * {@code
     * public static Iterable<String> myGenerator() {
     *     return Generators.create(() -> {
     *         Yield.yield_("hi");
     *         for (int i = 1; i <= 5; i++) {
     *             Yield.yield_("n".repeat(i));
     *         }
     *     });
     * }
     * }
     * </pre>
     * @param body The actual body of the continuation
     * @return The created generator
     * @param <T> The type of the values this generator will be yielding. All {@link Yield#yield_} calls must match
     *            this type (or heap pollution will occur), even though there's no way for this to be statically
     *            checked.
     */
    public static <T> Iterable<T> create(Runnable body) {
        return () -> createGenerator(body);
    }

    /**
     * Create a {@link Generator} that returns no final result. This is useful for being able to use
     * {@link Generator#send}.
     * @param body The body of the generator, which can now use {@link Yield#yield_}'s return value and have it not
     *             always return {@code null}.
     * @return A generator that can be sent to.
     * @param <T> The type of the values that will be yielded out of the returned generator.
     * @param <S> The type of the values that may be {@link Generator#send sent} to the returned generator.
     * @see Generator
     * @see Generator#send
     */
    public static <T, S> Generator<T, S, Void> createGenerator(Runnable body) {
        final GeneratorImpl<T, S, Void> impl = new GeneratorImpl<>(body);
        GENERATORS.put(impl.continuation, impl);
        return impl;
    }

    /**
     * Create a {@link Generator} that returns a final result and can have values sent to it.
     * @param body The body of the generator, which can return a final value.
     * @return The generator.
     * @param <T> The type of the values that will be yielded out of the returned generator.
     * @param <S> The type of the values that may be {@link Generator#send sent} to the returned generator.
     * @param <R> The type of the final result returned out of the generator.
     */
    public static <T, S, R> Generator<T, S, R> createWithResult(Supplier<R> body) {
        final MutableObject<GeneratorImpl<T, S, R>> reference = new MutableObject<>();
        final GeneratorImpl<T, S, R> impl = new GeneratorImpl<>(() -> reference.value.result = body.get());
        GENERATORS.put(impl.continuation, impl);
        return reference.value = impl;
    }
}
