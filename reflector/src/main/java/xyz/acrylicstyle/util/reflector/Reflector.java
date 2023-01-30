package xyz.acrylicstyle.util.reflector;

import net.blueberrymc.nativeutil.NativeUtil;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.acrylicstyle.util.reflector.executor.MethodExecutor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * A class that makes easier to call internal method, without using complex reflection code.
 */
public class Reflector {
    public static ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    public static MethodExecutor methodExecutor = MethodExecutor.getInstance();

    static final Map<Class<?>, Class<?>> reverseList = new HashMap<>();
    static final Map<Object, Object> reverseInstanceList = new HashMap<>();

    @Contract(pure = true)
    public static <T> @NotNull T newEmptyReflector(@Nullable ClassLoader classLoader, @NotNull Class<T> clazz) {
        return newReflector(classLoader, clazz, new ReflectorHandler(Object.class, new Object()));
    }

    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <T> @NotNull T newReflector(@Nullable ClassLoader classLoader, @NotNull Class<T> clazz, @NotNull ReflectorHandler handler) {
        Objects.requireNonNull(clazz, "class cannot be null");
        Objects.requireNonNull(handler.getTarget(), "target class cannot be null");
        reverseList.put(clazz, handler.getTarget());
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
            targetClass = Class.forName(targetClassName, true, classLoader != null ? classLoader : Reflector.classLoader);
        } catch (ClassNotFoundException ex) {
            return null;
        }
        return newReflector(classLoader, clazz, new ReflectorHandler(targetClass, instance));
    }

    public static <T, U> U castTo(@NotNull Class<T> clazz, @NotNull Object instance, @NotNull String method, @NotNull Class<U> target, Object... args) {
        return castTo(null, clazz, instance, method, target, args);
    }

    public static <T, U> U castFieldTo(@NotNull Class<T> clazz, @NotNull Object instance, @NotNull String field, @NotNull Class<U> target) {
        return castFieldTo(null, clazz, instance, field, target);
    }

    public static <T, U> U castTo(
            @Nullable ClassLoader classLoader,
            @NotNull Class<T> clazz,
            @NotNull Object instance,
            @NotNull String method,
            @NotNull Class<U> target,
            Object @NotNull ... args
    ) {
        Objects.requireNonNull(instance, "instance");
        Objects.requireNonNull(method, "method");
        Objects.requireNonNull(target, "target");
        if (!reverseList.containsKey(clazz)) {
            throw new RuntimeException("Reflector#newReflector was not called for class " + clazz);
        }
        Object inst;
        ReflectorHandler handler = (ReflectorHandler) Proxy.getInvocationHandler(instance);
        Class<?> cl = Objects.requireNonNull(reverseList.get(clazz));
        try {
            Class<?>[] classes = Arrays.stream(args).map(Object::getClass).toArray(Class[]::new);
            Method jmethod = null;
            try {
                // try exact match
                jmethod = cl.getDeclaredMethod(method, classes);
            } catch (NoSuchMethodException e) {
                // iterate through all methods and find the best match
                for (Method declaredMethod : cl.getDeclaredMethods()) {
                    // try to match by name and parameter count
                    if (declaredMethod.getName().equals(method) && declaredMethod.getParameterCount() == args.length) {
                        if (jmethod != null) {
                            // two or more methods found
                            throw new RuntimeException("Ambiguous method call: " + method + " in " + cl + " (found " + jmethod.toGenericString() + " and " + declaredMethod.toGenericString() + ")");
                        }
                        jmethod = declaredMethod;
                    }
                }
                // method not found
                if (jmethod == null) {
                    throw e;
                }
            }
            inst = methodExecutor.invoke(jmethod, handler.getInstance(), args);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        if (inst == null) return null;
        reverseList.put(target, inst.getClass());
        reverseInstanceList.put(instance, inst);
        return newReflector(classLoader, target, new ReflectorHandler(inst.getClass(), inst));
    }

    public static <T, U> U castTo(@Nullable ClassLoader classLoader, @NotNull Class<T> clazz, @NotNull Object instance, @NotNull String methodName, @NotNull Method method, @NotNull Class<U> target, Object... args) {
        if (!reverseList.containsKey(clazz)) {
            throw new RuntimeException("Reflector#newReflector was not called for class " + clazz);
        }
        ReflectorHandler handler = (ReflectorHandler) Proxy.getInvocationHandler(instance);
        Object inst;
        Class<?> cl = Objects.requireNonNull(reverseList.get(clazz));
        try {
            inst = NativeUtil.invoke(cl.getDeclaredMethod(methodName, method.getParameterTypes()), handler.getInstance(), args);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        if (inst == null) return null;
        reverseList.put(target, inst.getClass());
        reverseInstanceList.put(instance, inst);
        return newReflector(classLoader, target, new ReflectorHandler(inst.getClass(), inst));
    }

    @Contract("_, _, null, _ -> null")
    public static <U> U castTo(@Nullable ClassLoader classLoader, @NotNull Object proxy, @Nullable Object instance, @NotNull Class<U> target) {
        if (instance == null) return null;
        reverseList.put(target, instance.getClass());
        try {
            reverseInstanceList.put(proxy, instance);
        } catch (NullPointerException ignore) {}
        return newReflector(classLoader, target, new ReflectorHandler(instance.getClass(), instance));
    }

    public static <T, U> U castFieldTo(@Nullable ClassLoader classLoader, @NotNull Class<T> clazz, @NotNull Object instance, @NotNull String field, @NotNull Class<U> target) {
        Objects.requireNonNull(instance, "instance");
        Objects.requireNonNull(field, "field");
        Objects.requireNonNull(target, "target");
        if (!reverseList.containsKey(clazz)) {
            throw new RuntimeException("Reflector#newReflector was not called for class " + clazz);
        }
        Object inst;
        ReflectorHandler handler = (ReflectorHandler) Proxy.getInvocationHandler(instance);
        Class<?> cl = Objects.requireNonNull(reverseList.get(clazz));
        try {
            inst = NativeUtil.get(cl.getDeclaredField(field), handler.getInstance());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
        if (inst == null) return null;
        reverseList.put(target, inst.getClass());
        reverseInstanceList.put(instance, inst);
        return newReflector(classLoader, target, new ReflectorHandler(inst.getClass(), inst));
    }

    /**
     * Unwraps the proxy instance and returns the original instance.
     * @param o the proxy instance
     * @return the original instance, or empty if the instance is not a proxy
     */
    public static @NotNull Optional<Object> unwrap(@Nullable Object o) {
        if (o == null) {
            return Optional.empty();
        }
        try {
            InvocationHandler handler = Proxy.getInvocationHandler(o);
            if (handler instanceof ReflectorHandler) {
                return Optional.of(((ReflectorHandler) handler).getInstance());
            }
        } catch (IllegalArgumentException ignore) {}
        return Optional.empty();
    }

    /**
     * Almost same as {@link #unwrap(Object)}, but returns the passed object if it is not a proxy instance.
     * @param o the object
     * @return the unwrapped instance or the passed object
     */
    @Contract("null -> null")
    public static Object unwrapOrObject(@Nullable Object o) {
        if (o == null) {
            return null;
        }
        try {
            InvocationHandler handler = Proxy.getInvocationHandler(o);
            if (handler instanceof ReflectorHandler) {
                return ((ReflectorHandler) handler).getInstance();
            }
        } catch (IllegalArgumentException ignore) {}
        return o;
    }
}
