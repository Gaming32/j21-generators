package io.github.gaming32.generators;

import io.github.gaming32.generators.internal.Accessors;

import java.util.NoSuchElementException;

class GeneratorImpl<T, R> implements Generator<T, R> {
    final Object continuation;
    GeneratorState state = GeneratorState.NOT_READY;
    Object next;

    boolean canSend;
    Object sent;

    R result;

    GeneratorImpl(Runnable body) {
        continuation = Accessors.newContinuation(Generators.SCOPE, body);
    }

    @Override
    public boolean hasNext() {
        return switch (state) {
            case NOT_READY -> {
                state = GeneratorState.FAILED;
                Accessors.runContinuation(continuation);
                if (state == GeneratorState.FAILED) {
                    state = GeneratorState.DONE;
                    yield false;
                }
                yield true;
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

    @Override
    public boolean send(Object value) {
        if (!canSend) {
            throw new IllegalStateException("Cannot send() immediately");
        }
        sent = value;
        if (state == GeneratorState.READY) {
            next(); // Prepare the generator for a new step
        }
        return hasNext();
    }

    @Override
    public R getResult() {
        if (state != GeneratorState.DONE) {
            throw new IllegalStateException("Cannot getResult() of an incomplete Generator");
        }
        return result;
    }

    enum GeneratorState {
        NOT_READY, READY, FAILED, DONE
    }
}
