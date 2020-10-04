package util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ActionableResult<T> {
    @NotNull
    private static final ActionableResult<?> EMPTY = new ActionableResult<>();

    @Nullable
    private T value = null;

    private ActionableResult() {}

    private ActionableResult(@NotNull T value) { this.value = Objects.requireNonNull(value); }

    @SuppressWarnings("unchecked")
    @NotNull
    public static <V> ActionableResult<V> empty() { return (ActionableResult<V>) EMPTY; }

    @NotNull
    public static <V> ActionableResult<V> of(@NotNull V value) {
        Validate.notNull(value, "value cannot be null (Use #ofNullable)");
        return new ActionableResult<>(value);
    }

    @NotNull
    public static <V> ActionableResult<V> ofNullable(@Nullable V value) { return value == null ? empty() : new ActionableResult<>(value); }

    @NotNull
    public T value() { return get(); }

    @Nullable
    public T nullableValue() { return value; }

    @NotNull
    public T get() {
        if (value == null) throw new NoSuchElementException("No value present");
        return value;
    }

    public boolean isPresent() { return value != null; }

    @NotNull
    public ConditionalInvocableResult<T> invoke() { return new ConditionalInvocableResult<>(value); }

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

        public void ifPresent(@NotNull Consumer<? super E> consumer) {
            Validate.notNull(consumer, "consumer cannot be null");
            if (value != null) consumer.accept(value);
        }

        public void always(@NotNull Consumer<? super E> consumer) {
            Validate.notNull(consumer, "consumer cannot be null");
            consumer.accept(value);
        }

        public void ifTrue(@NotNull Predicate<? super E> predicate, @NotNull Consumer<? super E> consumer) {
            Validate.notNull(predicate, "predicate cannot be null");
            Validate.notNull(consumer, "consumer cannot be null");
            if (predicate.test(value)) consumer.accept(value);
        }

        @NotNull
        public ConditionalInvocableResult<E> ifTrue(@NotNull Predicate<? super E> predicate) {
            Validate.notNull(predicate, "predicate cannot be null");
            return predicate.test(value) ? this : empty();
        }
    }
}
