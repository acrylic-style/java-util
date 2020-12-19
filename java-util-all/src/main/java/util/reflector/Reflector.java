package util.reflector;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Collection;
import util.reflect.RefField;
import util.reflect.RefMethod;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * A class that makes easier to call internal method, without using complex reflection code.
 * See {@link ReflectorTest} for example.
 */
public class Reflector {
    public static ClassLoader classLoader = ClassLoader.getSystemClassLoader();

    static final Collection<Class<?>, Class<?>> reverseList = new Collection<>();
    static final Collection<Object, Object> reverseInstanceList = new Collection<>();

    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <T> @NotNull T newReflector(@Nullable ClassLoader classLoader, @NotNull Class<T> clazz, @NotNull ReflectorHandler handler) {
        reverseList.add(clazz, handler.getTarget());
        return (T) Proxy.newProxyInstance(classLoader == null ? Reflector.classLoader : classLoader, new Class[] { clazz }, handler);
    }

    /**
     * Creates new reflector. When targetClassName cannot be found at runtime, the null will be returned.
     * @param classLoader class loader to find class
     * @param clazz the class
     * @param targetClassName the target class to find
     * @param instance the instance of target class
     * @return reflector if found, null if target class could not be found
     */
    @Contract(pure = true)
    public static <T> @Nullable T newReflector(@Nullable ClassLoader classLoader, @NotNull Class<T> clazz, @NotNull String targetClassName, @Nullable Object instance) {
        Class<?> targetClass;
        try {
            targetClass = Class.forName(targetClassName);
        } catch (ClassNotFoundException ex) {
            return null;
        }
        return newReflector(classLoader, clazz, new ReflectorHandler(targetClass, instance));
    }

    public static <T, U> @NotNull U castTo(@NotNull Class<T> clazz, @NotNull Object instance, @NotNull String method, @NotNull Class<U> target, Object... args) {
        return castTo(null, clazz, instance, method, target, args);
    }

    public static <T, U> @NotNull U castFieldTo(@NotNull Class<T> clazz, @NotNull Object instance, @NotNull String field, @NotNull Class<U> target) {
        return castFieldTo(null, clazz, instance, field, target);
    }

    @NotNull
    public static <T, U> U castTo(@Nullable ClassLoader classLoader, @NotNull Class<T> clazz, @NotNull Object instance, @NotNull String method, @NotNull Class<U> target, Object... args) {
        if (!reverseList.containsKey(clazz)) {
            throw new RuntimeException("Reflector#newReflector was not called for class " + clazz);
        }
        Object inst;
        ReflectorHandler handler = (ReflectorHandler) Proxy.getInvocationHandler(instance);
        Class<?> cl = reverseList.get(clazz);
        try {
            inst = new RefMethod<>(cl.getDeclaredMethod(method)).accessible(true).invoke(handler.getInstance(), args);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        reverseList.add(target, inst.getClass());
        reverseInstanceList.add(instance, inst);
        return newReflector(classLoader, target, new ReflectorHandler(inst.getClass(), inst));
    }

    @NotNull
    public static <T, U> U castTo(@Nullable ClassLoader classLoader, @NotNull Class<T> clazz, @NotNull Object instance, @NotNull String methodName, @NotNull Method method, @NotNull Class<U> target, Object... args) {
        if (!reverseList.containsKey(clazz)) {
            throw new RuntimeException("Reflector#newReflector was not called for class " + clazz);
        }
        ReflectorHandler handler = (ReflectorHandler) Proxy.getInvocationHandler(instance);
        Object inst;
        Class<?> cl = reverseList.get(clazz);
        try {
            inst = new RefMethod<>(cl.getDeclaredMethod(methodName, method.getParameterTypes())).accessible(true).invoke(handler.getInstance(), args);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        reverseList.add(target, inst.getClass());
        reverseInstanceList.add(instance, inst);
        return newReflector(classLoader, target, new ReflectorHandler(inst.getClass(), inst));
    }

    @NotNull
    public static <T, U> U castFieldTo(@Nullable ClassLoader classLoader, @NotNull Class<T> clazz, @NotNull Object instance, @NotNull String field, @NotNull Class<U> target) {
        if (!reverseList.containsKey(clazz)) {
            throw new RuntimeException("Reflector#newReflector was not called for class " + clazz);
        }
        Object inst;
        ReflectorHandler handler = (ReflectorHandler) Proxy.getInvocationHandler(instance);
        Class<?> cl = reverseList.get(clazz);
        try {
            inst = new RefField<>(cl.getDeclaredField(field)).accessible(true).get(handler.getInstance());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        reverseList.add(target, inst.getClass());
        reverseInstanceList.add(instance, inst);
        return newReflector(classLoader, target, new ReflectorHandler(inst.getClass(), inst));
    }
}
