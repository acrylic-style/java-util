package util.function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Collection;
import util.ICollection;

public abstract class DelegatingThrowableSupplier<T> implements ThrowableSupplier<T> {
    @Nullable private ICollection.NullableEntry<T, Throwable> result;

    @NotNull
    protected abstract ThrowableSupplier<T> delegate();

    @Nullable
    @Override
    public final T get() {
        return result == null ? (result = ThrowableSupplier.super.entry()).getKey() : result.getKey();
    }

    @NotNull
    @Override
    public final ICollection.NullableEntry<T, Throwable> entry() {
        return result == null ? result = ThrowableSupplier.super.entry() : result;
    }

    @Override
    public final T evaluate() throws Throwable { return delegate().evaluate(); }

    public final void unregister() { remove(this.delegate()); }

    @NotNull
    private static final Collection<ThrowableSupplier<?>, DelegatingThrowableSupplier<?>> cache = new Collection<>();

    @SuppressWarnings("unchecked")
    @NotNull
    public static <T> DelegatingThrowableSupplier<T> getInstance(@NotNull ThrowableSupplier<T> supplier) {
        if (cache.containsKey(supplier)) return (DelegatingThrowableSupplier<T>) cache.get(supplier);
        DelegatingThrowableSupplier<T> instance = new DelegatingThrowableSupplier<T>() {
            @Override
            protected @NotNull ThrowableSupplier<T> delegate() {
                return supplier;
            }
        };
        cache.add(supplier, instance);
        return instance;
    }

    public static void remove(@NotNull ThrowableSupplier<?> supplier) {
        cache.remove(supplier);
    }
}
