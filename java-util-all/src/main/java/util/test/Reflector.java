package util.test;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Collection;

import java.lang.reflect.Proxy;

class Reflector {
    static final Collection<Class<?>, Class<?>> reverseList = new Collection<>();

    @SuppressWarnings("unchecked")
    public static <T> T newReflector(@Nullable ClassLoader classLoader, @NotNull Class<T> clazz, @NotNull ReflectorHandler handler) {
        reverseList.add(clazz, handler.getTarget());
        return (T) Proxy.newProxyInstance(classLoader == null ? ClassLoader.getSystemClassLoader() : classLoader, new Class[] { clazz }, handler);
    }

    public static <T, U> U castTo(@NotNull Class<T> clazz, @NotNull Object instance, @NotNull String method, @NotNull Class<U> target) {
        return castTo(null, clazz, instance, method, target);
    }

    @NotNull
    public static <T, U> U castTo(@Nullable ClassLoader classLoader, @NotNull Class<T> clazz, @NotNull Object instance, @NotNull String method, @NotNull Class<U> target) {
        if (!reverseList.containsKey(clazz)) {
            throw new RuntimeException("Reflector#newReclector was not called for class " + clazz);
        }
        Object inst;
        System.out.println("Clazz: " + clazz + ", inst: " + instance.getClass());
        try {
            inst = instance.getClass().getMethod(method).invoke(instance);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        return newReflector(classLoader, target, new ReflectorHandler(target, inst));
    }
}
