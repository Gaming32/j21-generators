package io.github.gaming32.generators;

import io.github.gaming32.generators.internal.Accessors;

import java.util.Iterator;

public class Yield {
    public static Object yield_(Object value) {
        final GeneratorImpl<?, ?> generator = Generators.GENERATORS.get(Accessors.getCurrentContinuation(Generators.SCOPE));
        if (generator == null) {
            throw new IllegalStateException("Current generator not in generator map?");
        }
        generator.state = GeneratorImpl.GeneratorState.READY;
        generator.next = value;
        generator.canSend = true;
        Accessors.yieldContinuation(Generators.SCOPE);
        final Object sent = generator.sent;
        generator.sent = null;
        return sent;
    }

    public static Object yieldAll(Iterable<?> values) {
        return yieldAll(values.iterator());
    }

    public static Object yieldAll(Iterator<?> iter) {
        if (iter instanceof Generator<?, ?> generator) {
            return yieldAll(generator);
        }
        while (iter.hasNext()) {
            final Object sent = yield_(iter.next());
            if (sent != null) {
                throw new IllegalArgumentException("Cannot send value to non-generator " + iter);
            }
        }
        return null;
    }

    public static <R> R yieldAll(Generator<?, R> generator) {
        while (generator.hasNext()) {
            generator.send(yield_(generator.next()));
        }
        return generator.getResult();
    }
}
