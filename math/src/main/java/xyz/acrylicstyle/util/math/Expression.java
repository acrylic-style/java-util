package xyz.acrylicstyle.util.math;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Expression<T> {
    private final @Nullable Expression<T> next;

    public Expression() {
        this.next = null;
    }

    public Expression(@Nullable Expression<T> next) {
        this.next = next;
    }

    @NotNull
    public abstract T evaluate();
}
