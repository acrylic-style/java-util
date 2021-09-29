package util.promise.rewrite;

import org.jetbrains.annotations.Nullable;

public interface PromiseContext<T> {
    void resolve(@Nullable T value);
    void reject(@Nullable Throwable throwable);

    /**
     * Resolves promise with null.
     */
    default void resolve() {
        resolve(null);
    }
}
