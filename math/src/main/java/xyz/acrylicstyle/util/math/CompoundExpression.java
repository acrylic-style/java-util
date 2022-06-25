package xyz.acrylicstyle.util.math;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CompoundExpression<T extends Number> extends Expression<T> {
    private final @NotNull Expression<T> expression;

    @Contract(pure = true)
    public CompoundExpression(@Nullable Expression<T> next, @NotNull Expression<T> expression) {
        super(next);
        this.expression = expression;
    }

    @Override
    public @NotNull T evaluate() {
        return null;
    }
}
