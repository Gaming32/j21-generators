package io.github.gaming32.generators;

import io.github.gaming32.generators.internal.Accessors;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.WeakHashMap;

public class Generators {
    static final Object SCOPE = Accessors.newContinuationScope("Generators");
    static final Map<Object, Object> RESULTS = new WeakHashMap<>();

    public static <T> Iterable<T> createGenerator(Runnable body) {
        return () -> createGeneratorIterator(body);
    }

    public static <T> Iterator<T> createGeneratorIterator(Runnable body) {
        return new Iterator<>() {
            private final Object continuation = Accessors.newContinuation(SCOPE, body);

            private GeneratorState state = GeneratorState.NOT_READY;
            private Object next;

            @Override
            public boolean hasNext() {
                return switch (state) {
                    case NOT_READY -> {
                        state = GeneratorState.FAILED;
                        next = null;
                        RESULTS.remove(continuation);
                        Accessors.runContinuation(continuation);
                        if (RESULTS.containsKey(continuation)) {
                            state = GeneratorState.READY;
                            next = RESULTS.remove(continuation);
                            yield true;
                        }
                        state = GeneratorState.DONE;
                        yield false;
                    }
                    case READY -> true;
                    case FAILED -> throw new IllegalStateException("Generator failed");
                    case DONE -> false;
                };
            }

            @Override
            @SuppressWarnings("unchecked")
            public T next() {
                return switch (state) {
                    case NOT_READY -> {
                        //noinspection ResultOfMethodCallIgnored
                        hasNext();
                        yield next();
                    }
                    case READY -> {
                        final Object result = next;
                        state = GeneratorState.NOT_READY;
                        yield (T)result;
                    }
                    case FAILED -> throw new IllegalStateException("Generator failed");
                    case DONE -> throw new NoSuchElementException();
                };
            }
        };
    }

    enum GeneratorState {
        NOT_READY, READY, FAILED, DONE
    }
}
