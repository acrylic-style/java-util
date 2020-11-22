package util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.function.DelegatingThrowableSupplier;
import util.function.ThrowableSupplier;

import java.util.function.Function;
import java.util.function.Predicate;

public class ThrowableActionableResult<T> extends ActionableResult<T> {
    @SuppressWarnings("rawtypes")
    @NotNull
    private static final ThrowableActionableResult EMPTY = new ThrowableActionableResult();

    private final Throwable throwable;

    protected ThrowableActionableResult() {
        super();
        this.throwable = null;
    }

    protected ThrowableActionableResult(@Nullable T value) {
        super(value);
        this.throwable = null;
    }

    protected ThrowableActionableResult(@Nullable T value, @Nullable Throwable throwable) {
        super(value);
        this.throwable = throwable;
    }

    protected ThrowableActionableResult(@NotNull ThrowableSupplier<T> supplier) {
        this(DelegatingThrowableSupplier.getInstance(supplier).entry().getKey(), DelegatingThrowableSupplier.getInstance(supplier).entry().getValue());
        DelegatingThrowableSupplier.remove(supplier); // remove from cache
    }

    @SuppressWarnings("unchecked")
    @NotNull
    public static <T> ThrowableActionableResult<T> empty() { return EMPTY; }

    @NotNull
    public static <T> ThrowableActionableResult<T> of(@NotNull ThrowableSupplier<T> supplier) {
        return new ThrowableActionableResult<>(supplier);
    }

    @NotNull
    public static <T> ThrowableActionableResult<T> of(@Nullable T value, @Nullable Throwable throwable) {
        return new ThrowableActionableResult<>(value, throwable);
    }

    @NotNull
    public static <T> ThrowableActionableResult<T> of(@NotNull T value) {
        Validate.notNull(value, "value cannot be null (Use #ofNullable)");
        return new ThrowableActionableResult<>(value);
    }

    @NotNull
    public static <T> ThrowableActionableResult<T> of() { return empty(); }

    @NotNull
    public static <T> ThrowableActionableResult<T> ofNullable(@Nullable T value) {
        return value == null ? empty() : of(value);
    }

    @NotNull
    public final ThrowableActionableResult<T> throwIfAny() {
        SneakyThrow.sneaky(throwable);
        return this;
    }

    @Override
    public @NotNull <U> ThrowableActionableResult<U> map(@NotNull Function<? super T, ? extends U> function) {
        return new ThrowableActionableResult<>(super.map(function).value, throwable);
    }

    @Override
    public @NotNull <U> ThrowableActionableResult<U> flatMap(@NotNull Function<? super T, ActionableResult<U>> function) {
        return new ThrowableActionableResult<>(super.flatMap(function).value, throwable);
    }

    @Override
    public @NotNull ThrowableActionableResult<T> filter(@NotNull Predicate<? super T> predicate) {
        return new ThrowableActionableResult<>(super.filter(predicate).value, throwable);
    }

    /**
     * Gets throwable for this result. May be null.
     * @return a throwable
     */
    @Nullable
    public Throwable getThrowable() { return throwable; }
}
