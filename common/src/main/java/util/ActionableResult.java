package util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.function.ThrowableSupplier;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ActionableResult<T> implements Chain<ActionableResult<T>> {
    @NotNull
    private static final ActionableResult<?> EMPTY = new ActionableResult<>();

    @Nullable
    protected final T value;

    protected ActionableResult() { this.value = null; }

    protected ActionableResult(@Nullable T value) { this.value = value; }

    @Nullable
    public static <V> V getThrowable(@NotNull ThrowableSupplier<V> supplier) {
        return ofThrowable(supplier).get();
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public static <V> ActionableResult<V> empty() { return (ActionableResult<V>) EMPTY; }

    @NotNull
    public static <V> ActionableResult<V> of(@NotNull Supplier<V> supplier) { return of(supplier.get()); }

    @NotNull
    public static <V> ActionableResult<V> ofNullable(@NotNull Supplier<V> supplier) { return ofNullable(supplier.get()); }

    @NotNull
    public static <V> ActionableResult<V> of(@NotNull V value) {
        Validate.notNull(value, "value cannot be null (Use #ofNullable)");
        return new ActionableResult<>(value);
    }

    @NotNull
    public static <V> ThrowableActionableResult<V> ofThrowable(@NotNull ThrowableSupplier<V> supplier) {
        return ThrowableActionableResult.of(supplier);
    }

    @NotNull
    public static <V> ActionableResult<V> ofNullable(@Nullable V value) { return value == null ? empty() : new ActionableResult<>(value); }

    /**
     * @deprecated use {@link #getOrThrow()} instead
     */
    @Deprecated
    @NotNull
    public T value() { return getOrThrow(); }

    /**
     * @deprecated use {@link #get()} instead
     */
    @Deprecated
    @Nullable
    public T nullableValue() { return value; }

    @Nullable
    public T get() {
        return value;
    }

    @NotNull
    public T getOrThrow() {
        if (value == null) throw new NoSuchElementException("No value present");
        return value;
    }

    public boolean isPresent() { return value != null; }

    @NotNull
    public ConditionalInvocableResult<T> invoke() { return new ConditionalInvocableResult<>(value); }

    @NotNull
    public ActionableResult<T> then(@NotNull Consumer<? super T> action) {
        invoke().always(action);
        return this;
    }

    @NotNull
    public ActionableResult<T> ifPresent(@NotNull Consumer<? super T> action) {
        invoke().ifPresent(action);
        return this;
    }

    @NotNull
    public ActionableResult<T> ifNotPresent(@NotNull Runnable action) {
        invoke().ifNotPresent(action);
        return this;
    }

    @NotNull
    public <U> ActionableResult<U> swap(@NotNull Supplier<U> supplier) { return ofNullable(supplier.get()); }

    @NotNull
    public <U> ActionableResult<U> swap(@Nullable U value) { return ofNullable(value); }

    @NotNull
    public ActionableResult<T> filter(@NotNull Predicate<? super T> predicate) {
        Validate.notNull(predicate, "predicate cannot be null");
        return !isPresent() || predicate.test(value) ? this : empty();
    }

    @NotNull
    public <U> ActionableResult<U> map(@NotNull Function<? super T, ? extends U> function) {
        Validate.notNull(function, "function cannot be null");
        return !isPresent() ? empty() : ActionableResult.ofNullable(function.apply(value));
    }

    @NotNull
    public <U> ActionableResult<U> flatMap(@NotNull Function<? super T, ActionableResult<U>> function) {
        Validate.notNull(function, "function cannot be null");
        return !isPresent() ? empty() : Objects.requireNonNull(function.apply(value));
    }

    @Contract("!null -> !null; null -> _")
    public T orElse(@Nullable T other) { return value == null ? other : value; }

    public T orElseGet(@NotNull Supplier<? extends T> supplier) { return value == null ? supplier.get() : value; }

    @NotNull
    public <X extends Throwable> T orElseThrow(@NotNull Supplier<? extends X> supplier) throws X {
        if (value != null) return value;
        throw supplier.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActionableResult<?> that = (ActionableResult<?>) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() { return value != null ? value.hashCode() : 0; }

    @Override
    public String toString() {
        return value == null ? "ActionableResult{empty}" : "ActionableResult{value=" + value + '}';
    }

    public static class ConditionalInvocableResult<E> {
        private static final ConditionalInvocableResult<?> EMPTY = new ConditionalInvocableResult<>(null);

        @Nullable
        private final E value;

        public ConditionalInvocableResult(@Nullable E value) { this.value = value; }

        @NotNull
        @SuppressWarnings("unchecked")
        public static <E> ConditionalInvocableResult<E> empty() { return (ConditionalInvocableResult<E>) EMPTY; }

        @NotNull
        public ConditionalInvocableResult<E> ifPresent(@NotNull Consumer<? super E> consumer) {
            Validate.notNull(consumer, "consumer cannot be null");
            if (value != null) consumer.accept(value);
            return this;
        }

        @NotNull
        public ConditionalInvocableResult<E> ifNotPresent(@NotNull Runnable action) {
            Validate.notNull(action, "runnable cannot be null");
            if (value == null) action.run();
            return this;
        }

        @NotNull
        public ConditionalInvocableResult<E> always(@NotNull Consumer<? super E> consumer) {
            Validate.notNull(consumer, "consumer cannot be null");
            consumer.accept(value);
            return this;
        }

        @NotNull
        public ConditionalInvocableResult<E> ifTrue(@NotNull Predicate<? super E> predicate, @NotNull Consumer<? super E> consumer) {
            Validate.notNull(predicate, "predicate cannot be null");
            Validate.notNull(consumer, "consumer cannot be null");
            if (predicate.test(value)) consumer.accept(value);
            return this;
        }

        @NotNull
        public ConditionalInvocableResult<E> ifTrue(@NotNull Predicate<? super E> predicate) {
            Validate.notNull(predicate, "predicate cannot be null");
            return predicate.test(value) ? this : empty();
        }

        @Contract(pure = true)
        @Nullable
        public final E getValue() {
            return value;
        }
    }
}
