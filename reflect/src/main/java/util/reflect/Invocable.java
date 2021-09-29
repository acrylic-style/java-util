package util.reflect;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Invocable<T extends Executable> {
    private final T executable;

    public Invocable(@NotNull T executable) {
        this.executable = executable;
    }

    @NotNull
    public T getExecutable() {
        return executable;
    }

    @Contract(value = "null -> null; !null -> new", pure = true)
    public static Invocable<Method> of(@Nullable Method method) {
        if (method == null) return null;
        return new Invocable<>(method);
    }

    @Contract(value = "null -> null; !null -> new", pure = true)
    public static <T> Invocable<Constructor<T>> of(@Nullable Constructor<T> constructor) {
        if (constructor == null) return null;
        return new Invocable<>(constructor);
    }

    public Object execute(@Nullable Object obj, Object... args) throws InvocationTargetException, IllegalAccessException, InstantiationException {
        if (executable instanceof Method) {
            Method method = ((Method) executable);
            return method.invoke(obj, args == null ? new Object[0] : args);
        } else if (executable instanceof Constructor) {
            return ((Constructor<?>) executable).newInstance(args);
        } else {
            throw new AssertionError(executable.getClass().getCanonicalName());
        }
    }
}
