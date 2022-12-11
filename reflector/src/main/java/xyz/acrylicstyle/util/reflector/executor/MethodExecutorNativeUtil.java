package xyz.acrylicstyle.util.reflector.executor;

import net.blueberrymc.nativeutil.NativeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Executes the method by using {@link NativeUtil}. Bypasses security check, but requires
 * {@code net.blueberrymc.nativeutil} library (v2 or later).
 */
public class MethodExecutorNativeUtil implements MethodExecutor {
    private static final Throwable UNAVAILABLE_REASON;

    static {
        Throwable reason;
        try {
            Class.forName("net.blueberrymc.nativeutil.NativeUtil");
            reason = null;
        } catch (Throwable e) {
            reason = e;
        }
        UNAVAILABLE_REASON = reason;
    }

    /**
     * Checks if the NativeUtil is available.
     * @return true if available, false otherwise
     */
    public static boolean isAvailable() {
        return UNAVAILABLE_REASON == null;
    }

    /**
     * Returns the reason why this class is unavailable.
     * @return the throwable, or null if this class is available.
     */
    public static @Nullable Throwable getUnavailableReason() {
        return UNAVAILABLE_REASON;
    }

    @Override
    public Object invoke(@NotNull Method method, Object instance, Object... args) {
        return NativeUtil.invoke(method, instance, args == null ? new Object[0] : args);
    }

    @Override
    public Object invokeSpecial(@NotNull Method method, @NotNull Object instance, Object... args) {
        return NativeUtil.invokeNonvirtual(method, instance, args == null ? new Object[0] : args);
    }

    @Override
    public <T> @NotNull T newInstance(@NotNull Constructor<T> constructor, Object... args) {
        return NativeUtil.newInstance(constructor, args == null ? new Object[0] : args);
    }

    @Override
    public Object getFieldValue(@NotNull Field field, Object instance) {
        return NativeUtil.get(field, instance);
    }

    @Override
    public void setFieldValue(@NotNull Field field, Object instance, Object value) {
        NativeUtil.set(field, instance, value);
    }
}
