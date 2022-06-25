package xyz.acrylicstyle.util.math;

import org.jetbrains.annotations.NotNull;

public class SimpleNumberExpression<T extends Number> extends Expression<T> {
    private final @NotNull T value;

    public SimpleNumberExpression(@NotNull T value) {
        this.value = value;
    }

    @Override
    public @NotNull T evaluate() {
        return value;
    }
}
