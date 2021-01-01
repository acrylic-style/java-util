package util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.function.ThrowableFunction;

import java.util.AbstractMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public interface Chain<T extends Chain<T>> {
    @Contract
    @NotNull
    default T also(@NotNull Consumer<T> consumer) {
        consumer.accept((T) this);
        return (T) this;
    }

    @Contract(pure = true)
    @NotNull
    default <R> Map.Entry<T, R> to(@NotNull R that) {
        return new AbstractMap.SimpleEntry<>((T) this, that);
    }

    @Contract(pure = true)
    @NotNull
    default <R> Map.Entry<R, T> from(@NotNull R that) {
        return new AbstractMap.SimpleEntry<>(that, (T) this);
    }

    default <R> R let(@NotNull Function<T, R> mapFunction) { return mapFunction.apply((T) this); }

    @Nullable
    default <R> R letCatching(@NotNull ThrowableFunction<T, R> function) { return function.apply((T) this).nullableValue(); }

    @Contract(pure = true)
    @NotNull
    default <R> ThrowableActionableResult<R> runCatching(@NotNull ThrowableFunction<T, R> function) {
        return function.apply((T) this);
    }

    /**
     * Returns this value if it satisfies the given <i>predicate</i> or null, if it doesn't.
     */
    @Nullable
    default T takeIf(@NotNull Predicate<T> predicate) {
        return predicate.test((T) this) ? (T) this : null;
    }

    /**
     * Returns this value if it <b>does not</b> satisfy the given <i>predicate</i> or null, if it does.
     */
    @Nullable
    default T takeUnless(@NotNull Predicate<T> predicate) {
        return predicate.test((T) this) ? (T) this : null;
    }
}
