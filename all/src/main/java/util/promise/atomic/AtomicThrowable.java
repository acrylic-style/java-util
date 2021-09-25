package util.promise.atomic;

import com.google.common.util.concurrent.AtomicDouble;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.SneakyThrow;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * AtomicThrowable makes easier to use Throwable on AtomicReference.
 */
public class AtomicThrowable extends AtomicReference<Throwable> {
    public AtomicThrowable() { super(); }
    public AtomicThrowable(@Nullable Throwable throwable) { super(throwable); }

    @NotNull
    public AtomicThrowable throwIfSet() {
        if (this.get() != null) SneakyThrow.sneaky(this.get());
        return this;
    }

    @NotNull
    public AtomicThrowable throwAsRuntimeExceptionIfSet() {
        Throwable throwable = this.get();
        if (throwable != null) {
            if (throwable instanceof RuntimeException) throw (RuntimeException) throwable;
            if (throwable instanceof Error) throw (Error) throwable;
            throw new RuntimeException(throwable);
        }
        return this;
    }

    public void orElseThrow(@NotNull Supplier<? extends Throwable> supplier) { if (get() == null) SneakyThrow.sneaky(supplier.get()); }

    public <T> T orElseReturn(@NotNull Supplier<T> supplier) { return supplier.get(); }

    public <T> T orElseReturn(@NotNull AtomicReference<T> atomic) { return atomic.get(); }

    public boolean orElseReturn(@NotNull AtomicBoolean atomic) { return atomic.get(); }

    public int orElseReturn(@NotNull AtomicInteger atomic) { return atomic.get(); }

    public long orElseReturn(@NotNull AtomicLong atomic) { return atomic.get(); }

    public double orElseReturn(@NotNull AtomicDouble atomic) { return atomic.get(); }
}
