package io.github.gaming32.generators;

import io.github.gaming32.generators.internal.Accessors;

import java.util.NoSuchElementException;

class GeneratorImpl<T> implements Generator<T> {
    final Object continuation;
    GeneratorState state = GeneratorState.NOT_READY;
    Object next;

    GeneratorImpl(Runnable body) {
        continuation = Accessors.newContinuation(Generators.SCOPE, body);
    }

    @Override
    public boolean hasNext() {
        return switch (state) {
            case NOT_READY -> {
                state = GeneratorState.FAILED;
                Accessors.runContinuation(continuation);
                yield state == GeneratorState.READY;
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
                next = null;
                yield (T)result;
            }
            case FAILED -> throw new IllegalStateException("Generator failed");
            case DONE -> throw new NoSuchElementException();
        };
    }

    enum GeneratorState {
        NOT_READY, READY, FAILED, DONE
    }
}
