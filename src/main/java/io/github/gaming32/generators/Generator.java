package io.github.gaming32.generators;

import java.util.Iterator;

public interface Generator<T, R> extends Iterator<T> {
    /**
     * Send a value to the generator for {@link Yield#yield_} to return.
     * @return The same as {@link #hasNext}.
     */
    boolean send(Object value);

    R getResult();
}
