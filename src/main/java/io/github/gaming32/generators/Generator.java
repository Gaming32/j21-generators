package io.github.gaming32.generators;

import java.util.Iterator;
import java.util.NoSuchElementException;

public interface Generator<T> extends Iterator<T> {
    /**
     * Send a value to the generator for {@link Yield#yield_} to return.
     * @return The same as {@link #hasNext}.
     * @apiNote If you attempt to call {@code send} again after {@link #hasNext} or {@code send} has returned
     * {@code false}, {@link NoSuchElementException} will be thrown.
     */
    boolean send(Object value);
}
