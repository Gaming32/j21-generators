package io.github.gaming32.generators;

import java.util.Iterator;

/**
 * An object that allows you to send values to a generator and get the final return value of the generator.
 * @param <T> The values that will be yielded from the generator.
 * @param <R> The final return value type of the generator.
 */
public interface Generator<T, S, R> extends Iterator<T> {
    /**
     * Send a value to the generator for {@link Yield#yield_} to return.
     * @return The same as {@link #hasNext}.
     */
    boolean send(S value);

    /**
     * Get the final return value of the generator.
     * @return The final return value of the iterator.
     */
    R getResult();
}
