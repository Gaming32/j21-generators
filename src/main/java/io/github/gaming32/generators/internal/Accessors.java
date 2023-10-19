package io.github.gaming32.generators.internal;

import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

public class Accessors {
    private static final MethodHandles.Lookup IMPL_LOOKUP;

    private static final Class<?> CONTINUATION_SCOPE;
    private static final MethodHandle CS_NEW;

    private static final Class<?> CONTINUATION;
    private static final MethodHandle C_NEW;
    private static final MethodHandle C_GET_CURRENT_CONTINUATION;
    private static final MethodHandle C_RUN;
    private static final MethodHandle C_YIELD;

    static {
        try {
            final Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
            unsafeField.setAccessible(true);
            final Unsafe unsafe = (Unsafe)unsafeField.get(null);
            final Field implLookupField = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            //noinspection deprecation
            IMPL_LOOKUP = (MethodHandles.Lookup)unsafe.getObject(
                unsafe.staticFieldBase(implLookupField),
                unsafe.staticFieldOffset(implLookupField)
            );

            CONTINUATION_SCOPE = IMPL_LOOKUP.findClass("jdk.internal.vm.ContinuationScope");
            CS_NEW = IMPL_LOOKUP.findConstructor(CONTINUATION_SCOPE, MethodType.methodType(void.class, String.class));

            CONTINUATION = IMPL_LOOKUP.findClass("jdk.internal.vm.Continuation");
            C_NEW = IMPL_LOOKUP.findConstructor(CONTINUATION, MethodType.methodType(void.class, CONTINUATION_SCOPE, Runnable.class));
            C_GET_CURRENT_CONTINUATION = IMPL_LOOKUP.findStatic(CONTINUATION, "getCurrentContinuation", MethodType.methodType(CONTINUATION, CONTINUATION_SCOPE));
            C_RUN = IMPL_LOOKUP.findVirtual(CONTINUATION, "run", MethodType.methodType(void.class));
            C_YIELD = IMPL_LOOKUP.findStatic(CONTINUATION, "yield", MethodType.methodType(boolean.class, CONTINUATION_SCOPE));
        } catch (Exception e) {
            throw rethrow(e);
        }
    }

    public static Object newContinuationScope(String name) {
        try {
            return CS_NEW.invoke(name);
        } catch (Throwable t) {
            throw rethrow(t);
        }
    }

    public static Object newContinuation(Object scope, Runnable target) {
        try {
            return C_NEW.invoke(scope, target);
        } catch (Throwable t) {
            throw rethrow(t);
        }
    }

    public static Object getCurrentContinuation(Object scope) {
        try {
            return C_GET_CURRENT_CONTINUATION.invoke(scope);
        } catch (Throwable t) {
            throw rethrow(t);
        }
    }

    public static void runContinuation(Object continuation) {
        try {
            C_RUN.invoke(continuation);
        } catch (Throwable t) {
            throw rethrow(t);
        }
    }

    public static boolean yieldContinuation(Object scope) {
        try {
            return (boolean)C_YIELD.invoke(scope);
        } catch (Throwable t) {
            throw rethrow(t);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Throwable> RuntimeException rethrow(Throwable t) throws T {
        throw (T)t;
    }
}
