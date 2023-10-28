package io.github.gaming32.generators;

import io.github.gaming32.generators.internal.Accessors;

import java.util.Iterator;

/**
 * Methods for yielding values out of generators.
 */
public class Yield {
    /**
     * The most basic yield method.
     * @param value The value to yield.
     * @return Any value that was {@link Generator#send sent} to the current generator.
     * @param <S> The type of values {@link Generator#send sent} to this generator.
     */
    public static <S> S yield_(Object value) {
        final Object continuation = Accessors.getCurrentContinuation(Generators.SCOPE);
        if (continuation == null) {
            throw new IllegalStateException("Cannot call Yield.yield_ outside a generator.");
        }
        final GeneratorImpl<?, ?, ?> generator = Generators.GENERATORS.get(continuation);
        assert generator != null : "Current generator not in generator map?";
        generator.state = GeneratorImpl.GeneratorState.READY;
        generator.next = value;
        generator.canSend = true;
        Accessors.yieldContinuation(Generators.SCOPE);
        @SuppressWarnings("unchecked") final S sent = (S)generator.sent;
        generator.sent = null;
        return sent;
    }

    /**
     * Yields all the values in {@code values}.
     * @param values The {@link Iterable} to yield all values from.
     * @return The return value of the generator referenced by {@code values} if it is one, otherwise {@code null}.
     * @param <T> The type of the values in {@code values}.
     * @param <R> The type of the return value.
     */
    public static <T, R> R yieldAll(Iterable<T> values) {
        return yieldAll(values.iterator());
    }

    /**
     * Yields all the values in {@code iter}.
     * @param iter The {@link Iterator} to yield all values from.
     * @return The return value of the generator referenced by {@code iter} if it is one, otherwise {@code null}.
     * @param <T> The type of the values in {@code iter}.
     * @param <R> The type of the return value.
     */
    public static <T, R> R yieldAll(Iterator<T> iter) {
        if (iter instanceof Generator<?, ?, ?> generator) {
            @SuppressWarnings("unchecked") final R result = yieldAll((Generator<T, ?, R>)generator);
            return result;
        }
        while (iter.hasNext()) {
            final Object sent = yield_(iter.next());
            if (sent != null) {
                throw new IllegalArgumentException("Cannot send value to non-generator " + iter);
            }
        }
        return null;
    }

    /**
     * Yields all the values in {@code generator}.
     * @param generator The {@link Generator} to yield all values from.
     * @return The return value of {@code generator}.
     * @param <T> The type of the values in {@code generator}.
     * @param <S> The type of values sent to {@code generator}
     * @param <R> The type of the return value.
     */
    public static <T, S, R> R yieldAll(Generator<T, S, R> generator) {
        while (generator.hasNext()) {
            generator.send(yield_(generator.next()));
        }
        return generator.getResult();
    }
}
