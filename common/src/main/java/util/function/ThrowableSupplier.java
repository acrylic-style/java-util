package util.function;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.ThrowableActionableResult;

import java.util.AbstractMap;
import java.util.Map;
import java.util.function.Supplier;

@FunctionalInterface
public interface ThrowableSupplier<T> extends Supplier<T> {
    /**
     * Gets a result of this supplier. May be null if exception was thrown.
     * @return a result
     */
    @Nullable
    @Override
    @Contract(pure = true)
    default T get() {
        try {
            return this.evaluate();
        } catch (Throwable throwable) {
            return null;
        }
    }

    @NotNull
    @Contract(pure = true)
    default ThrowableActionableResult<T> getAsResult() {
        return ThrowableActionableResult.ofThrowable(this);
    }

    /**
     * Gets a result of this supplier as entry. Key <b>may</b> be null if exception was thrown, and the value will be null
     * if it was run successfully.
     * @return a result as entry
     */
    @NotNull
    @Contract(pure = true)
    default Map.Entry<@Nullable T, @Nullable Throwable> entry() {
        try {
            return new AbstractMap.SimpleImmutableEntry<>(this.evaluate(), null);
        } catch (Throwable throwable) {
            return new AbstractMap.SimpleImmutableEntry<>(null, throwable);
        }
    }

    /**
     * Gets a result.
     * @return a result
     */
    T evaluate() throws Throwable;
}
