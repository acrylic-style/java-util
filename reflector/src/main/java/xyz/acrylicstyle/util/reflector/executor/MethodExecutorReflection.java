package xyz.acrylicstyle.util.reflector.executor;

import org.jetbrains.annotations.NotNull;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Executes the method by using reflection.
 */
public class MethodExecutorReflection implements MethodExecutor {
    @Override
    public Object invoke(@NotNull Method method, Object instance, Object... args) throws ReflectiveOperationException {
        if (!method.isAccessible()) {
            method.setAccessible(true);
        }
        return method.invoke(instance, args);
    }

    @Override
    public Object invokeSpecial(@NotNull Method method, @NotNull Object proxy, Object... args) throws Throwable {
        try {
            return MethodHandles.lookup()
                    .findSpecial(method.getDeclaringClass(), method.getName(), MethodType.methodType(method.getReturnType(), method.getParameterTypes()), method.getDeclaringClass())
                    .bindTo(proxy)
                    .invokeWithArguments(args);
        } catch (IllegalAccessException ignore) {}
        // hack for java 8
        return newInstance(MethodHandles.Lookup.class.getDeclaredConstructor(Class.class), method.getDeclaringClass())
                .in(method.getDeclaringClass())
                .unreflectSpecial(method, method.getDeclaringClass())
                .bindTo(proxy)
                .invokeWithArguments(args);
    }

    @Override
    public <T> @NotNull T newInstance(@NotNull Constructor<T> constructor, Object... args) throws ReflectiveOperationException {
        if (!constructor.isAccessible()) {
            constructor.setAccessible(true);
        }
        return constructor.newInstance(args);
    }

    @Override
    public Object getFieldValue(@NotNull Field field, Object instance) throws ReflectiveOperationException {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        return field.get(instance);
    }

    @Override
    public void setFieldValue(@NotNull Field field, Object instance, Object value) throws ReflectiveOperationException {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        field.set(instance, value);
    }
}
