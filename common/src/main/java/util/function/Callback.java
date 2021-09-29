package util.function;

import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface Callback<T> {
    void done(@Nullable T t, @Nullable Throwable e);
}
