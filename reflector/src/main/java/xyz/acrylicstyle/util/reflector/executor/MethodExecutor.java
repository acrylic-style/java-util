package xyz.acrylicstyle.util.reflector.executor;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface MethodExecutor {
    Object invoke(@NotNull Method method, Object instance, Object... args) throws ReflectiveOperationException;

    Object invokeSpecial(@NotNull Method method, @NotNull Object instance, Object... args) throws Throwable;

    <T> @NotNull T newInstance(@NotNull Constructor<T> constructor, Object... args) throws ReflectiveOperationException;

    Object getFieldValue(@NotNull Field field, Object instance) throws ReflectiveOperationException;

    void setFieldValue(@NotNull Field field, Object instance, Object value) throws ReflectiveOperationException;

    default Object execute(@NotNull Executable executable, Object instance, Object... args) throws ReflectiveOperationException {
        if (executable instanceof Method) return invoke((Method) executable, instance, args);
        if (executable instanceof Constructor) return newInstance((Constructor<?>) executable, args);
        throw new IllegalArgumentException("Unknown executable type: " + executable.getClass().getName());
    }

    @Contract(pure = true)
    static @NotNull MethodExecutor getInstance() {
        if (MethodExecutorNativeUtil.isAvailable()) {
            return new MethodExecutorNativeUtil();
        } else {
            return new MethodExecutorReflection();
        }
    }
}
