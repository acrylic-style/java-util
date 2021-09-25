package util;

import org.jetbrains.annotations.Nullable;

public interface Callback<T> {
    void done(@Nullable T t, @Nullable Throwable e);
}
