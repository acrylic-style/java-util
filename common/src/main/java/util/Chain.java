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

public interface Chain<T> {
    @SuppressWarnings("unchecked")
    default T getObject() {
        return (T) this;
    }
    
    @Contract
    @NotNull
    default T also(@NotNull Consumer<T> consumer) {
        consumer.accept(getObject());
        return getObject();
    }

    @Contract(pure = true)
    @NotNull
    default <R> Map.Entry<T, R> to(@NotNull R that) {
        return new AbstractMap.SimpleEntry<>(getObject(), that);
    }

    @Contract(pure = true)
    @NotNull
    default <R> Map.Entry<R, T> from(@NotNull R that) {
        return new AbstractMap.SimpleEntry<>(that, getObject());
    }

    default <R> R let(@NotNull Function<T, R> mapFunction) { return mapFunction.apply(getObject()); }

    @Nullable
    default <R> R letCatching(@NotNull ThrowableFunction<T, R> function) { return function.apply(getObject()).get(); }

    @Contract(pure = true)
    @NotNull
    default <R> ThrowableActionableResult<R> runCatching(@NotNull ThrowableFunction<T, R> function) {
        return function.apply(getObject());
    }

    /**
     * Returns this value if it satisfies the given <i>predicate</i> or null, if it doesn't.
     */
    @Nullable
    default T takeIf(@NotNull Predicate<T> predicate) {
        return predicate.test(getObject()) ? getObject() : null;
    }

    /**
     * Returns this value if it <b>does not</b> satisfy the given <i>predicate</i> or null, if it does.
     */
    @Nullable
    default T takeUnless(@NotNull Predicate<T> predicate) {
        return predicate.test(getObject()) ? getObject() : null;
    }
}
