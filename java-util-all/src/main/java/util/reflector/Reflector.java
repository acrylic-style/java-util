package util.reflector;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Collection;

import java.lang.reflect.Proxy;

/**
 * A class that makes easier to call internal method, without using complex reflection code.
 * See {@link ReflectorTest} for example.
 */
public class Reflector {
    static final Collection<Class<?>, Class<?>> reverseList = new Collection<>();

    @SuppressWarnings("unchecked")
    public static <T> T newReflector(@Nullable ClassLoader classLoader, @NotNull Class<T> clazz, @NotNull ReflectorHandler handler) {
        reverseList.add(clazz, handler.getTarget());
        return (T) Proxy.newProxyInstance(classLoader == null ? ClassLoader.getSystemClassLoader() : classLoader, new Class[] { clazz }, handler);
    }

    public static <T, U> U castTo(@NotNull Class<T> clazz, @NotNull Object instance, @NotNull String method, @NotNull Class<U> target, Object... args) {
        return castTo(null, clazz, instance, method, target, args);
    }

    @SuppressWarnings("JavaReflectionInvocation")
    @NotNull
    public static <T, U> U castTo(@Nullable ClassLoader classLoader, @NotNull Class<T> clazz, @NotNull Object instance, @NotNull String method, @NotNull Class<U> target, Object... args) {
        if (!reverseList.containsKey(clazz)) {
            throw new RuntimeException("Reflector#newReflector was not called for class " + clazz);
        }
        Object inst;
        ReflectorHandler handler = (ReflectorHandler) Proxy.getInvocationHandler(instance);
        Class<?> cl = reverseList.get(clazz);
        try {
            inst = cl.getDeclaredMethod(method).invoke(handler.getInstance(), args);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        reverseList.add(target, inst.getClass());
        return newReflector(classLoader, target, new ReflectorHandler(inst.getClass(), inst));
    }
}
