package util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.reflect.Ref;

public class Rethrow<T> {
    @NotNull
    private final ThrowableSupplier<T> runnable;
    @Nullable
    private Class<? extends Throwable> clazz;

    public Rethrow(@NotNull ThrowableSupplier<T> runnable) {
        Validate.notNull(runnable, "supplier cannot be null");
        this.runnable = runnable;
    }

    /**
     * Specifies the Throwable class that will be thrown sneaky when the exception/error was thrown.
     */
    @NotNull
    @Contract("_ -> this")
    public Rethrow<T> as(@NotNull Class<? extends Throwable> clazz) {
        try {
            clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException(clazz.getCanonicalName() + " is impossible to instantiate");
        }
        this.clazz = clazz;
        return this;
    }

    /**
     * Evaluates the runnable and returns null if exception was thrown.
     * Throws the exception sneaky when the throwable specified in the {@link #as(Class)}
     * was the thrown.
     * @return the evaluated value, or null if exception was thrown.
     */
    public T run() {
        try {
            return runnable.run();
        } catch (Throwable e) {
            if (clazz != null) {
                if (Ref.getClass(e.getClass()).isExtends(clazz)) SneakyThrow.sneaky(e);
            }
            return null; // may be null if clazz was null
        }
    }
}
