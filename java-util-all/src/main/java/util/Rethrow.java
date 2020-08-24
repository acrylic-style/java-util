package util;

import lombok.SneakyThrows;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Rethrow<T> {
    @NotNull
    private final ThrowableSupplier<T> runnable;
    @Nullable
    private Class<? extends Throwable> clazz;

    public Rethrow(@NotNull ThrowableSupplier<T> runnable) {
        Validate.notNull(runnable, "supplier cannot be null");
        this.runnable = runnable;
    }

    @NotNull
    @Contract("_ -> this")
    public Rethrow<T> as(@NotNull Class<? extends Throwable> clazz) {
        this.clazz = clazz;
        return this;
    }

    @SneakyThrows({IllegalAccessException.class, InstantiationException.class})
    public T run() {
        try {
            return runnable.run();
        } catch (Throwable e) {
            if (clazz != null) SneakyThrow.sneaky(clazz.newInstance());
            return null; // may be null if clazz was null
        }
    }
}
